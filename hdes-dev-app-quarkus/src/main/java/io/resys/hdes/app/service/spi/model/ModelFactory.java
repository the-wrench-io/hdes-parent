package io.resys.hdes.app.service.spi.model;

import io.resys.hdes.app.service.api.ApplicationService.Model;
import io.resys.hdes.app.service.api.ApplicationService.StateChange;
import io.resys.hdes.storage.api.Changes;

public interface ModelFactory {
  ModelBuilder create();
  ModelChangeBuilder change();
  
  interface ModelBuilder {
    ModelBuilder from(Changes changes);
    Model build();
  }
  
  interface ModelChangeBuilder {
    ModelChangeBuilder from(StateChange from);
    StateChange build();
  }
}
