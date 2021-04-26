package io.resys.hdes.docdb.spi.objects;

import io.resys.hdes.docdb.api.actions.ObjectsActions;
import io.resys.hdes.docdb.spi.ClientState;

public class ObjectsActionsDefault implements ObjectsActions {
  private final ClientState state;
  
  public ObjectsActionsDefault(ClientState state) {
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
    return new CommitStateBuilderDefault(state);
  }
}
