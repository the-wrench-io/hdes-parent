package io.resys.hdes.app.service.spi;

import io.resys.hdes.app.service.api.ApplicationService;
import io.resys.hdes.app.service.spi.builders.GenericApplicationExceptionBuilder;
import io.resys.hdes.app.service.spi.builders.GenericApplicationHealthQuery;
import io.resys.hdes.app.service.spi.builders.GenericApplicationModelQuery;
import io.resys.hdes.app.service.spi.builders.GenericApplicationSaveBuilder;

public class GenericApplicationService implements ApplicationService {
  private final State state;
  
  public GenericApplicationService(State state) {
    super();
    this.state = state;
  }

  @Override
  public ModelQuery query() {
    return new GenericApplicationModelQuery(state);
  }

  @Override
  public SaveBuilder save() {
    return new GenericApplicationSaveBuilder(state);
  }

  @Override
  public State state() {
    return state;
  }

  @Override
  public HealthQuery health() {
    return new GenericApplicationHealthQuery(state);
  }

  @Override
  public ExceptionBuilder exception() {
    return new GenericApplicationExceptionBuilder();
  }
  
}
