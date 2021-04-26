package io.resys.hdes.docdb.spi.mongo;

import com.mongodb.client.model.Filters;

import io.resys.hdes.docdb.api.models.Objects.Blob;
import io.resys.hdes.docdb.spi.ClientQuery.BlobQuery;
import io.resys.hdes.docdb.spi.codec.BlobCodec;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

public class MongoBlobQuery implements BlobQuery {

  private final MongoClientWrapper wrapper;
  
  public MongoBlobQuery(MongoClientWrapper wrapper) {
    this.wrapper = wrapper;
  }
  @Override
  public Uni<Blob> id(String blobId) {
    final var ctx = wrapper.getNames();
    return this.wrapper.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getBlobs(), Blob.class)
        .find(Filters.eq(BlobCodec.ID, blobId))
        .collectItems().first();
  }
  @Override
  public Multi<Blob> find() {
    final var ctx = wrapper.getNames();
    return this.wrapper.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getBlobs(), Blob.class)
        .find();
  }
}
