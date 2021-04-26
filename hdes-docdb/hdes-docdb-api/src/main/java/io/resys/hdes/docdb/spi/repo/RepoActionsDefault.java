package io.resys.hdes.docdb.spi.repo;

import io.resys.hdes.docdb.api.actions.RepoActions;
import io.resys.hdes.docdb.spi.ClientState;

public class RepoActionsDefault implements RepoActions {

  private final ClientState state;
  
  public RepoActionsDefault(ClientState state) {
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
    return new RepoCreateBuilder(state);
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