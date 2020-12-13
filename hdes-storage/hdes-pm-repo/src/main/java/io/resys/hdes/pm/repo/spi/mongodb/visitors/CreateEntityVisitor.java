package io.resys.hdes.pm.repo.spi.mongodb.visitors;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;

import io.resys.hdes.pm.repo.api.PmRepository.Access;
import io.resys.hdes.pm.repo.api.PmRepository.Project;
import io.resys.hdes.pm.repo.api.PmRepository.User;
import io.resys.hdes.pm.repo.spi.mongodb.ImmutablePersistedEntities;
import io.resys.hdes.pm.repo.spi.mongodb.PersistentCommand.EntityVisitor;
import io.resys.hdes.pm.repo.spi.mongodb.PersistentCommand.MongoDbConfig;
import io.resys.hdes.pm.repo.spi.support.RepoAssert;

public class CreateEntityVisitor implements EntityVisitor {

  private final MongoClient client;
  private final MongoDbConfig config;
  private final ImmutablePersistedEntities.Builder collect;
  
  public CreateEntityVisitor(MongoClient client, MongoDbConfig config, ImmutablePersistedEntities.Builder collect) {
    super();
    this.client = client;
    this.config = config;
    this.collect = collect;
  }

  public static Builder builder() {
    return new Builder();
  }
    
  public static class Builder {
    private ImmutablePersistedEntities.Builder collect;
    private MongoDbConfig config;
    private MongoClient client;
    
    public Builder collect(ImmutablePersistedEntities.Builder collect) {
      this.collect = collect;
      return this;
    }
    public Builder client(MongoClient client) {
      this.client = client;
      return this;
    }  
    public Builder config(MongoDbConfig config) {
      this.config = config;
      return this;
    }
    public CreateEntityVisitor build() {
      RepoAssert.notNull(collect, () -> "collect not defined!");
      RepoAssert.notNull(client, () -> "client not defined!");
      RepoAssert.notNull(config, () -> "config not defined!");
      
      return new CreateEntityVisitor(client, config, collect);
    }
  }

  @Override
  public Project visitProject(Project project) {
    final MongoCollection<Project> collection = client
        .getDatabase(config.getDb())
        .getCollection(config.getProjects(), Project.class);
    
    collection.insertOne(project);
    collect.putProject(project.getId(), project);
    return project;
  }

  @Override
  public Access visitAccess(Access access) {
    final MongoCollection<Access> collection = client
        .getDatabase(config.getDb())
        .getCollection(config.getAccess(), Access.class);
    
    collection.insertOne(access);
    collect.putAccess(access.getId(), access);
    return access;
  }

  @Override
  public User visitUser(User user) {
    final MongoCollection<User> collection = client
        .getDatabase(config.getDb())
        .getCollection(config.getUsers(), User.class);
    
    collection.insertOne(user);
    collect.putUser(user.getId(), user);
    return user;
  }
}
