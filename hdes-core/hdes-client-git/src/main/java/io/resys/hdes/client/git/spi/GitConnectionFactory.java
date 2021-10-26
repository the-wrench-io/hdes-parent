package io.resys.hdes.client.git.spi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig.Host;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.util.FS;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import io.resys.hdes.client.git.spi.GitDataSourceImpl.GitEntry;
import io.resys.hdes.client.spi.util.FileUtils;
import io.resys.hdes.client.spi.util.HdesAssert;

public class GitConnectionFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(GitConnectionFactory.class);
  
  @Value.Immutable
  public interface GitInit {
    String getBranch();
    String getRemote();
    String getSshPath();
    String getStorage();
  }
  
  @Value.Immutable
  public interface GitConnection {
    GitInit getInit();
    
    CacheManager getCacheManager();
    String getCacheName();
    Integer getCacheHeap();
    
    Path getParentPath();           // path where git repository is cloned
    String getAbsolutePath();       // git working directory path
    String getAbsoluteAssetsPath(); // absolute path for assets in the git working directory 
    TransportConfigCallback getCallback();
    Git getClient();
  }
  
  public static GitConnection create(GitInit config) throws IOException, 
      RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
    
    final var path = StringUtils.isEmpty(config.getRemote()) ? Files.createTempDirectory("git_repo") : new File(config.getRemote()).toPath();
    final var resolver = new PathMatchingResourcePatternResolver();
    final var privateKey = copyKey(resolver, path, config.getSshPath(), "id_rsa", "Define git respository private key for assets");
    final var knownHosts = copyKey(resolver, path, config.getSshPath() + ".known_hosts", "id_rsa.known_hosts", "Define git respository known hosts for assets");
    final var sshSessionFactory = createSshSessionFactory(privateKey, knownHosts);
    final var clone = new File(path + "/clone");

    final TransportConfigCallback callback = transport -> ((SshTransport) transport).setSshSessionFactory(sshSessionFactory);
    final Git git;

    if(clone.exists()) {
      LOGGER.debug("Checking out branch: {}", config.getBranch());
      git = Git.open(clone);
      git.checkout().setName(config.getBranch()).call();
      git.pull().setTransportConfigCallback(callback).call();
    } else {
      LOGGER.debug("Cloning new repository branch: {}", config.getBranch());
      git = Git.cloneRepository().
          setURI(config.getRemote()).
          setDirectory(new File(path + "/clone")).
          setBranch(config.getBranch()).
          setTransportConfigCallback(callback).call();
    }

    // git 
    git.getRepository().getRefDatabase().getRefsByPrefix(Constants.R_TAGS);
    
    final var absolutePath = git.getRepository().getWorkTree().getAbsolutePath();
    final var assetPath = "/" + FileUtils.cleanPath(absolutePath) + "/src/main/resources/assets/";
    
    // init cache
    final var cacheName = "hdesGitCache";
    final var cacheHeap = 200;
    final var cacheManager = CacheManagerBuilder.newCacheManagerBuilder() 
        .withCache(cacheName,
            CacheConfigurationBuilder.newCacheConfigurationBuilder(
                String.class, GitEntry.class, 
                ResourcePoolsBuilder.heap(cacheHeap))) 
        .build(); 
    cacheManager.init();
    
    return ImmutableGitConnection.builder()
        .init(config)
        .client(git)
        .cacheManager(cacheManager)
        .cacheName(cacheName)
        .cacheHeap(cacheHeap)
        .parentPath(path)
        .callback(callback)
        .absolutePath(absolutePath)
        .absoluteAssetsPath(assetPath)
        .build();
  }
  
  private static File copyKey(ResourcePatternResolver resolver, Path path, String src, String target, String errorMsg) throws IOException {
    File result = new File(path.toFile(), target);
    LOGGER.debug("Reading private key from: " + src);
    Resource resource = resolver.getResource(src);
    HdesAssert.isTrue(resource.exists(), () -> errorMsg + ": " + src);
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

  private static SshSessionFactory createSshSessionFactory(File privateKey, File knownHosts) {
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

  private static void configureKnownHosts(JSch sch, FS fs, File knownHosts) throws JSchException {
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
}
