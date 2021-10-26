package io.resys.hdes.client.git.integration;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.EmptyCommitException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.AndTreeFilter;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.PathFilterGroup;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.resys.hdes.client.git.GitDataSource;
import io.resys.hdes.client.git.spi.GitConnectionFactory;
import io.resys.wrench.assets.bundle.spi.exceptions.AssetErrorCodes;
import io.resys.wrench.assets.bundle.spi.store.git.AssetAuthorProvider.Author;

public class GitIntegrationImpl implements GitDataSource {
  private static final Logger LOGGER = LoggerFactory.getLogger(GitIntegrationImpl.class);
  private static final String TAG_PREFIX = "refs/tags/";
  
  private final GitDataSource dataSource;
  private final Map<String, ObjectId> tags = new HashMap<>();
  
  public GitIntegrationImpl(GitIntegrationConfig config) {
    try {
      this.dataSource = GitConnectionFactory.create(config);
      this.dataSource.getUnwrapped().getRepository().getRefDatabase().getRefsByPrefix(Constants.R_TAGS);
    } catch (Exception e) {
      throw new RuntimeException(String.format("Error fetching from git, branch: %s, remote: %s, message: %s",
          config.getBranch(),
          config.getRemote(),
          e.getMessage()), e);
    }
  }

  @Override
  public ContentTimestamps getTimestamps(String file, boolean isTag)  {
    Repository repository = git.getRepository();
    
    try (RevWalk revWalk = new RevWalk(repository)) {
      if(isTag) {
        final RevCommit commit = revWalk.parseCommit(this.tags.get(file));
        Timestamp created = new Timestamp(commit.getCommitTime() * 1000L);
        return new ImmutableContentTimestamps(created, created);
      } else {
        final RevCommit commit = revWalk.parseCommit( repository.resolve( Constants.HEAD ));
        final String filter = file.substring(file.indexOf(basePath) + basePath.length()+1);
        final TreeFilter treeFilter = AndTreeFilter.create(
            PathFilterGroup.createFromStrings(filter), 
            TreeFilter.ANY_DIFF);
        
        revWalk.markStart(commit);
        revWalk.setTreeFilter(treeFilter);
        revWalk.sort(RevSort.COMMIT_TIME_DESC);
        Timestamp modified = new Timestamp(revWalk.next().getCommitTime() * 1000L);
        
        revWalk.reset();
        revWalk.markStart(commit);
        revWalk.setTreeFilter(treeFilter);
        revWalk.sort(RevSort.COMMIT_TIME_DESC);
        revWalk.sort(RevSort.REVERSE, true);
        Timestamp created = new Timestamp(revWalk.next().getCommitTime() * 1000L);
        
        return new ImmutableContentTimestamps(created, modified);
      }
    } catch (Exception e) {
      LOGGER.error("Can't resolve timestamps for: " + file + System.lineSeparator() + e.getMessage(), e);
      Timestamp now = new Timestamp(System.currentTimeMillis());
      return new ImmutableContentTimestamps(now, now);
    }
  }

  @Override
  public void push() {
    try {
      // pull
      git.pull().setTransportConfigCallback(transportConfigCallback).call();
      LOGGER.debug("Pulling...");
      
      // add new files
      git.add().addFilepattern(path).call();

      Author author = gitAuthorProvider.get();

      // commit changes
      git.commit()
      .setAll(true)
      .setAllowEmpty(false)
      .setMessage(message)
      .setAuthor(author.getUser(), author.getEmail())
      .setCommitter(committer.getUser(), committer.getEmail())
      .call();

      // push
      git.push().setTransportConfigCallback(transportConfigCallback).call();

      LOGGER.debug("Commit and push success");
      
    } catch(CheckoutConflictException e) {
      LOGGER.error("Conflict, resetting... " +  e.getMessage(), e);
      try {
        git.reset().setMode(ResetType.HARD).call();
        git.pull().setTransportConfigCallback(transportConfigCallback).call();
      } catch(Exception ex) {
        LOGGER.error(e.getMessage(), e);
        throw new RuntimeException(e.getMessage(), e);
      }

      throw AssetErrorCodes.GIT_CONFLICT.newException(e.getMessage());
    } catch(EmptyCommitException e) {
      LOGGER.debug("nothing to commit");
    } catch(Exception e) {
      LOGGER.error(e.getMessage(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }
  
  @Override
  public void pushTag(String name, String annotation) {
    try {
      Ref ref = git.tag().setName(name).setAnnotated(true).setMessage(annotation).call();
      git.push().setPushTags().setTransportConfigCallback(transportConfigCallback).call();
      this.tags.put(name, ref.getObjectId());
      
      LOGGER.debug("Tag create and push success, {}", ref.getObjectId());
      
    } catch(CheckoutConflictException e) {
      LOGGER.error("Conflict, resetting... " +  e.getMessage(), e);
      try {
        git.reset().setMode(ResetType.HARD).call();
        git.pull().setTransportConfigCallback(transportConfigCallback).call();
      } catch(Exception ex) {
        LOGGER.error(e.getMessage(), e);
        throw new RuntimeException(e.getMessage(), e);
      }

      throw AssetErrorCodes.GIT_CONFLICT.newException(e.getMessage());
    } catch(EmptyCommitException e) {
      LOGGER.debug("nothing to commit");
    } catch(Exception e) {
      LOGGER.error(e.getMessage(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }
  

  @Override
  public void deleteTag(String name) {
    try {
      final String filter = TAG_PREFIX + name;
      
      Optional<Ref> target = git.tagList().call().stream()
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
      git.push().setRefSpecs(refSpec).setRemote("origin").setTransportConfigCallback(transportConfigCallback).call();
      
      this.tags.remove(name);
      
      LOGGER.debug("Tag delete and push success");
      
    } catch(CheckoutConflictException e) {
      LOGGER.error("Conflict, resetting... " +  e.getMessage(), e);
      try {
        git.reset().setMode(ResetType.HARD).call();
        git.pull().setTransportConfigCallback(transportConfigCallback).call();
      } catch(Exception ex) {
        LOGGER.error(e.getMessage(), e);
        throw new RuntimeException(e.getMessage(), e);
      }

      throw AssetErrorCodes.GIT_CONFLICT.newException(e.getMessage());
    } catch(EmptyCommitException e) {
      LOGGER.debug("nothing to commit");
    } catch(Exception e) {
      LOGGER.error(e.getMessage(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }


  @Override
  public String getContent(String commitOrTag, String path) {
    try {
      Repository repository = git.getRepository();
      ObjectId lastCommitId = repository.resolve(commitOrTag);

      try (RevWalk revWalk = new RevWalk(repository)) {
        RevCommit commit = revWalk.parseCommit(lastCommitId);
        RevTree tree = commit.getTree();

        try (TreeWalk treeWalk = new TreeWalk(repository)) {
          treeWalk.addTree(tree);
          treeWalk.setRecursive(true);
          treeWalk.setFilter(PathFilter.create(this.path + path));
          if (!treeWalk.next()) {
            throw new IllegalStateException(String.format("Did not find expected file '%s'", path));
          }

          ObjectId objectId = treeWalk.getObjectId(0);
          ObjectLoader loader = repository.open(objectId);

          // and then one can the loader to read the file
          ByteArrayOutputStream out = new ByteArrayOutputStream();
          loader.copyTo(out);

          return out.toString(StandardCharsets.UTF_8.name());
        }
      }
    } catch(Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @Override
  public List<String> getRev(String path) {
    try {

      List<String> result = new ArrayList<>();
      git.log()
      .add(git.getRepository().resolve(Constants.HEAD))
      .addPath(this.path + path)
      .call()
      .forEach(c -> result.add(c.getId().getName()));

      return Collections.unmodifiableList(result);
    } catch(Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @Override
  public List<String> getTags() {
    try {
      List<String> result = new ArrayList<>();
      for(Ref ref : git.tagList().call()) {        
        final String name = ref.getName().startsWith(TAG_PREFIX) ? 
            ref.getName().substring(TAG_PREFIX.length()) : 
            ref.getName();
        result.add(name);
        this.tags.put(name, ref.getObjectId());
      }

      return Collections.unmodifiableList(result);
    } catch(Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @Override
  public String getWorkingDir() {
    return "file:" + workingDir;
  }

  @Override
  public Git get() {
    return git;
  }

}
