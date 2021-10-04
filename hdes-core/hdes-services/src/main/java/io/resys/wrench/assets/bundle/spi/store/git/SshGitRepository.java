package io.resys.wrench.assets.bundle.spi.store.git;

import java.io.ByteArrayOutputStream;

/*-
 * #%L
 * wrench-component-assets-persistence
 * %%
 * Copyright (C) 2016 - 2017 Copyright 2016 ReSys OÜ
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.EmptyCommitException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig.Host;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.AndTreeFilter;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.PathFilterGroup;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.eclipse.jgit.util.FS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import io.resys.hdes.client.spi.util.FileUtils;
import io.resys.wrench.assets.bundle.spi.exceptions.AssetErrorCodes;
import io.resys.wrench.assets.bundle.spi.store.git.AssetAuthorProvider.Author;

public class SshGitRepository implements GitRepository {
  private static final Logger LOGGER = LoggerFactory.getLogger(SshGitRepository.class);
  private static final String TAG_PREFIX = "refs/tags/";
  private final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
  private final Git git;
  private final String workingDir;
  private final String message;
  private final Author committer;
  private final String path;
  private final String basePath; 
  private final TransportConfigCallback transportConfigCallback;
  private final AssetAuthorProvider gitAuthorProvider;
  private final Map<String, ObjectId> tags = new HashMap<>();
  

  public SshGitRepository(GitConfig gitConfigBean, AssetAuthorProvider gitAuthorProvider) 
  throws IOException, InvalidRemoteException, TransportException, GitAPIException {
    
    message = gitConfigBean.getMessage();
    this.gitAuthorProvider = gitAuthorProvider;
    committer = new Author(gitConfigBean.getEmail().split("@")[0], gitConfigBean.getEmail());
    path = FileUtils.cleanPath(gitConfigBean.getPath());

    Path path = StringUtils.isEmpty(gitConfigBean.getRepositoryPath()) ? Files.createTempDirectory("git_repo") : new File(gitConfigBean.getRepositoryPath()).toPath();
    File privateKey = copy(path, gitConfigBean.getPrivateKey(), "id_rsa", "Define git respository private key for assets");
    File knownHosts = copy(path, gitConfigBean.getPrivateKey() + ".known_hosts", "id_rsa.known_hosts", "Define git respository known hosts for assets");
    SshSessionFactory sshSessionFactory = createSshSessionFactory(privateKey, knownHosts);

    this.transportConfigCallback = createTransportConfigCallback(sshSessionFactory);

    File clone = new File(path + "/clone");
    try {
      if(clone.exists()) {
        LOGGER.debug("Checking out branch: {}", gitConfigBean.getBranchSpecifier());
  
        git = Git.open(clone);
        git.checkout().setName(gitConfigBean.getBranchSpecifier()).call();
        git.pull().setTransportConfigCallback(transportConfigCallback).call();
      } else {
        LOGGER.debug("Cloning new repository branch: {}", gitConfigBean.getBranchSpecifier());
        git = Git.cloneRepository().
            setURI(gitConfigBean.getRepositoryUrl()).
            setDirectory(new File(path + "/clone")).
            setBranch(gitConfigBean.getBranchSpecifier()).
            setTransportConfigCallback(transportConfigCallback).call();
      } 
      
      this.basePath = git.getRepository().getWorkTree().getAbsolutePath();
      
      LOGGER.debug("Checking out into: {}", basePath);
      workingDir = "/" + FileUtils.cleanPath(basePath) + "/" + FileUtils.cleanPath(gitConfigBean.getPath());
      git.getRepository().getRefDatabase().getRefsByPrefix(Constants.R_TAGS);
    } catch (Exception e) {
      throw new RuntimeException(String.format("Error fetching from git, branchSpecifier: %s, repositoryUrl: %s, message: %s",
          gitConfigBean.getBranchSpecifier(),
          gitConfigBean.getRepositoryUrl(),
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

  protected File copy(Path path, String src, String target, String errorMsg) throws IOException {
    File result = new File(path.toFile(), target);
    LOGGER.debug("Reading private key from: " + src);
    Resource resource = resolver.getResource(src);
    Assert.isTrue(resource.exists(), () -> errorMsg + ": " + src);
    InputStream stream = resource.getInputStream();
    LOGGER.debug("Writing private key to: " + result.getPath());
    if(result.exists()) {
      result.delete();
    }
    
    result.getParentFile().mkdirs();
    result.createNewFile();
    
    IOUtils.copy(stream, new FileOutputStream(result));
    stream.close();
    return result;
  }

  protected TransportConfigCallback createTransportConfigCallback(SshSessionFactory sshSessionFactory) {
    return transport -> ((SshTransport) transport).setSshSessionFactory(sshSessionFactory);
  }

  protected SshSessionFactory createSshSessionFactory(File privateKey, File knownHosts) {
    return new JschConfigSessionFactory() {
      @Override
      protected void configure(Host host, Session session) {
      }
      @Override
      protected JSch createDefaultJSch(FS fs) throws JSchException {
        JSch defaultJSch = new JSch();
        configureKnownHosts(defaultJSch, fs, knownHosts);

        defaultJSch.addIdentity(privateKey.getAbsolutePath());
        return defaultJSch;
      }
    };
  }

  protected void configureKnownHosts(JSch sch, FS fs, File knownHosts) throws JSchException {
    final File home = fs.userHome();
    if (home == null) {
      return;
    }
    try {
      final FileInputStream in = new FileInputStream(knownHosts);
      try {
        sch.setKnownHosts(in);
      } finally {
        in.close();
      }
    } catch (FileNotFoundException none) {
      // Oh well. They don't have a known hosts in home.
    } catch (IOException err) {
      // Oh well. They don't have a known hosts in home.
    }
  }
  
  private static class ImmutableContentTimestamps implements ContentTimestamps {
    private final Timestamp created;
    private final Timestamp modified;
    public ImmutableContentTimestamps(Timestamp created, Timestamp modified) {
      super();
      this.created = created;
      this.modified = modified;
    }
    public Timestamp getCreated() {
      return created;
    }
    public Timestamp getModified() {
      return modified;
    }
  }
}
