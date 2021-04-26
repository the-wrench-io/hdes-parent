package io.resys.hdes.docdb.spi.support;


import java.util.function.Function;

import org.immutables.value.Value;

import com.mongodb.client.MongoClient;

@FunctionalInterface
public interface MongoCommand<T> {
  T accept(Function<MongoClient, T> client);
  
  
  @Value.Immutable
  interface MongoDbConfig {
    String getDb();
    String getRefs();
    String getTags();
    String getObjects();
  }
}
