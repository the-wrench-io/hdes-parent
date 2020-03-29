package io.resys.hdes.app.service.api;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.resys.hdes.app.service.spi.state.ApplicationState;
import io.resys.hdes.datatype.api.DataTypeCommand;
import io.resys.hdes.decisiontable.api.DecisionTableModel;
import io.resys.hdes.flow.api.FlowModel;
import io.resys.hdes.storage.api.Changes;



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
    @Nullable
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
    ApplicationState refresh();
  }
  
  interface StateCopy {
    StateCopy add(SaveRequest saveRequest);
    StateCopy add(Changes changes);
    Collection<StateChange> build();
  }
  
  @Value.Immutable
  interface StateChange {
    SaveRequest getRequest();
    Changes getValue();
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
    @Nullable
    String getId();
    @Nullable
    Integer getRev();
    String getLabel();
    Collection<DataTypeCommand> getValues();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableSaveResponse.class)
  @JsonDeserialize(as = ImmutableSaveResponse.class)
  interface SaveResponse {
    @Nullable
    String getId();
    @Nullable
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
    @Nullable
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
    DecisionTableModel getValue();
  }

  
  @Value.Immutable
  @JsonSerialize(as = ImmutableFlViewModel.class)
  @JsonDeserialize(as = ImmutableFlViewModel.class)
  interface FlViewModel extends ViewModel {
    FlowModel getValue();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableSearchModel.class)
  @JsonDeserialize(as = ImmutableSearchModel.class)
  interface SearchModel {
    String getType();
    String getValue();
  }
}
