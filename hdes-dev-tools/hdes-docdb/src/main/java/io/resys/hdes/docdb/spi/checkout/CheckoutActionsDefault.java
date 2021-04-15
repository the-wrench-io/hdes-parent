package io.resys.hdes.docdb.spi.checkout;

import io.resys.hdes.docdb.api.actions.CheckoutActions;
import io.resys.hdes.docdb.spi.state.DocDBClientState;

public class CheckoutActionsDefault implements CheckoutActions {

  private final DocDBClientState state;
  
  public CheckoutActionsDefault(DocDBClientState state) {
    super();
    this.state = state;
  }

  @Override
  public CommitCheckout commit() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public TagCheckout tag() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public HeadCheckout head() {
    // TODO Auto-generated method stub
    return null;
  }


}
