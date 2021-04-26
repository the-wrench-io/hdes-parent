package io.resys.hdes.projects.spi.mongodb;

import io.resys.hdes.projdb.spi.ProjectDBClientDefault;
import io.resys.hdes.projdb.spi.context.DBCommand;
import io.resys.hdes.projdb.spi.support.RepoAssert;
import io.resys.hdes.projects.spi.mongodb.MongoWrapper.MongoDbConfig;
import io.resys.hdes.projects.spi.mongodb.MongoWrapper.MongoTransaction;

public class MongoProjectDBClient extends ProjectDBClientDefault {

  public MongoProjectDBClient(DBCommand dbCommand) {
    super(dbCommand);
  }

  public static Config config() {
    return new Config();
  } 
  
  public static class Config {
    private MongoTransaction transaction;
    private MongoDbConfig config;
    private String dbName;
    
    public Config transaction(MongoTransaction transaction) {
      this.transaction = transaction;
      return this;
    }

    public Config config(MongoDbConfig config) {
      this.config = config;
      return this;
    }
    public Config dbName(String dbName) {
      this.dbName = dbName;
      return this;
    }
    public MongoProjectDBClient build() {
      RepoAssert.notNull(transaction, () -> "transaction not defined!");
      RepoAssert.notEmpty(dbName, () -> "dbName not defined!");
      
      if (config == null) {
        config = ImmutableMongoDbConfig.builder()
            .db(dbName)
            .transactions("transactions")
            .projects("projects")
            .users("users")
            .access("access")
            .groups("groups")
            .groupUsers("groupUsers")
            .build();
      }
      
      final var dbCommand = new MongoDBCommand(transaction, config);
      return new MongoProjectDBClient(dbCommand);
    }
  }
}
