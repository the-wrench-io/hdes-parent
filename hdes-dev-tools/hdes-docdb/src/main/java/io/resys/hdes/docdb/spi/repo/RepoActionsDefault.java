package io.resys.hdes.docdb.spi.repo;

import io.resys.hdes.docdb.api.actions.RepoActions;
import io.resys.hdes.docdb.spi.state.DocDBClientState;

public class RepoActionsDefault implements RepoActions {

  private final DocDBClientState state;
  
  public RepoActionsDefault(DocDBClientState state) {
    super();
    this.state = state;
  }

  @Override
  public QueryBuilder query() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public CreateBuilder create() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public UpdateBuilder update() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public StateBuilder state() {
    // TODO Auto-generated method stub
    return null;
  }
}