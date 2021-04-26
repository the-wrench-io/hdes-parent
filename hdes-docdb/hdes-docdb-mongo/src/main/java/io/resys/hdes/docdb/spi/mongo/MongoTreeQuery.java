package io.resys.hdes.docdb.spi.mongo;

import com.mongodb.client.model.Filters;

import io.resys.hdes.docdb.api.models.Objects.Tree;
import io.resys.hdes.docdb.spi.ClientQuery.TreeQuery;
import io.resys.hdes.docdb.spi.codec.TreeCodec;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

public class MongoTreeQuery implements TreeQuery {

  private final MongoClientWrapper wrapper;
  
  public MongoTreeQuery(MongoClientWrapper wrapper) {
    this.wrapper = wrapper;
  }
  @Override
  public Uni<Tree> id(String tree) {
    final var ctx = wrapper.getNames();
    return wrapper.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getTrees(), Tree.class)
        .find(Filters.eq(TreeCodec.ID, tree))
        .collectItems().first();
  }
  @Override
  public Multi<Tree> find() {
    final var ctx = wrapper.getNames();
    return wrapper.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getTrees(), Tree.class)
        .find();
  }
}
