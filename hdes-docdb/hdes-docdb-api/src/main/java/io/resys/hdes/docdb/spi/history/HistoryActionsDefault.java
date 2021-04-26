package io.resys.hdes.docdb.spi.history;

import io.resys.hdes.docdb.api.actions.HistoryActions;
import io.resys.hdes.docdb.spi.ClientState;

public class HistoryActionsDefault implements HistoryActions {

  private final ClientState state;
  
  public HistoryActionsDefault(ClientState state) {
    super();
    this.state = state;
  }

  @Override
  public BlobHistoryBuilder blob() {
    // TODO Auto-generated method stub
    return null;
  }

}
