//package io.resys.hdes.projects.api.commands;
//
//import java.io.Serializable;
//
///*-
// * #%L
// * hdes-pm-repo
// * %%
// * Copyright (C) 2020 Copyright 2020 ReSys OÜ
// * %%
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// * 
// *      http://www.apache.org/licenses/LICENSE-2.0
// * 
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// * #L%
// */
//
//import java.util.List;
//import java.util.Map;
//
//import javax.annotation.Nullable;
//
//import org.immutables.value.Value;
//
//import io.resys.hdes.projects.api.PmRepository.Access;
//import io.resys.hdes.projects.api.PmRepository.Group;
//import io.resys.hdes.projects.api.PmRepository.GroupUser;
//import io.resys.hdes.projects.api.PmRepository.Project;
//import io.resys.hdes.projects.api.PmRepository.User;
//
//
//public interface BatchCommands {
//  BatchBuilder builder();
//  BatchQuery query();
//  
//  interface BatchBuilder {
//    ProjectResource project(BatchProject project);
//    GroupResource group(BatchGroup group);
//    UserResource user(BatchUser user);
//  }
//  
//  interface BatchQuery {
//    BatchUserQuery users();
//    BatchGroupQuery groups();
//    BatchProjectQuery project();
//  }
//  
//  interface BatchProjectQuery {
//    ProjectResource get(String id);
//    ProjectResource get(String id, String rev);
//    List<ProjectResource> find();
//  }
//  
//  interface BatchUserQuery {
//    UserResource get(String idOrValueOrExternalIdOrToken);
//    List<UserResource> find();
//  }
//
//  interface BatchGroupQuery {
//    GroupResource get(String idOrName);
//    List<GroupResource> find();
//  }
//  
//  interface BatchResource extends Serializable {}
//  interface BatchMutator extends Serializable {
//    @Nullable
//    String getId();
//    @Nullable
//    String getRev();
//  }
//  
//  @Value.Immutable
//  interface BatchProject extends BatchMutator {
//    String getName();
//    List<String> getUsers();
//    List<String> getGroups();
//  }
//
//  @Value.Immutable
//  interface BatchGroup extends BatchMutator {
//    String getName();
//    List<String> getUsers();
//    List<String> getProjects();
//  }
//
//  @Value.Immutable
//  interface BatchUser extends BatchMutator {
//    String getName();
//    List<String> getProjects();
//    List<String> getGroups();
//  }
//  
//  @Value.Immutable
//  interface ProjectResource extends BatchResource {
//    Project getProject();
//    Map<String, User> getUsers();
//    Map<String, Access> getAccess();
//    Map<String, Group> getGroups();
//    Map<String, GroupUser> getGroupUsers();
//  }
//  
//  @Value.Immutable
//  interface UserResource extends BatchResource {
//    User getUser();
//    Map<String, Project> getProjects();
//    Map<String, Access> getAccess();
//    Map<String, Group> getGroups();
//    Map<String, GroupUser> getGroupUsers();
//  }
//  
//  @Value.Immutable
//  interface GroupResource extends BatchResource {
//    Group getGroup();
//    Map<String, User> getUsers();
//    Map<String, Project> getProjects();
//    Map<String, Access> getAccess();
//    Map<String, GroupUser> getGroupUser();
//  }
//}
