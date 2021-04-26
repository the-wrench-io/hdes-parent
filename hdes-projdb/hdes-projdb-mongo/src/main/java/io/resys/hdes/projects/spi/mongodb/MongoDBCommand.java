package io.resys.hdes.projects.spi.mongodb;

import java.util.function.Function;

import io.resys.hdes.projdb.spi.context.DBCommand;
import io.resys.hdes.projdb.spi.context.DBContext;
import io.resys.hdes.projects.spi.mongodb.MongoWrapper.MongoDbConfig;
import io.resys.hdes.projects.spi.mongodb.MongoWrapper.MongoTransaction;
import io.resys.hdes.projects.spi.mongodb.queries.MongoQueryDefault;

public class MongoDBCommand implements DBCommand {
  private final MongoTransaction transaction;
  private final MongoDbConfig config;
  
  public MongoDBCommand(MongoTransaction transaction, MongoDbConfig config) {
    super();
    this.transaction = transaction;
    this.config = config;
  }

  @Override
  public <T> T accept(Function<DBContext, T> action) {
    return transaction.accept(client -> {
      final var mongo = ImmutableMongoWrapper.builder().db(client.getDatabase(config.getDb())).config(config).client(client).build();
      final var query = new MongoQueryDefault(mongo);
      final var ctx = new MongoDBContext(mongo, query);
      return action.apply(ctx);
    });
  }
}
