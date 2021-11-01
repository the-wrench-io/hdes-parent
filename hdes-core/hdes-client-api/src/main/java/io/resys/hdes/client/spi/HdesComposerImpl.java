package io.resys.hdes.client.spi;

import io.resys.hdes.client.api.HdesClient;
import io.resys.hdes.client.api.HdesComposer;
import io.smallrye.mutiny.Uni;

public class HdesComposerImpl implements HdesComposer {

  private final HdesClient client;
  
  public HdesComposerImpl(HdesClient client) {
    super();
    this.client = client;
    
    
  }

  @Override
  public Uni<ComposerState> get() {
    
    return null;
  }

  @Override
  public Uni<ComposerState> update(UpdateEntity asset) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Uni<ComposerState> create(CreateEntity asset) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Uni<ComposerState> delete(String id) {
    // TODO Auto-generated method stub
    return null;
  }

}
