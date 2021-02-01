package io.resys.hdes.projects.spi.mongodb.builders;

/*-
 * #%L
 * hdes-pm-backend
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
import java.util.Optional;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import io.resys.hdes.projects.api.PmRepository.Access;
import io.resys.hdes.projects.api.PmRepository.Group;
import io.resys.hdes.projects.api.PmRepository.GroupUser;
import io.resys.hdes.projects.api.PmRepository.Project;
import io.resys.hdes.projects.api.PmRepository.User;
import io.resys.hdes.projects.api.PmRepository.UserStatus;

public interface MongoBuilder {

  ProjectVisitor visitProject();
  GroupVisitor visitGroup();
  GroupUserVisitor visitGroupUser();
  AccessVisitor visitAccess();
  UserVisitor visitUser();
  MongoBuilderTree build();
  
  @Value.Immutable
  interface MongoBuilderTree {
    Map<String, Project> getProject();
    Map<String, Access> getAccess();
    Map<String, User> getUser();
    Map<String, Group> getGroups();
    Map<String, GroupUser> getGroupUsers();
  }
  
  interface Builder<V, E> {
    V visit(E entity);
    V visitId(String id);
    V visitRev(String rev);
    E build();
  }
  
  interface GroupUserVisitor extends Builder<GroupUserVisitor, GroupUser> {
    GroupUserVisitor visitUser(String userId);
    GroupUserVisitor visitGroup(String groupId);
  }
  
  interface AccessVisitor extends Builder<AccessVisitor, Access> {
    AccessVisitor visitUser(String userId);
    AccessVisitor visitGroup(String groupId);
    AccessVisitor visitProject(String projectId);
    AccessVisitor visitComment(String comment);
  }
  
  interface ProjectVisitor extends Builder<ProjectVisitor, Project> {
    ProjectVisitor visitName(@Nullable String name);
    ProjectVisitor visitUsers(@Nullable List<String> users);
    ProjectVisitor visitGroups(@Nullable List<String> groups);
  }
  
  interface GroupVisitor extends Builder<GroupVisitor, Group> {
    GroupVisitor visitName(@Nullable String name);
    GroupVisitor visitUsers(@Nullable List<String> users);
    GroupVisitor visitProjects(@Nullable List<String> projects);
  }

  interface UserVisitor extends Builder<UserVisitor, User> {
    UserVisitor visitName(@Nullable String name);
    UserVisitor visitStatus(@Nullable UserStatus status);
    UserVisitor visitExternalId(@Nullable Optional<String> externalId);
    UserVisitor visitToken(@Nullable String token);
    UserVisitor visitEmail(@Nullable String email);
    UserVisitor visitGroups(@Nullable List<String> groups);
    UserVisitor visitProjects(@Nullable List<String> projects);
  }
}
