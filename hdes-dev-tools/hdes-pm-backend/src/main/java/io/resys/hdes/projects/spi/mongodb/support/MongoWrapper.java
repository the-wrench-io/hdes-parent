package io.resys.hdes.projects.spi.mongodb.support;

import java.util.function.Function;

import org.immutables.value.Value;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

@Value.Immutable
public interface MongoWrapper {
  MongoClient getClient();
  MongoDbConfig getConfig();
  MongoDatabase getDb();

  @Value.Immutable
  interface MongoDbConfig {
    String getDb();
    String getProjects();
    String getUsers();
    String getGroups();
    String getGroupUsers();
    String getAccess();
  }
  
  @FunctionalInterface
  interface MongoTransaction {
    <T> T accept(Function<MongoClient, T> action);
  }
}