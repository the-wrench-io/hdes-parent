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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public interface PmRepository {

  BatchBuilder update();
  BatchBuilder create();
  BatchDelete delete();
  BatchQuery query();

  interface BatchDelete {
    Project project(String projectId, String rev);
    Group group(String groupId, String rev);
    User user(String userId, String rev);
  }
  
  interface BatchBuilder {
    ProjectResource project(Consumer<ImmutableBatchProject.Builder> builder);
    ProjectResource project(BatchProject project);
    
    GroupResource group(Consumer<ImmutableBatchGroup.Builder> builder);
    GroupResource group(BatchGroup group);
    
    UserResource user(Consumer<ImmutableBatchUser.Builder> builder);
    UserResource user(BatchUser user);
  }
  
  interface BatchQuery {
    BatchUserQuery users();
    BatchGroupQuery groups();
    BatchProjectQuery project();
  }
  
  interface BatchProjectQuery {
    ProjectResource get(String idOrName);
    ProjectResource get(String id, String rev);
    List<ProjectResource> find();
  }
  
  interface BatchUserQuery {
    UserResource get(String idOrValueOrExternalIdOrToken);
    List<UserResource> find();
  }

  interface BatchGroupQuery {
    GroupResource get(String idOrName);
    List<GroupResource> find();
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
    @Nullable
    List<String> getUsers();
    @Nullable
    List<String> getGroups();
  }

  @Value.Immutable
  @JsonSerialize(as = ImmutableBatchGroup.class)
  @JsonDeserialize(as = ImmutableBatchGroup.class)
  interface BatchGroup extends BatchMutator {
    @Nullable
    String getName();
    @Nullable
    List<String> getUsers();
    @Nullable
    List<String> getProjects();
  }

  @Value.Immutable
  @JsonSerialize(as = ImmutableBatchUser.class)
  @JsonDeserialize(as = ImmutableBatchUser.class)
  interface BatchUser extends BatchMutator {
    @Nullable
    String getName();
    @Nullable
    String getEmail();
    @Nullable
    String getExternalId();
    @Nullable
    UserStatus getStatus();
    @Nullable
    List<String> getProjects();
    @Nullable
    List<String> getGroups();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableProjectResource.class)
  @JsonDeserialize(as = ImmutableProjectResource.class)
  interface ProjectResource extends BatchResource {
    Project getProject();
    Map<String, User> getUsers();
    Map<String, Access> getAccess();
    Map<String, Group> getGroups();
    Map<String, GroupUser> getGroupUsers();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableUserResource.class)
  @JsonDeserialize(as = ImmutableUserResource.class)
  interface UserResource extends BatchResource {
    User getUser();
    Map<String, Project> getProjects();
    Map<String, Access> getAccess();
    Map<String, Group> getGroups();
    Map<String, GroupUser> getGroupUsers();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableGroupResource.class)
  @JsonDeserialize(as = ImmutableGroupResource.class)
  interface GroupResource extends BatchResource {
    Group getGroup();
    Map<String, User> getUsers();
    Map<String, Project> getProjects();
    Map<String, Access> getAccess();
    Map<String, GroupUser> getGroupUser();
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

  @JsonSerialize(as = ImmutableUser.class)
  @JsonDeserialize(as = ImmutableUser.class)
  @Value.Immutable
  interface User extends Serializable {
    String getId();
    String getRev();
    LocalDateTime getCreated();
    Optional<String> getExternalId();
    String getName();
    String getEmail();
    String getToken();
    UserStatus getStatus();
  }
  
  enum UserStatus {
    PENDING, ENABLED, DISABLED
  }
  
  @JsonSerialize(as = ImmutableGroup.class)
  @JsonDeserialize(as = ImmutableGroup.class)
  @Value.Immutable
  interface Group extends Serializable {
    String getId();
    String getRev();
    LocalDateTime getCreated();
    String getName();
  }
  
  @JsonSerialize(as = ImmutableUser.class)
  @JsonDeserialize(as = ImmutableUser.class)
  @Value.Immutable
  interface GroupUser extends Serializable {
    String getId();
    String getRev();
    LocalDateTime getCreated();
    String getUserId();
    String getGroupId();
  }
  
  @JsonSerialize(as = ImmutableAccess.class)
  @JsonDeserialize(as = ImmutableAccess.class)
  @Value.Immutable
  interface Access extends Serializable {
    String getId();
    String getRev();
    LocalDateTime getCreated();
    String getProjectId();
    Optional<String> getComment();
    
    Optional<String> getUserId();
    Optional<String> getGroupId();
  }
}
