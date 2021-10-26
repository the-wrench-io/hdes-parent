package io.resys.hdes.client.git.spi;

import java.sql.Timestamp;

import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.resys.hdes.client.api.HdesStore;
import io.resys.hdes.client.api.ast.AstBody.AstBodyType;
import io.resys.hdes.client.git.spi.GitConnectionFactory.GitConnection;
import io.resys.hdes.client.spi.staticresources.StoreEntityLocation;
import io.resys.hdes.client.spi.util.HdesAssert;
import io.smallrye.mutiny.Uni;

public class GitDataSourceImpl implements HdesStore {
  private static final Logger LOGGER = LoggerFactory.getLogger(GitDataSourceImpl.class);
  
  private final StoreEntityLocation location;
  private final GitConnection conn;

  public GitDataSourceImpl(StoreEntityLocation location, GitConnection conn) {
    super();
    this.location = location;
    this.conn = conn;
  }
  @Override
  public CreateBuilder create() {
    return new CreateBuilder() {
      
      @Override
      public Uni<StoreEntity> service(String id) {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public Uni<StoreEntity> flow(String id) {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public Uni<StoreEntity> decision(String id) {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public Uni<StoreEntity> build(CreateAstType newType) {
        // TODO Auto-generated method stub
        return null;
      }
    };
  }
  @Override
  public QueryBuilder query() {
    // TODO Auto-generated method stub
    return null;
  }
  @Override
  public DeleteBuilder delete() {
    // TODO Auto-generated method stub
    return null;
  }
  @Override
  public UpdateBuilder update() {
    // TODO Auto-generated method stub
    return null;
  }
  
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String remote;
    private String branch;
    private String storage;
    private String sshPath;
    
    public GitDataSourceImpl build() {
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
      
      return new GitDataSourceImpl(location, conn);
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
