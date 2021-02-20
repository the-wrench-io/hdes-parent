package io.resys.hdes.resource.editor.api;

/*-
 * #%L
 * hdes-pm-repo
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

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public interface ReRepository {

  BatchBuilder update();
  BatchBuilder create();
  BatchDelete delete();
  BatchQuery query();

  interface BatchDelete {
    Project project(String projectId, String commitId);
  }
  
  interface BatchBuilder {
    ProjectResource project(Consumer<ImmutableBatchProject.Builder> builder);
  }
  
  interface BatchQuery {
    BatchProjectQuery projects();
  }
  
  interface BatchProjectQuery {
    ProjectResource get(String idOrName);
  }
    
  interface BatchResource extends Serializable {}
  interface BatchMutator extends Serializable {
    @Nullable
    String getId();
    @Nullable
    String getRev();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableBatchProject.class)
  @JsonDeserialize(as = ImmutableBatchProject.class)
  interface BatchProject extends BatchMutator {
    @Nullable
    String getName();
  }

  
  @Value.Immutable
  @JsonSerialize(as = ImmutableProjectResource.class)
  @JsonDeserialize(as = ImmutableProjectResource.class)
  interface ProjectResource extends BatchResource {
    Project getProject();
  }

  @JsonSerialize(as = ImmutableProject.class)
  @JsonDeserialize(as = ImmutableProject.class)
  @Value.Immutable
  interface Project extends Serializable {
    String getId();
    String getRev();
    String getName();
    LocalDateTime getCreated();
  }
}
