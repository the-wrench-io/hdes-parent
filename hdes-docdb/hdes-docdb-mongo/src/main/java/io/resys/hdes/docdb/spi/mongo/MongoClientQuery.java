package io.resys.hdes.docdb.spi.mongo;

import io.resys.hdes.docdb.spi.ClientQuery;

public class MongoClientQuery implements ClientQuery {
  
  private final MongoClientWrapper wrapper;
  
  public MongoClientQuery(MongoClientWrapper wrapper) {
    this.wrapper = wrapper;
  }

  @Override
  public TagQuery tags() {
    return new MongoTagQuery(wrapper);
  }

  @Override
  public CommitQuery commits() {
    return new MongoCommitQuery(wrapper);
  }

  @Override
  public RefQuery refs() {
    return new MongoRefQuery(wrapper);
  }

  @Override
  public TreeQuery trees() {
    return new MongoTreeQuery(wrapper);
  }

  @Override
  public BlobQuery blobs() {
    return new MongoBlobQuery(wrapper);
  }
}
