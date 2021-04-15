package io.resys.hdes.docdb.spi.state;


import org.immutables.value.Value;

import io.quarkus.mongodb.reactive.ReactiveMongoClient;

@Value.Immutable
public interface DocDBClientState {
  ReactiveMongoClient getClient();
  DocDBContext getContext();
}
