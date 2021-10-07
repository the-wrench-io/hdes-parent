package io.resys.hdes.client.api;

/*-
 * #%L
 * hdes-client-api
 * %%
 * Copyright (C) 2020 - 2021 Copyright 2020 ReSys OÜ
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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.resys.hdes.client.api.ast.AstCommandType;
import io.resys.hdes.client.api.ast.AstType;
import io.resys.hdes.client.api.ast.DecisionAstType;
import io.resys.hdes.client.api.ast.FlowAstType;
import io.resys.hdes.client.api.ast.ServiceAstType;
import io.smallrye.mutiny.Uni;

public interface HdesStore {
  CreateBuilder create();
  QueryBuilder query();
  DeleteBuilder delete();
  UpdateBuilder update();
  
  interface DeleteBuilder {
    Uni<Entity<AstType>> build(DeleteAstType deleteType);
  }
  
  interface UpdateBuilder {
    Uni<Entity<AstType>> build(UpdateAstType updateType); 
  }
  
  interface QueryBuilder {
    Uni<StoreState> get();
    Uni<Entity<AstType>> get(String id);
  }
  
  interface CreateBuilder {
    Uni<Entity<FlowAstType>> flow(String name);
    Uni<Entity<DecisionAstType>> decision(String name);
    Uni<Entity<ServiceAstType>> service(String name);
    Uni<Entity<AstType>> build(CreateAstType newType);
  }

  @JsonSerialize(as = ImmutableDeleteAstType.class)
  @JsonDeserialize(as = ImmutableDeleteAstType.class)
  @Value.Immutable
  interface DeleteAstType extends Serializable {
    String getId();
  }
  
  @JsonSerialize(as = ImmutableCreateAstType.class)
  @JsonDeserialize(as = ImmutableCreateAstType.class)
  @Value.Immutable
  interface UpdateAstType extends Serializable {
    String getId();
    EntityType getType();
    List<AstCommandType> getBody();
  }
  
  @JsonSerialize(as = ImmutableCreateAstType.class)
  @JsonDeserialize(as = ImmutableCreateAstType.class)
  @Value.Immutable
  interface CreateAstType extends Serializable {
    String getName();
    EntityType getType();
  }
  
  @JsonSerialize(as = ImmutableStoreState.class)
  @JsonDeserialize(as = ImmutableStoreState.class)
  @Value.Immutable
  interface StoreState {
    Map<String, Entity<FlowAstType>> getFlows();
    Map<String, Entity<FlowAstType>> getServices();
    Map<String, Entity<FlowAstType>> getDecisions();
  }
  
  @JsonSerialize(as = ImmutableEntity.class)
  @Value.Immutable
  interface Entity<T extends AstType> {
    String getId();
    String getValue();
    EntityType getType();
    T getBody();
  }
  
  enum EntityType { 
    FLOW, FLOW_TASK, DT, TAG, DATA_TYPE 
  }
  
  @JsonSerialize(as = ImmutableStoreExceptionMsg.class)
  @Value.Immutable
  interface StoreExceptionMsg {
    String getId();
    String getValue();
    List<String> getArgs();
  }
}
