package io.resys.hdes.app.service.spi.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.resys.hdes.app.service.api.ApplicationService.Model;
import io.resys.hdes.app.service.api.ApplicationService.SaveRequest;
import io.resys.hdes.app.service.api.ApplicationService.SaveResponse;
import io.resys.hdes.app.service.api.ApplicationService.State;
import io.resys.hdes.app.service.api.ApplicationService.StateChange;
import io.resys.hdes.app.service.api.ApplicationService.StateCopy;
import io.resys.hdes.app.service.api.ImmutableSaveResponse;
import io.resys.hdes.app.service.spi.model.ModelCache;
import io.resys.hdes.app.service.spi.model.ModelFactory;
import io.resys.hdes.app.service.spi.model.ModelServices;
import io.resys.hdes.storage.api.Changes;

public class ApplicationState implements State {
  private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationState.class);
  
  private final ModelServices config;
  private final ModelFactory modelFactory;
  private final ModelCache cache;

  public ApplicationState(ModelServices config, ModelCache cache, ModelFactory modelFactory) {
    super();
    this.config = config;
    this.cache = cache;
    this.modelFactory = modelFactory;
  }

  @Override
  public Collection<Model> getModels() {
    return cache.getModels();
  }

  @Override
  public Optional<Model> getModel(String id) {
    return cache.getModel(id);
  }

  @Override
  public StateCopy copy() {
    return new ApplicationStateCopy(config, cache, modelFactory);
  }
  
  @Override
  public ApplicationState refresh() {
    LOGGER.debug("Refreshing application state");
    
    List<Changes> changes = config.getStorageService().changes().query().get().toList().blockingGet();
    StateCopy copy = copy();
    
    for (Changes stateChange : changes) {
      copy.add(stateChange);
    }
    
    for (StateChange stateChange : copy.build()) {
      Changes saved = stateChange.getValue();
      cache.setModel(stateChange.getModel(), saved);
    }
    return this;
  }

  @Override
  public Collection<SaveResponse> save(Collection<StateChange> from) {
    Collection<SaveResponse> result = new ArrayList<>();
    for (StateChange stateChange : from) {
      SaveRequest saveRequest = stateChange.getRequest();
      Changes saved = config.getStorageService().changes().save()
          .id(stateChange.getValue().getId())
          .revision(saveRequest.getRev() == null ? 0 : saveRequest.getRev())
          .label(saveRequest.getLabel())
          .changes(saveRequest.getValues())
          .build().blockingGet();
      result.add(ImmutableSaveResponse.builder()
          .id(saved.getId())
          .label(saved.getLabel())
          .build());
      cache.setModel(stateChange.getModel(), saved);
    }
    return result;
  }
}