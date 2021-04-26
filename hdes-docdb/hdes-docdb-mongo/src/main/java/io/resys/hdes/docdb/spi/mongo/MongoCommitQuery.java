package io.resys.hdes.docdb.spi.mongo;

import com.mongodb.client.model.Filters;

import io.resys.hdes.docdb.api.models.Objects.Commit;
import io.resys.hdes.docdb.spi.ClientQuery.CommitQuery;
import io.resys.hdes.docdb.spi.codec.CommitCodec;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

public class MongoCommitQuery implements CommitQuery {

  private final MongoClientWrapper wrapper;
  
  public MongoCommitQuery(MongoClientWrapper wrapper) {
    this.wrapper = wrapper;
  }
  @Override
  public Uni<Commit> id(String commit) {
    final var ctx = wrapper.getNames();
    return this.wrapper.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getCommits(), Commit.class)
        .find(Filters.eq(CommitCodec.ID, commit))
        .collectItems().first();
  }
  @Override
  public Multi<Commit> find() {
    final var ctx = wrapper.getNames();
    return this.wrapper.getClient()
        .getDatabase(ctx.getDb())
        .getCollection(ctx.getCommits(), Commit.class)
        .find();
  }
}
