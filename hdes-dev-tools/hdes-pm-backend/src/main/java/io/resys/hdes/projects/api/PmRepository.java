package io.resys.hdes.projects.api;

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
import java.util.Optional;

import org.immutables.value.Value;

import io.resys.hdes.projects.api.commands.AccessCommands;
import io.resys.hdes.projects.api.commands.BatchCommands;
import io.resys.hdes.projects.api.commands.GroupCommands;
import io.resys.hdes.projects.api.commands.ProjectCommands;
import io.resys.hdes.projects.api.commands.UserCommands;

public interface PmRepository {

  ProjectCommands projects();
  UserCommands users();
  GroupCommands groups();
  
  AccessCommands access();
  BatchCommands batch();
  
  @Value.Immutable
  interface Project extends Serializable {
    String getId();
    String getRev();
    String getName();
    LocalDateTime getCreated();
  }
  
  @Value.Immutable
  interface User extends Serializable {
    String getId();
    String getRev();
    LocalDateTime getCreated();
    Optional<String> getExternalId();
    String getName();
    String getToken();
  }
  
  @Value.Immutable
  interface Group extends Serializable {
    String getId();
    String getRev();
    LocalDateTime getCreated();
    String getName();
  }
  
  @Value.Immutable
  interface GroupUser extends Serializable {
    String getId();
    String getRev();
    LocalDateTime getCreated();
    String getUserId();
    String getGroupId();
  }
  
  @Value.Immutable
  interface Access extends Serializable {
    String getId();
    String getRev();
    LocalDateTime getCreated();
    String getProjectId();
    String getName();
    
    Optional<String> getUserId();
    Optional<String> getGroupId();
  }
}
