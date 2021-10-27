package io.resys.hdes.client.git.spi;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.EmptyCommitException;
import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.client.api.HdesStore;
import io.resys.hdes.client.api.ImmutableStoreEntity;
import io.resys.hdes.client.api.ast.AstBody.AstBodyType;
import io.resys.hdes.client.git.spi.GitConnectionFactory.GitConnection;
import io.resys.hdes.client.spi.staticresources.StoreEntityLocation;
import io.resys.hdes.client.spi.util.HdesAssert;
import io.smallrye.mutiny.Uni;


public class GitDataSourceImpl implements HdesStore {
  private static final Logger LOGGER = LoggerFactory.getLogger(GitDataSourceImpl.class);
  
  private final StoreEntityLocation location;
  private final GitConnection conn;
  private final ObjectMapper objectMapper;
  private final GitCredsSupplier creds;
  
  interface FileMarker {
    String getAbsolutePath();
  }
  
  public interface GitCredsSupplier {
    String getUser();
    String getEmail();
  }
  
  public GitDataSourceImpl(StoreEntityLocation location, GitConnection conn, ObjectMapper objectMapper, GitCredsSupplier creds) {
    super();
    this.location = location;
    this.conn = conn;
    this.objectMapper = objectMapper;
    this.creds = creds;
  }
  
  @Override
  public Uni<StoreEntity> create(CreateAstType newType) {
    
    return Uni.createFrom().item(() -> {
      try {
        final var resourceName = location.getAbsolutePath(newType.getBodyType(), newType.getId());
        final var assetName = resourceName.startsWith("file:") ? resourceName.substring(5) : resourceName;
        
        File outputFile = new File(assetName);
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
    
        // copy data to file
        final StoreEntity src = ImmutableStoreEntity.builder()
            .id(newType.getId())
            .body(newType.getBody())
            .bodyType(newType.getBodyType())
            .build();
        
        
        final var blob = objectMapper.writeValueAsString(src);
        final var fileOutputStream = new FileOutputStream(outputFile);
        try {
          IOUtils.copy(new ByteArrayInputStream(blob.getBytes(StandardCharsets.UTF_8)), fileOutputStream);
        } catch(Exception e) {
          throw new RuntimeException(e.getMessage(), e);
        } finally {
          fileOutputStream.close();          
        }
        
        final var created = Timestamp.valueOf(LocalDateTime.now());
        final var gitEntry = ImmutableGitEntry.builder()
          .id(newType.getId())
          .modified(created)
          .created(created)
          .revision("")
          .bodyType(newType.getBodyType())
          .treeValue(conn.getAssetsPath() + outputFile.getName())
          .blobValue(blob)
          .build();
        
        
        push(gitEntry);

        return src;
      } catch(Exception e) {
        LOGGER.error("Failed to create new asset because:" + e.getMessage(), e);
        throw new RuntimeException(e.getMessage(), e);
      }
    });
  }
  @Override
  public Uni<StoreEntity> update(UpdateAstType updateType) {
    // TODO Auto-generated method stub
    return null;
  }
  @Override
  public Uni<StoreEntity> delete(DeleteAstType deleteType) {
    // TODO Auto-generated method stub
    return null;
  }
  @Override
  public QueryBuilder query() {
    final var cache = conn.getCacheManager().getCache(conn.getCacheName(), String.class, GitEntry.class);
    return null;
  }
  
  public void push(GitEntry gitEntry) {
    final var cache = conn.getCacheManager().getCache(conn.getCacheName(), String.class, GitEntry.class);
    final var git = conn.getClient();
    final var callback = conn.getCallback();
    
    try {
      // pull
      git.pull().setTransportConfigCallback(callback).call().getFetchResult();
      LOGGER.debug("Pulling...");
      
      // add new files
      git.add().addFilepattern(gitEntry.getTreeValue()).call();


      // commit changes
      git.commit()
      .setAll(true)
      .setAllowEmpty(false)
      .setMessage("Changes to: " + gitEntry.getBodyType() + " file: " + gitEntry.getTreeValue())
      .setAuthor(creds.getUser(), creds.getEmail())
      .setCommitter(creds.getUser(), creds.getEmail())
      .call();

      // push
      git.push().setTransportConfigCallback(callback).call();

      cache.put(gitEntry.getId(), gitEntry);
      
      LOGGER.debug("Commit and push success");
      
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
  }
  
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String remote;
    private String branch;
    private String storage;
    private String sshPath;
    private ObjectMapper objectMapper;
    private GitCredsSupplier creds;
    
    public GitDataSourceImpl build() {
      HdesAssert.notNull(objectMapper, () -> "objectMapper must be defined!");
      HdesAssert.notNull(creds, () -> "creds must be defined!");
      
      
      if(LOGGER.isDebugEnabled()) {
        final var log = new StringBuilder()
            .append(System.lineSeparator())
            .append("Configuring GIT: ")
            .append("  remote: '").append(remote).append("'").append(System.lineSeparator())
            .append("  branch: '").append(branch).append("'").append(System.lineSeparator())
            .append("  storage: '").append(storage).append("'").append(System.lineSeparator())
            .append("  sshPath: '").append(sshPath).append("'").append(System.lineSeparator());
        LOGGER.debug(log.toString());
      }

      HdesAssert.notEmpty(remote, () -> "remote must be defined! Example of the remote: 'ssh://git@git.resys.io:22222/wrench/wrench-demo-assets.git'");
      HdesAssert.notEmpty(branch, () -> "branch must be defined! Example of the branch: 'main'");
      HdesAssert.notEmpty(storage, () -> "storage must be defined! Storage is the path to what to clone the repository, example: '/home/olev/Development/wrench-demo-assets'");
      HdesAssert.notEmpty(sshPath, () -> "sshPath must be defined! SshPath must contain 2 files(classpath or folder), private key and known host: 'id_rsa', 'id_rsa.known_hosts'. Classpath example: 'classpath:ssh/id_rsa'");
      
      final var init = ImmutableGitInit.builder().branch(branch).remote(remote).storage(storage).sshPath(sshPath).build();
      final GitConnection conn;
      try {
        conn = GitConnectionFactory.create(init);
      } catch(Exception e) {
        throw new RuntimeException(e.getMessage(), e);
      }
      final var location = new StoreEntityLocation(conn.getAbsoluteAssetsPath());

      if(LOGGER.isDebugEnabled()) {
        final var log = new StringBuilder()
            .append(System.lineSeparator())
            .append("Configured GIT: ")
            .append("  cloned into directory: '").append(conn.getParentPath()).append("'").append(System.lineSeparator())
            .append("  working directory: '").append(conn.getAbsolutePath()).append("'").append(System.lineSeparator())
            .append("  assets directory: '").append(conn.getAbsoluteAssetsPath()).append("'").append(System.lineSeparator())
            .append("  relative assets directory: '").append(conn.getAssetsPath()).append("'").append(System.lineSeparator())
            .append("  cache name: '").append(conn.getCacheName()).append("'").append(System.lineSeparator())
            .append("  cache size/heap: '").append(conn.getCacheHeap()).append("'").append(System.lineSeparator());
        LOGGER.debug(log.toString());
      }

      // load assets
      final var cache = conn.getCacheManager().getCache(conn.getCacheName(), String.class, GitEntry.class);
      try(final var loader = new GitDataSourceLoader(conn, location)) {
        loader.read().forEach(e -> cache.put(e.getId(), e));
      } catch (Exception e) {
        throw new RuntimeException(e.getMessage(), e);
      };
      
      return new GitDataSourceImpl(location, conn, objectMapper, creds);
    }
  }
  
  @Value.Immutable
  public interface GitEntry {
    String getId();
    Timestamp getCreated();
    Timestamp getModified();
    AstBodyType getBodyType();
    String getRevision();
    String getTreeValue();
    String getBlobValue();
  }
}
