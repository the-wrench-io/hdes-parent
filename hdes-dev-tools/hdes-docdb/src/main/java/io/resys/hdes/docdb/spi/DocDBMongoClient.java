package io.resys.hdes.docdb.spi;

import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.resys.hdes.docdb.api.DocDB;
import io.resys.hdes.docdb.api.actions.CheckoutActions;
import io.resys.hdes.docdb.api.actions.CommitActions;
import io.resys.hdes.docdb.api.actions.HistoryActions;
import io.resys.hdes.docdb.api.actions.RepoActions;
import io.resys.hdes.docdb.api.actions.TagActions;

public class DocDBMongoClient implements DocDB {
  private final ReactiveMongoClient client;
  private final DocDBContext ctx;
  
  public DocDBMongoClient(ReactiveMongoClient client, DocDBContext ctx) {
    super();
    this.client = client;
    this.ctx = ctx;
  }

  @Override
  public RepoActions repo() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public CommitActions commit() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public TagActions tag() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public CheckoutActions checkout() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public HistoryActions history() {
    // TODO Auto-generated method stub
    return null;
  }
}
