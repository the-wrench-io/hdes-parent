package io.resys.hdes.client.spi.store;

import io.resys.hdes.client.api.HdesStore;

public class HdesDocumentStore implements HdesStore {

  private final PersistenceConfig config;
  
  public HdesDocumentStore(PersistenceConfig config) {
    this.config = config;
  }

  @Override
  public CreateBuilder create() {
    return new DocumentCreateBuilder(config);
  }

  @Override
  public QueryBuilder query() {
    return new DocumentQueryBuilder(config);
  }

  @Override
  public DeleteBuilder delete() {
    return new DocumentDeleteBuilder(config);
  }

  @Override
  public UpdateBuilder update() {
    return new DocumentUpdateBuilder(config);
  }

}
