package io.resys.hdes.app.service.api;

/*-
 * #%L
 * hdes-dev-app
 * %%
 * Copyright (C) 2020 Copyright 2020 ReSys OÜ
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;



public interface ApplicationService {
  
  ModelQuery query();
  
  SaveBuilder save();
  
  State state();
  
  HealthQuery health();
  
  ExceptionBuilder exception();
 
  interface ExceptionBuilder {
    ExceptionBuilder value(Exception e);
    Health build();
  }
  
  interface HealthQuery {
    Health get();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableHealth.class)
  @JsonDeserialize(as = ImmutableHealth.class)
  interface Health {
    String getStatus();
    String getLogCode();
    Collection<HealthValue> getValues();
  }

  @JsonSerialize(as = ImmutableHealthValue.class)
  @JsonDeserialize(as = ImmutableHealthValue.class)
  @Value.Immutable
  interface HealthValue {
    String getId();
    String getValue();
  }
  
  interface State {
    Collection<Model> getModels();
    Optional<Model> getModel(String id);
    StateCopy copy();
    Collection<SaveResponse> save(Collection<StateChange> from);
  }
  
  interface StateCopy {
    StateCopy add(SaveRequest saveRequest);
    StateCopy add(Object changes);
    Collection<StateChange> build();
  }
  
  @Value.Immutable
  interface StateChange {
    SaveRequest getRequest();
    Object getValue();
    Model getModel();
  }
  
  
  interface SaveBuilder {
    SaveBuilder add(SaveRequest ...entry);
    Collection<SaveResponse> build();
  }
  
  interface ModelQuery {
    Collection<Model> get();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableSaveRequest.class)
  @JsonDeserialize(as = ImmutableSaveRequest.class)
  interface SaveRequest {
    String getId();
    Integer getRev();
    String getLabel();
    Collection<Object> getValues();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableSaveResponse.class)
  @JsonDeserialize(as = ImmutableSaveResponse.class)
  interface SaveResponse {
    String getId();
    String getLabel();
  }

  @Value.Immutable
  @JsonSerialize(as = ImmutableModel.class)
  @JsonDeserialize(as = ImmutableModel.class)
  interface Model {
    String getId();
    String getType();
    String getName();
    ViewModel getView();
    Collection<SearchModel> getSearch();
    UpdateModel getUpdate();
  }

  @Value.Immutable
  @JsonSerialize(as = ImmutableUpdateModel.class)
  @JsonDeserialize(as = ImmutableUpdateModel.class)
  interface UpdateModel {
    LocalDateTime getCreated();
    LocalDateTime getUpdated();
    String getUpdatedBy();
    String getCreatedBy();
    String getStorage();
    String getTenant();
    Collection<String> getTags();
  }
  
  interface ViewModel {
    String getType();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableDtViewModel.class)
  @JsonDeserialize(as = ImmutableDtViewModel.class)
  interface DtViewModel extends ViewModel {
    Object getValue();
  }

  
  @Value.Immutable
  @JsonSerialize(as = ImmutableFlViewModel.class)
  @JsonDeserialize(as = ImmutableFlViewModel.class)
  interface FlViewModel extends ViewModel {
    Object getValue();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableSearchModel.class)
  @JsonDeserialize(as = ImmutableSearchModel.class)
  interface SearchModel {
    String getType();
    String getValue();
  }
}
