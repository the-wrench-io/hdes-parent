package io.resys.hdes.docdb.spi.mongo;

import io.resys.hdes.docdb.spi.ClientQuery;

public class MongoClientQuery implements ClientQuery {
  
  private final MongoClientWrapper wrapper;
  
  public MongoClientQuery(MongoClientWrapper wrapper) {
    this.wrapper = wrapper;
  }

  @Override
  public TagQuery tags() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public CommitQuery commits() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public RefQuery refs() {
    // TODO Auto-generated method stub
    return null;
  }
}
