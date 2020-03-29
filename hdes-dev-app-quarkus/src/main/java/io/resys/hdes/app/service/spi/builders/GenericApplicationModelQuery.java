package io.resys.hdes.app.service.spi.builders;

import java.util.Collection;

import io.resys.hdes.app.service.api.ApplicationService;
import io.resys.hdes.app.service.api.ApplicationService.Model;
import io.resys.hdes.app.service.api.ApplicationService.State;

public class GenericApplicationModelQuery implements ApplicationService.ModelQuery {

  private final State state;
  
  public GenericApplicationModelQuery(State state) {
    super();
    this.state = state;
  }

  @Override
  public Collection<Model> get() {
    return state.getModels();
  }
}
