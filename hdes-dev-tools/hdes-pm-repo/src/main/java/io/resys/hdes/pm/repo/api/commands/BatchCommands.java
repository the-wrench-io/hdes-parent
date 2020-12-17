package io.resys.hdes.pm.repo.api.commands;

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

import java.util.List;
import java.util.Map;

import org.immutables.value.Value;

import io.resys.hdes.pm.repo.api.PmRepository.Access;
import io.resys.hdes.pm.repo.api.PmRepository.Group;
import io.resys.hdes.pm.repo.api.PmRepository.GroupUser;
import io.resys.hdes.pm.repo.api.PmRepository.Project;
import io.resys.hdes.pm.repo.api.PmRepository.User;

public interface BatchCommands {

  BatchProjectBuilder createProject();
  BatchGroupUsersBuilder createGroupUsers();
  
  BatchProjectQuery queryProject();
  BatchUserQuery queryUsers();
  BatchGroupQuery queryGroups();
  
  interface BatchUserQuery {
    UserResource get(String idOrValueOrExternalIdOrToken);
    List<UserResource> find();
  }

  interface BatchGroupQuery {
    GroupResource get(String idOrName);
    List<GroupResource> find();
  }
  
  interface BatchGroupUsersBuilder {
    BatchGroupUsersBuilder groupId(String groupIdOrName);
    BatchGroupUsersBuilder users(String ... userIdOrExternalIdOrValue);
    BatchGroupUsersBuilder createUser(boolean createUsersIfNotFound);
    GroupResource build();
  }
  
  interface BatchProjectBuilder {
    
    BatchProjectBuilder users(String ... userIdOrExternalIdOrValue);
    // users or groups
    BatchProjectBuilder groups(String ... groupNames);
    
    BatchProjectBuilder projectName(String projectName);
    BatchProjectBuilder createUser(boolean createUsersIfNotFound);
    ProjectResource build();
  }
  
  interface BatchProjectQuery {
    ProjectResource get(String id);
    List<ProjectResource> find();
  }
  
  @Value.Immutable
  interface ProjectResource {
    Project getProject();
    Map<String, User> getUsers();
    Map<String, Access> getAccess();
    Map<String, Group> getGroups();
    Map<String, GroupUser> getGroupUsers();
  }
  
  
  @Value.Immutable
  interface UserResource {
    User getUser();
    Map<String, Project> getProjects();
    Map<String, Access> getAccess();
    Map<String, Group> getGroups();
    Map<String, GroupUser> getGroupUsers();
  }
  
  @Value.Immutable
  interface GroupResource {
    Group getGroup();
    Map<String, User> getUsers();
    Map<String, Project> getProjects();
    Map<String, Access> getAccess();
    Map<String, GroupUser> getGroupUser();
  }
}
