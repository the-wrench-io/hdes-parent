package io.resys.wrench.assets.bundle.api.repositories;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;

/*-
 * #%L
 * wrench-component-assets-service
 * %%
 * Copyright (C) 2016 - 2017 Copyright 2016 ReSys OÜ
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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.resys.hdes.client.api.HdesClient;
import io.resys.hdes.client.api.ast.AstCommand;
import io.resys.hdes.client.api.ast.TypeDef;
import io.resys.hdes.client.api.ast.TypeDef.Direction;
import io.resys.hdes.client.api.programs.Program;

public interface AssetServiceRepository {

  ServiceExecutor executor();
  ServiceBuilder createBuilder(ServiceType type);
  ServiceQuery createQuery();
  ServiceStore createStore();
  String getHash();
  MigrationBuilder createMigration();
  Migration readMigration(String json);
  String toSrc(MigrationValue migration);
  
  HdesClient getTypes();
  
  interface MigrationBuilder {
    Migration build();
  }

  @JsonSerialize(as = ImmutableMigration.class)
  @JsonDeserialize(as = ImmutableMigration.class)
  @Value.Immutable
  interface Migration {
    String getId();
    List<MigrationValue> getValue();
  }
  
  @JsonSerialize(as = ImmutableMigrationValue.class)
  @JsonDeserialize(as = ImmutableMigrationValue.class)
  @Value.Immutable
  interface MigrationValue {
    String getId();
    ServiceType getType();
    String getName();
    List<AstCommand> getCommands();
  }
  
  interface ServiceExecutor {
    FlowServiceExecutor flow(String name);
    DtServiceExecutor dt(String name);
  }
  
  interface FlowServiceExecutor {
    FlowServiceExecutor withMap(Map<String, Object> input);
    FlowServiceExecutor withEntity(Object inputObject);
    Object andGetTask(String task);
  }

  interface DtServiceExecutor {
    DtServiceExecutor withMap(Map<String, Object> input);
    DtServiceExecutor withEntity(Object inputObject);
    Map<String, Serializable> andGet();
    List<Map<String, Serializable>> andFind();
  }

  interface ServiceStore {
    AssetService get(AssetService service, String rev);
    Collection<AssetService> list();
    AssetService get(String id);
    boolean contains(String id);
    AssetService save(AssetService service);
    AssetService load(AssetService service);
    List<String> getTags();
    void remove(String id);
  }

  interface ServiceIdGen {
    String nextId();
  }
  
  interface ServiceQuery {
    AssetService tag(String name);
    AssetService flowTask(String name);
    AssetService dt(String name);
    AssetService flow(String name);
    AssetService dataType(String name);

    ServiceQuery id(String id);
    ServiceQuery rev(String rev);
    ServiceQuery name(String name);
    ServiceQuery type(ServiceType type);
    List<AssetService> list();
    Optional<AssetService> get();
  }

  interface ServiceBuilder {
    ServiceBuilder id(String id);
    ServiceBuilder name(String name);
    ServiceBuilder src(String src);
    ServiceBuilder pointer(String pointer);
    ServiceBuilder created(Timestamp created);
    ServiceBuilder lastModified(Timestamp lastModified);
    ServiceBuilder rename();
    ServiceBuilder ignoreErrors();
    AssetService build();
  }

  interface AssetService {
    String getId();
    ServiceType getType();
    ServiceDataModel getDataModel();
    String getName();
    String getDescription();
    String getMetadata();
    String getSrc();
    String getPointer();
    String getRev();
    ServiceExecution newExecution();
    Supplier<ServiceExecution> getExecution();
  }

  interface ServiceExecution {
    <T extends Program<?>> T unwrap();
    ServiceExecution insert(Serializable bean);
    <T> void run(Consumer<T> serviceType);
    ServiceResponse run();
  }

  interface ServiceResponse extends AutoCloseable {
    <T> T getDebug();
    void forEach(Consumer<Object> consumer);
    List<?> list();
    <T> T get();
  }

  interface ServicePostProcessorSupplier {
    ServicePostProcessor get(ServiceType type);
  }

  interface ServicePostProcessor {
    void process(ServiceStore store, AssetService oldState, AssetService newState);
    void delete(ServiceStore store, AssetService state);
  }

  enum ServiceType {
    FLOW, FLOW_TASK, DT, TAG, DATA_TYPE
  }

  enum ServiceStatus {
    OK, ERROR
  }

  interface ServiceDataModel {
    String getId();
    Timestamp getCreated();
    Timestamp getModified();
    String getName();
    String getDescription();
    Class<?> getBeanType();
    ServiceType getType();
    ServiceStatus getStatus();
    List<ServiceError> getErrors();
    List<TypeDef> getParams();
    List<ServiceAssociation> getAssociations();
    ServiceDataModel withErrors(List<ServiceError> errors);
    ServiceDataModel withTimestamps(Timestamp created, Timestamp modified);
  }

  interface ServiceAssociation {
    String getId();
    String getName();
    ServiceType getServiceType();
    Direction getDirection();
    ServiceAssociationType getAssociationType();
  }

  interface ServiceError {
    String getId();
    String getMessage();
  }

  enum ServiceAssociationType {
    ONE_TO_ONE, ONE_TO_MANY
  }

  enum ExportType {
    CSV, JSON
  }
}
