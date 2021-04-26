package io.resys.hdes.docdb.spi;

import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.resys.hdes.docdb.api.DocDB;
import io.resys.hdes.docdb.spi.state.ImmutableDocDBClientState;
import io.resys.hdes.docdb.spi.state.ImmutableDocDBContext;
import io.resys.hdes.docdb.spi.support.RepoAssert;

public class DocDBFactory {
  
  public static Builder create() {
    return new Builder();
  }
  
  public static class Builder {
    private ReactiveMongoClient client;
    private String db = "docdb";

    public Builder db(String db) {
      this.db = db;
      return this;
    }
    public Builder client(ReactiveMongoClient client) {
      this.client = client;
      return this;
    }
    public DocDB build() {
      RepoAssert.notNull(client, () -> "client must be defined!");
      RepoAssert.notNull(db, () -> "db must be defined!");
      
      this.state = ImmutableDocDBClientState.builder()
          .client(client)
          .context(ctx)
          .build();
      
      final var ctx = ImmutableDocDBContext.builder()
        .db(db)
        .repos("repos")
        .refs("refs")
        .tags("tags")
        .blobs("blobs")
        .trees("trees")
        .commits("commits")
        .build();
      return new DocDBDefault(client, ctx);
    }
  }
}
