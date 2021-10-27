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

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.resys.hdes.client.api.ast.AstBody;
import io.resys.hdes.client.api.ast.AstDecision;
import io.resys.hdes.client.api.ast.AstFlow;
import io.resys.hdes.client.api.ast.AstService;
import io.resys.hdes.client.api.programs.ProgramEnvir.ProgramAssociation;
import io.resys.hdes.client.api.programs.ProgramEnvir.ProgramMessage;
import io.resys.hdes.client.api.programs.ProgramEnvir.ProgramStatus;
import io.smallrye.mutiny.Uni;

/**
 * Backend for composer related service. 
 * Provides mutability of the assets.
 */
public interface HdesComposer {
  
  Uni<ComposerState> get();
  Uni<ComposerState> update(UpdateEntity asset);
  Uni<ComposerState> create(CreateEntity asset);
  Uni<ComposerState> delete(String id);
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableComposerState.class)
  @JsonDeserialize(as = ImmutableComposerState.class)
  interface ComposerState {
    Map<String, ComposerEntity<AstFlow>> getFlows();
    Map<String, ComposerEntity<AstService>> getServices();
    Map<String, ComposerEntity<AstDecision>> getDecisionss();
  }

  @Value.Immutable
  @JsonSerialize(as = ImmutableComposerEntity.class)
  @JsonDeserialize(as = ImmutableComposerEntity.class)
  interface ComposerEntity<A extends AstBody> {
    String getId();
    @Nullable
    A getAst();
    
    List<ProgramMessage> getWarnings();
    List<ProgramMessage> getErrors();
    List<ProgramAssociation> getAssociations();
    ProgramStatus getStatus();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableUpdateEntity.class)
  @JsonDeserialize(as = ImmutableUpdateEntity.class)
  interface UpdateEntity {
    String getId();
    String getBody();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableCreateEntity.class)
  @JsonDeserialize(as = ImmutableCreateEntity.class)
  interface CreateEntity {
    String getName();
    String getType();
  }

}
