package io.resys.hdes.client.git.spi;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.EmptyCommitException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.ehcache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.resys.hdes.client.api.HdesStore.CreateStoreEntity;
import io.resys.hdes.client.api.ast.AstBody.AstBodyType;
import io.resys.hdes.client.api.ast.AstCommand;
import io.resys.hdes.client.git.spi.GitDataSourceLoader.GitFile;
import io.resys.hdes.client.git.spi.GitDataSourceLoader.GitFileReload;
import io.resys.hdes.client.git.spi.connection.GitConnection;
import io.resys.hdes.client.git.spi.connection.GitConnection.GitEntry;
import io.resys.hdes.client.spi.staticresources.Sha2;
import io.resys.hdes.client.spi.util.HdesAssert;

public class GitFiles {
  private static final Logger LOGGER = LoggerFactory.getLogger(GitFiles.class);
  private final GitConnection conn;
  
  public GitFiles(GitConnection connection) {
    super();
    this.conn = connection;
  }
  
  public List<GitFileReload> push(GitFile gitFile) {
    final var git = conn.getClient();
    final var repo = git.getRepository();    
    final var callback = conn.getCallback();
    final var creds = conn.getCreds().get();
    try {
      final var start = repo.resolve(Constants.HEAD);
      
      // pull
      git.pull().setTransportConfigCallback(callback).call().getFetchResult();
      
      // add new files
      git.add().addFilepattern(gitFile.getTreeValue()).call();

      // commit changes
      git.commit()
      .setAll(true)
      .setAllowEmpty(false)
      .setMessage("Changes to: " + gitFile.getBodyType() + " file: " + gitFile.getTreeValue())
      .setAuthor(creds.getUser(), creds.getEmail())
      .setCommitter(creds.getUser(), creds.getEmail())
      .call();

      // push
      git.push().setTransportConfigCallback(callback).call();
      final var end = repo.resolve(Constants.HEAD);
      return diff(start, end);
      
    } catch(CheckoutConflictException e) {
      LOGGER.error("Conflict, resetting... " +  e.getMessage(), e);
      try {
        git.reset().setMode(ResetType.HARD).call();
        git.pull().setTransportConfigCallback(callback).call();
      } catch(Exception ex) {
        LOGGER.error(e.getMessage(), e);
        throw new RuntimeException(e.getMessage(), e);
      }
      throw new RuntimeException(e.getMessage(), e);
    } catch(EmptyCommitException e) {
      LOGGER.debug("nothing to commit");
    } catch(Exception e) {
      LOGGER.error(e.getMessage(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
    
    return Collections.emptyList();
  }
  
  public Map.Entry<String, List<GitFileReload>> tag(CreateStoreEntity entity) {
    final var git = conn.getClient();
    final var callback = conn.getCallback();
    final var creds = conn.getCreds().get();
    final var repo = git.getRepository();
    
    try {
      final var start = repo.resolve(Constants.HEAD);
      git.pull().setTransportConfigCallback(callback).call().getFetchResult();
      final var name = entity.getBody().iterator().next().getValue();
      
      Ref ref = git.tag().setName(name).setAnnotated(true)
          .setMessage("tag created")
          .setTagger(new PersonIdent(creds.getUser(), creds.getEmail()))
          .call();
      git.push().setPushTags().setTransportConfigCallback(callback).call();
      final var end = repo.resolve(Constants.HEAD);
      
      final var result = new ArrayList<>(diff(start, end));
      result.add(ImmutableGitFileReload.builder().treeValue(ref.getName()).build());
      return Map.entry(ref.getObjectId().getName(), result);
    } catch(Exception e) {
      LOGGER.error(e.getMessage(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }
  
  private List<GitFileReload> diff(ObjectId oldHead, ObjectId newHead) {
    final var git = conn.getClient();
    final var repo = git.getRepository();
    
    try (final var reader = repo.newObjectReader()) {
      final var result = new ArrayList<GitFileReload>();
      
      final var oldTreeIter = new CanonicalTreeParser();
      oldTreeIter.reset(reader, repo.parseCommit(oldHead).getTree());
      
      final var newTreeIter = new CanonicalTreeParser();
      newTreeIter.reset(reader, repo.parseCommit(newHead).getTree());

      // finally get the list of changed files
      final List<DiffEntry> diffs = git.diff().setNewTree(newTreeIter).setOldTree(oldTreeIter).call();
      for (final DiffEntry entry : diffs) {
        // example: src/main/resources/assets/flow/2d8958a2-44eb-4020-9c17-9bb83daa7434.json
        final var treeValue = entry.getOldPath();
        result.add(ImmutableGitFileReload.builder().treeValue(treeValue).build());
      }
      
      return result;
    } catch(Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }    
  }
  
  public GitFile create(AstBodyType bodyType, List<AstCommand> body) throws IOException {
    final var location = conn.getLocation();
    final var id = UUID.randomUUID().toString();
    final var resourceName = location.getAbsolutePath(bodyType, id);
    final var assetName = resourceName.startsWith("file:") ? resourceName.substring(5) : resourceName;
    final var outputFile = new File(assetName);
    
    if(outputFile.exists()) {
      throw new RuntimeException("Can't create asset: '" + assetName + "' because it's already created!");
    } else {
      outputFile.getParentFile().mkdirs();
      boolean created = outputFile.createNewFile();
      HdesAssert.isTrue(created, () -> "Failed to create new file: " + assetName);
    }
    
    if(LOGGER.isDebugEnabled()) {
      LOGGER.debug("Created new file: " + outputFile.getCanonicalPath());
    }
    
    final var blob = conn.getSerializer().write(body);
    final var fileOutputStream = new FileOutputStream(outputFile);
    try {
      IOUtils.copy(new ByteArrayInputStream(blob.getBytes(StandardCharsets.UTF_8)), fileOutputStream);
    } catch(Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    } finally {
      fileOutputStream.close();          
    }
    
    return ImmutableGitFile.builder()
        .id(id)
        .treeValue(conn.getAssetsPath() +  location.getFileName(bodyType, id))
        .blobValue(blob)
        .blobHash(Sha2.blob(blob))
        .bodyType(bodyType)
        .build();
  }
  
  public GitFile update(String id, AstBodyType bodyType, List<AstCommand> body) throws IOException {
    final var location = conn.getLocation();
    final var resourceName = location.getAbsolutePath(bodyType, id);
    final var assetName = resourceName.startsWith("file:") ? resourceName.substring(5) : resourceName;
    final var outputFile = new File(assetName);
    
    if(!outputFile.exists()) {
      throw new RuntimeException("Can't update asset: '" + assetName + "' because it does not exist!"); 
    }
     
    final var blob = conn.getSerializer().write(body);
    final var fileOutputStream = new FileOutputStream(outputFile);
    try {
      IOUtils.copy(new ByteArrayInputStream(blob.getBytes(StandardCharsets.UTF_8)), fileOutputStream);
    } catch(Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    } finally {
      fileOutputStream.close();          
    }
    
    return ImmutableGitFile.builder()
        .id(id)
        .treeValue(conn.getAssetsPath() +  location.getFileName(bodyType, id))
        .blobValue(blob)
        .bodyType(bodyType)
        .blobHash(Sha2.blob(blob))
        .build();
  }
  
  public List<GitFileReload> delete(String id) throws IOException {
    
    final Cache<String, GitEntry> cache = conn.getCacheManager().getCache(conn.getCacheName(), String.class, GitEntry.class);
    final GitEntry gitFile = cache.get(id);
    final var bodyType = gitFile.getBodyType();
    
    final var git = conn.getClient();
    final var callback = conn.getCallback();
    final var creds = conn.getCreds().get();
    final var repo = git.getRepository();
    final var start = repo.resolve(Constants.HEAD);
    final List<GitFileReload> reload = new ArrayList<>();

    try {
      if(bodyType == AstBodyType.TAG) {
        final String filter = gitFile.getTreeValue();
        final Optional<Ref> target = git.tagList().call().stream()
          .filter(ref -> ref.getName().equals(filter))
          .findFirst();
        
        if(!target.isPresent()) {
          final String msg = "Can't find tag: " + filter + "!";
          LOGGER.error(msg);
          throw new RuntimeException(msg);
        }
  
        
        git.tagDelete().setTags(target.get().getName()).call();
    
        //delete branch 'branchToDelete' on remote 'origin'
        RefSpec refSpec = new RefSpec()
                .setSource(null)
                .setDestination(target.get().getName());
        git.push().setRefSpecs(refSpec).setRemote("origin").setTransportConfigCallback(conn.getCallback()).call();
        
        reload.add(ImmutableGitFileReload.builder().treeValue(gitFile.getTreeValue()).build());
      } else {
        // pull
        git.pull().setTransportConfigCallback(callback).call().getFetchResult();
        
        final var location = conn.getLocation();
        final var resourceName = location.getAbsolutePath(bodyType, id);
        
        LOGGER.debug("Removing assets from git: " + resourceName + "");
        final var file = new File(URI.create(resourceName));
        
        boolean deleted = file.delete();
        if(!deleted) {
          throw new RuntimeException("Cant delete assets from git: " + resourceName + "");
        }
        
        // add new files
        git.add().addFilepattern(gitFile.getTreeValue()).call();

        // commit changes
        git.commit()
        .setAll(true)
        .setAllowEmpty(false)
        .setMessage("Delete: " + gitFile.getBodyType() + " file: " + gitFile.getTreeValue())
        .setAuthor(creds.getUser(), creds.getEmail())
        .setCommitter(creds.getUser(), creds.getEmail())
        .call();

        // push
        git.push().setTransportConfigCallback(callback).call();
      }
    } catch(Exception e) {
      try {
        git.reset().setMode(ResetType.HARD).call();
      } catch (GitAPIException e1) {
        throw new RuntimeException(e.getMessage(), e);
      }
    }
    
    final var end = repo.resolve(Constants.HEAD);
    reload.addAll(diff(start, end));
    return reload;
  }
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static class Builder {
    private GitConnection conn;
    
    public Builder git(GitConnection conn) {
      this.conn = conn;
      return this;
    }
    
    public GitFiles build() {
      HdesAssert.notNull(conn, () -> "git connection must be defined!");
      return new GitFiles(conn);
    }
  }
}
