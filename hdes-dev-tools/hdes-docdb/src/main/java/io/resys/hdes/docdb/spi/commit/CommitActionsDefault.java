package io.resys.hdes.docdb.spi.commit;

import io.resys.hdes.docdb.api.actions.CommitActions;
import io.resys.hdes.docdb.spi.state.DocDBClientState;

public class CommitActionsDefault implements CommitActions {

  private final DocDBClientState state;
  
  public CommitActionsDefault(DocDBClientState state) {
    super();
    this.state = state;
  }

  @Override
  public HeadCommitBuilder head() {
    // TODO Auto-generated method stub
    return null;
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
