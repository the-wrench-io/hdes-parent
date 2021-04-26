package io.resys.hdes.docdb.spi.mongo;

import org.immutables.value.Value;

import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.resys.hdes.docdb.api.models.Repo;
import io.resys.hdes.docdb.spi.ClientCollections;

@Value.Immutable
public interface MongoClientWrapper {
  Repo getRepo();
  ReactiveMongoClient getClient();
  ClientCollections getNames();
}
