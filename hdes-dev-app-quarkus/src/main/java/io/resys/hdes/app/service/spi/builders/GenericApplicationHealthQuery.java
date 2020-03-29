package io.resys.hdes.app.service.spi.builders;

import java.util.Arrays;

import io.resys.hdes.app.service.api.ApplicationService.Health;
import io.resys.hdes.app.service.api.ApplicationService.HealthQuery;
import io.resys.hdes.app.service.api.ApplicationService.State;
import io.resys.hdes.app.service.api.ImmutableHealth;
import io.resys.hdes.app.service.api.ImmutableHealthValue;

public class GenericApplicationHealthQuery implements HealthQuery {
  private final State state;

  public GenericApplicationHealthQuery(State state) {
    super();
    this.state = state;
  }

  @Override
  public Health get() {
    return ImmutableHealth.builder().status("OK").values(Arrays.asList(
        ImmutableHealthValue.builder()
          .id("models")
          .value(String.valueOf(state.getModels().size()))
          .build())
        ).build();
  }
}
