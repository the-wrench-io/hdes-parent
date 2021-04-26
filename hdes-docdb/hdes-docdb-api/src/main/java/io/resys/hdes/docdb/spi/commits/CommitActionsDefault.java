package io.resys.hdes.docdb.spi.commits;

import io.resys.hdes.docdb.api.actions.CommitActions;
import io.resys.hdes.docdb.api.actions.ObjectsActions;
import io.resys.hdes.docdb.spi.ClientState;

public class CommitActionsDefault implements CommitActions {

  private final ClientState state;
  private final ObjectsActions objectsActions;
  
  public CommitActionsDefault(ClientState state, ObjectsActions objectsActions) {
    super();
    this.state = state;
    this.objectsActions = objectsActions;
  }

  @Override
  public HeadCommitBuilder head() {
    return new HeadCommitBuilderDefault(state, objectsActions);
  }

  @Override
  public MergeBuilder merge() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public RebaseBuilder rebase() {
    // TODO Auto-generated method stub
    return null;
  }

}
