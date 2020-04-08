package io.resys.hdes.object.repo.mongodb;

import java.util.function.Function;

import org.immutables.value.Value;

import com.mongodb.client.MongoClient;

@FunctionalInterface
public interface MongoCommand<T> {
  T accept(Function<MongoClient, T> client);
  
  
  @Value.Immutable
  interface MongoDbConfig {
    String getDb();
    String getHeads();
    String getTags();
    String getObjects();
  }
}
