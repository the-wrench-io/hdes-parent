package io.resys.hdes.backend.spi.mongodb;

import io.resys.hdes.backend.api.PmRepository;
import io.resys.hdes.backend.api.commands.AccessCommands;
import io.resys.hdes.backend.api.commands.BatchProjectCommands;
import io.resys.hdes.backend.api.commands.ProjectCommands;
import io.resys.hdes.backend.api.commands.UserCommands;
import io.resys.hdes.backend.spi.mongodb.PersistentCommand.MongoDbConfig;
import io.resys.hdes.backend.spi.mongodb.commands.MongoAccessCommands;
import io.resys.hdes.backend.spi.mongodb.commands.MongoBatchProjectCommands;
import io.resys.hdes.backend.spi.mongodb.commands.MongoPersistentCommand;
import io.resys.hdes.backend.spi.mongodb.commands.MongoPersistentCommand.MongoTransaction;
import io.resys.hdes.backend.spi.mongodb.commands.MongoProjectCommands;
import io.resys.hdes.backend.spi.mongodb.commands.MongoUserCommands;
import io.resys.hdes.backend.spi.support.RepoAssert;

public class MongoPmRepository implements PmRepository {

  private final BatchProjectCommands batchProjectCommands;
  private final ProjectCommands projectCommands;
  private final UserCommands userCommands;
  private final AccessCommands accessCommands;

  public MongoPmRepository(
      BatchProjectCommands batchProjectCommands, 
      ProjectCommands projectCommands, 
      UserCommands userCommands,
      AccessCommands accessCommands) {
    super();
    this.batchProjectCommands = batchProjectCommands;
    this.projectCommands = projectCommands;
    this.userCommands = userCommands;
    this.accessCommands = accessCommands;
  }
  @Override
  public ProjectCommands projects() {
    return projectCommands;
  }
  @Override
  public UserCommands users() {
    return userCommands;
  }
  @Override
  public AccessCommands access() {
    return accessCommands;
  }
  @Override
  public BatchProjectCommands batchProject() {
    return batchProjectCommands;
  }

  public static Builder builder() {
    return new Builder();
  } 
  
  public static class Builder {
    private MongoTransaction transaction;
    private MongoDbConfig config;

    public Builder transaction(MongoTransaction transaction) {
      this.transaction = transaction;
      return this;
    }

    public Builder config(MongoDbConfig config) {
      this.config = config;
      return this;
    }

    public MongoPmRepository build() {
      RepoAssert.notNull(transaction, () -> "transaction not defined!");
      if (config == null) {
        config = ImmutableMongoDbConfig.builder()
            .db("PM")
            .projects("projects")
            .users("users")
            .access("access")
            .build();
      }
      
      final var persistentCommand = new MongoPersistentCommand(transaction, config);
      final var projectCommands = new MongoProjectCommands(persistentCommand);
      final var userCommands = new MongoUserCommands(persistentCommand);
      final var accessCommands = new MongoAccessCommands(persistentCommand, projectCommands, userCommands);
      final var batchProjectCommands = new MongoBatchProjectCommands(projectCommands, userCommands, accessCommands);
      
      return new MongoPmRepository(batchProjectCommands, projectCommands, userCommands, accessCommands);
    }
  }
}
