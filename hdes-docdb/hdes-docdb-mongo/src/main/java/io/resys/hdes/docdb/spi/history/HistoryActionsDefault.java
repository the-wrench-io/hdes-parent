package io.resys.hdes.docdb.spi.history;

import io.resys.hdes.docdb.api.actions.HistoryActions;
import io.resys.hdes.docdb.spi.state.DocDBClientState;

public class HistoryActionsDefault implements HistoryActions {

  private final DocDBClientState state;
  
  public HistoryActionsDefault(DocDBClientState state) {
    super();
    this.state = state;
  }

  @Override
  public BlobHistoryBuilder blob() {
    // TODO Auto-generated method stub
    return null;
  }

}
