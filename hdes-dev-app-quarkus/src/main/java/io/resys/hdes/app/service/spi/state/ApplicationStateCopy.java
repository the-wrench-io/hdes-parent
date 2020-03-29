package io.resys.hdes.app.service.spi.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import io.resys.hdes.app.service.api.ApplicationService.Model;
import io.resys.hdes.app.service.api.ApplicationService.SaveRequest;
import io.resys.hdes.app.service.api.ApplicationService.StateChange;
import io.resys.hdes.app.service.api.ApplicationService.StateCopy;
import io.resys.hdes.app.service.api.ImmutableSaveRequest;
import io.resys.hdes.app.service.api.ImmutableStateChange;
import io.resys.hdes.app.service.spi.model.ModelCache;
import io.resys.hdes.app.service.spi.model.ModelFactory;
import io.resys.hdes.app.service.spi.model.ModelServices;
import io.resys.hdes.storage.api.Changes;

public class ApplicationStateCopy implements StateCopy {

  private final ModelServices config;
  private final ModelFactory modelFactory;
  private final ModelCache cache;
  private final Collection<StateChange> newChanges = new ArrayList<>();

  public ApplicationStateCopy(ModelServices config, ModelCache cache, ModelFactory modelFactory) {
    super();
    this.config = config;
    this.cache = cache.copy();
    this.modelFactory = modelFactory;
  }

  @Override
  public StateCopy add(SaveRequest saveRequest) {
    Changes changes = config.getStorageService().changes().save()
        .id(saveRequest.getId())
        .revision(saveRequest.getRev() == null ? 0 : saveRequest.getRev())
        .label(saveRequest.getLabel())
        .changes(saveRequest.getValues())
        .copy().blockingGet();
    Model model = modelFactory.create().from(changes).build();
    
    newChanges.add(ImmutableStateChange.builder()
        .request(saveRequest)
        .value(changes)
        .model(model)
        .build());
    return this;
  }

  @Override
  public StateCopy add(Changes changes) {
    SaveRequest saveRequest = ImmutableSaveRequest.builder()
        .id(changes.getId())
        .label(changes.getLabel())
        .values(changes.getValues()).build();
    
    Model model = modelFactory.create().from(changes).build();
    
    newChanges.add(ImmutableStateChange.builder()
        .request(saveRequest)
        .value(changes)
        .model(model)
        .build());
    
    newChanges.add(ImmutableStateChange.builder()
        .request(saveRequest)
        .value(changes)
        .model(model)
        .build());
    return this;
  }

  
  @Override
  public Collection<StateChange> build() {
    return Collections.unmodifiableCollection(newChanges);
  }
}
