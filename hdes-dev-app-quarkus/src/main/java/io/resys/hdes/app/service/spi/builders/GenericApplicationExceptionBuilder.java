package io.resys.hdes.app.service.spi.builders;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.resys.hdes.app.service.api.ApplicationService.ExceptionBuilder;
import io.resys.hdes.app.service.api.ApplicationService.Health;
import io.resys.hdes.app.service.api.ImmutableHealth;
import io.resys.hdes.app.service.api.ImmutableHealthValue;

public class GenericApplicationExceptionBuilder implements ExceptionBuilder {
  private static final Logger LOGGER = LoggerFactory.getLogger(GenericApplicationExceptionBuilder.class);
  
  private Exception e;

  @Override
  public ExceptionBuilder value(Exception e) {
    this.e = e;
    return this;
  }

  @Override
  public Health build() {
    LOGGER.error(e.getMessage(), e);
    
    return ImmutableHealth.builder().status("ERROR").values(Arrays.asList(
        ImmutableHealthValue.builder().id(e.getClass().getSimpleName()).value(e.getMessage() == null ? "": e.getMessage()).build()
        )).build();
  }
}
