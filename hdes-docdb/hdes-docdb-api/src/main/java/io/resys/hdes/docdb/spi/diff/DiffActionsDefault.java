package io.resys.hdes.docdb.spi.diff;

import io.resys.hdes.docdb.api.actions.DiffActions;
import io.resys.hdes.docdb.api.actions.ObjectsActions;
import io.resys.hdes.docdb.spi.ClientState;

public class DiffActionsDefault implements DiffActions {
  private final ClientState state;
  private final ObjectsActions objects;
  
  public DiffActionsDefault(ClientState state, ObjectsActions objects) {
    super();
    this.state = state;
    this.objects = objects;
  }

  @Override
  public HeadDiffBuilder head() {
    return new HeadDiffBuilderDefault(state, objects);
  }
}
