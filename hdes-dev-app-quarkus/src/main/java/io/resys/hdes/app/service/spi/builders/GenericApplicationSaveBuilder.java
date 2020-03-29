package io.resys.hdes.app.service.spi.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import io.resys.hdes.app.service.api.ApplicationService.SaveBuilder;
import io.resys.hdes.app.service.api.ApplicationService.SaveRequest;
import io.resys.hdes.app.service.api.ApplicationService.SaveResponse;
import io.resys.hdes.app.service.api.ApplicationService.State;
import io.resys.hdes.app.service.api.ApplicationService.StateCopy;

public class GenericApplicationSaveBuilder implements SaveBuilder {
  private final State state;
  private final List<SaveRequest> entries = new ArrayList<>();

  public GenericApplicationSaveBuilder(State state) {
    super();
    this.state = state;
  }

  @Override
  public SaveBuilder add(SaveRequest... entry) {
    entries.addAll(Arrays.asList(entry));
    return this;
  }

  @Override
  public Collection<SaveResponse> build() {
    StateCopy stateCopy = state.copy();
    for (SaveRequest saveRequest : entries) {
      stateCopy.add(saveRequest);
    }
    return state.save(stateCopy.build());
  }
}
