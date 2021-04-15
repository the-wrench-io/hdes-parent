package io.resys.hdes.docdb.spi.tag;

import io.resys.hdes.docdb.api.actions.TagActions;
import io.resys.hdes.docdb.spi.state.DocDBClientState;

public class TagActionsDefault implements TagActions {

  private final DocDBClientState state;
  
  public TagActionsDefault(DocDBClientState state) {
    super();
    this.state = state;
  }

  @Override
  public TagBuilder create() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public TagQuery query() {
    // TODO Auto-generated method stub
    return null;
  }


}
