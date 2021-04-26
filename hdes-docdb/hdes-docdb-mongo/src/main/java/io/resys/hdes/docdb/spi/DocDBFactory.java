package io.resys.hdes.docdb.spi;

import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.resys.hdes.docdb.api.DocDB;
import io.resys.hdes.docdb.api.models.Repo;
import io.resys.hdes.docdb.spi.mongo.ImmutableMongoClientWrapper;
import io.resys.hdes.docdb.spi.mongo.MongoClientInsertBuilder;
import io.resys.hdes.docdb.spi.mongo.MongoClientQuery;
import io.resys.hdes.docdb.spi.mongo.MongoRepoBuilder;
import io.resys.hdes.docdb.spi.support.RepoAssert;
import io.smallrye.mutiny.Uni;

public class DocDBFactory {
  
  public static Builder create() {
    return new Builder();
  }
  
  public static ClientState state(ClientCollections ctx, ReactiveMongoClient client) {
    return new ClientState() {
      @Override
      public ClientCollections getCollections() {
        return ctx;
      }
      @Override
      public RepoBuilder repos() {
        return new MongoRepoBuilder(client, ctx);
      }
      @Override
      public Uni<ClientInsertBuilder> insert(String repoNameOrId) {
        return repos().getByNameOrId(repoNameOrId).onItem().transform(repo -> insert(repo));
      }
      @Override
      public ClientInsertBuilder insert(Repo repo) {
        final var wrapper = ImmutableMongoClientWrapper.builder()
            .repo(repo)
            .client(client)
            .names(ctx.toRepo(repo))
            .build();
        return new MongoClientInsertBuilder(wrapper);
      }
      @Override
      public Uni<ClientQuery> query(String repoNameOrId) {
        return repos().getByNameOrId(repoNameOrId).onItem().transform(repo -> query(repo));
      }
      @Override
      public ClientQuery query(Repo repo) {
        final var wrapper = ImmutableMongoClientWrapper.builder()
            .repo(repo)
            .client(client)
            .names(ctx.toRepo(repo))
            .build();
        return new MongoClientQuery(wrapper);
      }
      @Override
      public ClientRepoState withRepo(Repo repo) {
        final var wrapper = ImmutableMongoClientWrapper.builder()
            .repo(repo)
            .client(client)
            .names(ctx.toRepo(repo))
            .build();
        return new ClientRepoState() {
          @Override
          public ClientQuery query() {
            return new MongoClientQuery(wrapper);
          }
          @Override
          public ClientInsertBuilder insert() {
            return new MongoClientInsertBuilder(wrapper);
          }
        };
      }
      @Override
      public Uni<ClientRepoState> withRepo(String repoNameOrId) {
        return repos().getByNameOrId(repoNameOrId).onItem().transform(repo -> withRepo(repo));
      }
    };
  }
  
  public static ClientCollections names(String db) {
    return ImmutableClientCollections.builder()
        .db(db)
        .repos("repos")
        .refs("refs")
        .tags("tags")
        .blobs("blobs")
        .trees("trees")
        .commits("commits")
        .build();
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
      
      final var ctx = names(db);
      return new DocDBDefault(state(ctx, client));
    }
  }
}
