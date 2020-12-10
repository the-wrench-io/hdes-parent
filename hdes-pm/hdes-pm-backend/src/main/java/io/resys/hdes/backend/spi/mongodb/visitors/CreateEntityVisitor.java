package io.resys.hdes.backend.spi.mongodb.visitors;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.InsertOneResult;

import io.resys.hdes.backend.api.PmRepository.Access;
import io.resys.hdes.backend.api.PmRepository.Project;
import io.resys.hdes.backend.api.PmRepository.User;
import io.resys.hdes.backend.spi.mongodb.ImmutablePersistedEntities;
import io.resys.hdes.backend.spi.mongodb.PersistentCommand.EntityVisitor;
import io.resys.hdes.backend.spi.mongodb.PersistentCommand.MongoDbConfig;
import io.resys.hdes.backend.spi.support.RepoAssert;

public class CreateEntityVisitor implements EntityVisitor {

  private final MongoClient client;
  private final MongoDbConfig config;
  private final ImmutablePersistedEntities.Builder collect;
  private final StringBuilder log = new StringBuilder("Writing transaction: ").append(System.lineSeparator());
  
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
    InsertOneResult inserted = collection.insertOne(project);
    
    collect.putProject(project.getId(), project);
    log.append("  - ").append(project.getName()).append(System.lineSeparator());
    return project;
  }

  @Override
  public Access visitAccess(Access access) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public User visitUser(User user) {
    // TODO Auto-generated method stub
    return null;
  }
}
