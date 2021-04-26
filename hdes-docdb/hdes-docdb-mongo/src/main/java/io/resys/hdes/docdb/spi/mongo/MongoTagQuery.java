package io.resys.hdes.docdb.spi.mongo;

import com.mongodb.client.model.Filters;

import io.resys.hdes.docdb.api.models.Objects.Tag;
import io.resys.hdes.docdb.spi.ClientQuery.DeleteResult;
import io.resys.hdes.docdb.spi.ClientQuery.TagQuery;
import io.resys.hdes.docdb.spi.ImmutableDeleteResult;
import io.resys.hdes.docdb.spi.codec.TagCodec;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

public class MongoTagQuery implements TagQuery {

  private final MongoClientWrapper wrapper;
  private String name;
  
  public MongoTagQuery(MongoClientWrapper wrapper) {
    this.wrapper = wrapper;
  }
  @Override
  public TagQuery name(String name) {
    this.name = name;
    return this;
  }
  @Override
  public Uni<DeleteResult> delete() {
    final var ctx = this.wrapper.getNames();
    return this.wrapper.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getTags(), Tag.class)
        .deleteOne(Filters.eq(TagCodec.ID, name))
        .onItem()
        .transform(result -> ImmutableDeleteResult.builder().deletedCount(result.getDeletedCount()).build());
  }
  @Override
  public Uni<Tag> get() {
    return find().collectItems().first();
  }
  @Override
  public Multi<Tag> find() {
    final var ctx = this.wrapper.getNames();
    if(name == null || name.isBlank()) {
      return this.wrapper.getClient()
          .getDatabase(ctx.getDb())
          .getCollection(ctx.getTags(), Tag.class)
          .find();      
    }
    return this.wrapper.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getTags(), Tag.class)
        .find(Filters.eq(TagCodec.ID, name));
  }
}
