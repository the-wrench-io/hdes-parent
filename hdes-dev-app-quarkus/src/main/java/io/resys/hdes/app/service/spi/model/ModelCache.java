package io.resys.hdes.app.service.spi.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import io.resys.hdes.app.service.api.ApplicationService;
import io.resys.hdes.app.service.api.ApplicationService.Model;
import io.resys.hdes.storage.api.Changes;

public class ModelCache {
  
  private final Map<String, Changes> changes;
  private final Map<String, ApplicationService.Model> models;
  private final Collection<ApplicationService.Model> unmodifiableModels;

  public ModelCache() {    
    this.changes = new ConcurrentHashMap<>();
    this.models = new ConcurrentHashMap<>();
    this.unmodifiableModels = Collections.unmodifiableCollection(models.values());
  }
  
  public ModelCache(Map<String, Changes> changes, Map<String, Model> models) {
    this.changes = changes;
    this.models = models;
    this.unmodifiableModels = Collections.unmodifiableCollection(models.values());
  }
  
  public Optional<ApplicationService.Model> getModel(String id) {
    return Optional.ofNullable(models.get(id));
  }

  public Collection<ApplicationService.Model> getModels() {
    return unmodifiableModels;
  }
    
  public ModelCache setModel(ApplicationService.Model model, Changes changes) {
   this.changes.put(changes.getId(), changes);
   this.models.put(changes.getId(), model);
   return this; 
  }
  
  public ModelCache copy() {
    Map<String, Changes> changes = new HashMap<>(this.changes);
    Map<String, ApplicationService.Model> models = new HashMap<>(this.models);
    return new ModelCache(changes, models);
  }
}
