package io.resys.hdes.docdb.spi.tags;

import io.resys.hdes.docdb.api.actions.TagActions;
import io.resys.hdes.docdb.spi.ClientState;

public class TagActionsDefault implements TagActions {

  private final ClientState state;
  
  public TagActionsDefault(ClientState state) {
    super();
    this.state = state;
  }

  @Override
  public TagBuilder create() {
    return new CreateTagBuilder(state);
  }

  @Override
  public TagQuery query() {
    return new AnyTagQuery(state);
  }
}
