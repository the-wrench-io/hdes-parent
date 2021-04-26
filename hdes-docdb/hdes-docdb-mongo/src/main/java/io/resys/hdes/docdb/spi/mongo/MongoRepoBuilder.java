package io.resys.hdes.docdb.spi.mongo;

import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;

import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.resys.hdes.docdb.api.models.Repo;
import io.resys.hdes.docdb.spi.ClientCollections;
import io.resys.hdes.docdb.spi.ClientState.RepoBuilder;
import io.resys.hdes.docdb.spi.codec.RepoCodec;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

public class MongoRepoBuilder implements RepoBuilder {

  private final ReactiveMongoClient client;
  private final ClientCollections names;
  
  public MongoRepoBuilder(ReactiveMongoClient client, ClientCollections names) {
    super();
    this.client = client;
    this.names = names;
  }

  @Override
  public Uni<Repo> getByName(String name) {
    final var ctx = names;
    return client
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getRepos(), Repo.class)
        .find(Filters.or(
            Filters.eq(RepoCodec.NAME, name)))
        .collectItems()
        .first();
  }

  @Override
  public Uni<Repo> getByNameOrId(String nameOrId) {
    final var ctx = names;
    return client
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getRepos(), Repo.class)
        .find(Filters.or(
            Filters.eq(RepoCodec.ID, nameOrId),
            Filters.eq(RepoCodec.NAME, nameOrId)))
        .collectItems()
        .first();
  }

  @Override
  public Uni<Repo> insert(Repo newRepo) {
    final var ctx = names;
    return client
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getRepos(), Repo.class)
        .insertOne(newRepo).onItem()
        .transform((InsertOneResult insertOne) -> newRepo);
  }

  @Override
  public Multi<Repo> find() {
    final var ctx = names;
    return client
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getRepos(), Repo.class)
        .find();
  }

}
