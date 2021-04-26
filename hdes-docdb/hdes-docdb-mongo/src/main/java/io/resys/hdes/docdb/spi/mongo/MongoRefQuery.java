package io.resys.hdes.docdb.spi.mongo;

import com.mongodb.client.model.Filters;

import io.resys.hdes.docdb.api.models.Objects.Ref;
import io.resys.hdes.docdb.spi.ClientQuery.RefQuery;
import io.resys.hdes.docdb.spi.codec.RefCodec;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

public class MongoRefQuery implements RefQuery {

  private final MongoClientWrapper wrapper;
  
  public MongoRefQuery(MongoClientWrapper wrapper) {
    this.wrapper = wrapper;
  }
  @Override
  public Uni<Ref> nameOrCommit(String refNameOrCommit) {
    final var ctx = wrapper.getNames();
    return this.wrapper.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getRefs(), Ref.class)
        .find(Filters.or(
            Filters.eq(RefCodec.NAME, refNameOrCommit),
            Filters.eq(RefCodec.COMMIT, refNameOrCommit)
        ))
        .collectItems()
        .first();
  }
  @Override
  public Uni<Ref> get() {
    return find().collectItems().first();
  }
  @Override
  public Multi<Ref> find() {
    final var ctx = wrapper.getNames();
    return this.wrapper.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getTags(), Ref.class)
        .find();
  }
  @Override
  public Uni<Ref> name(String name) {
    final var ctx = wrapper.getNames();
    return this.wrapper.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getRefs(), Ref.class)
        .find(Filters.eq(RefCodec.NAME, name))
        .collectItems()
        .first();
  }
}
