package io.resys.hdes.docdb.spi.objects;

import io.resys.hdes.docdb.api.actions.ObjectsActions;
import io.resys.hdes.docdb.spi.state.DocDBClientState;

public class ObjectsActionsDefault implements ObjectsActions {
  private final DocDBClientState state;
  
  public ObjectsActionsDefault(DocDBClientState state) {
    super();
    this.state = state;
  }

  @Override
  public RepoStateBuilder repoState() {
    return new RepoStateBuilderDefault(state);
  }
  @Override
  public RefStateBuilder refState() {
    return new RefStateBuilderDefault(state);
  }

  @Override
  public CommitStateBuilder commitState() {
    // TODO Auto-generated method stub
    return null;
  }
}
