package io.resys.hdes.projdb.spi.builders;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.resys.hdes.projdb.api.model.BatchResource.GroupResource;
import io.resys.hdes.projdb.api.model.BatchResource.ProjectResource;
import io.resys.hdes.projdb.api.model.BatchResource.UserResource;
import io.resys.hdes.projdb.api.model.ImmutableGroupResource;
import io.resys.hdes.projdb.api.model.ImmutableProjectResource;
import io.resys.hdes.projdb.api.model.ImmutableUserResource;
import io.resys.hdes.projdb.api.model.Resource.Access;
import io.resys.hdes.projdb.api.model.Resource.Group;
import io.resys.hdes.projdb.api.model.Resource.GroupUser;
import io.resys.hdes.projdb.api.model.Resource.Project;
import io.resys.hdes.projdb.api.model.Resource.User;

public class ResourceBuilder {

  public static GroupResource map(ObjectsQuery query, Group group) {
    
    List<Access> access = query.access().group(group.getId()).findAll();
    List<Project> projects = access
        .stream().map(e -> e.getProjectId()).collect(Collectors.toSet())
        .stream().map(e -> query.project().id(e).get())
        .collect(Collectors.toList());
    List<GroupUser> groupUsers = query.groupUser().group(group.getId()).findAll();
    List<User> users = groupUsers.stream().map(user -> query.user().id(user.getUserId()).get()).collect(Collectors.toList());
    
    return ImmutableGroupResource.builder()
      .group(group)
      .users(users.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
      .access(access.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
      .projects(projects.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
      .groupUser(groupUsers.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
      .build();
  }

  public static UserResource map(ObjectsQuery query, User user) {
    List<GroupUser> groupUsers = query.groupUser().user(user.getId()).findAll();
    List<Group> groups = groupUsers
        .stream().map(e -> e.getGroupId()).collect(Collectors.toSet())
        .stream().map(e -> query.group().id(e).get())
        .collect(Collectors.toList());
    
    List<Access> access = new ArrayList<>(query.access().user(user.getId()).findAll());
    groups.stream().map(g -> g.getId())
      .forEach(groupId -> query.access().group(groupId).findAll().forEach(a -> access.add(a)));        

    List<Project> projects = access
        .stream().map(e -> e.getProjectId()).collect(Collectors.toSet())
        .stream().map(e -> query.project().id(e).get())
        .collect(Collectors.toList());

    return ImmutableUserResource.builder()
      .user(user)
      .access(access.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
      .projects(projects.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
      .groups(groups.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
      .groupUsers(groupUsers.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
      .build();
  }
  
  public static ProjectResource map(ObjectsQuery query, Project project) {
    List<Access> access = query.access().project(project.getId()).findAll();
    List<User> users = new ArrayList<>(access
        .stream().filter(e -> e.getUserId().isPresent())
        .map(e -> e.getUserId().get()).collect(Collectors.toSet())
        .stream().map(e -> query.user().id(e).get())
        .collect(Collectors.toList()));
    
    Set<String> groupIds = access.stream()
        .filter(e -> e.getGroupId().isPresent())
        .map(e -> e.getGroupId().get())
        .collect(Collectors.toSet());
    
    List<GroupUser> groupUsers = new ArrayList<>();
    groupIds.stream().forEach(e -> query.groupUser().group(e).findAll().forEach(g -> groupUsers.add(g)));
    
    List<Group> groups = groupIds
        .stream().map(e -> query.group().id(e).get())
        .collect(Collectors.toList());

    // Add users from group
    groupUsers.forEach(user -> users.add(query.user().id(user.getUserId()).get()));
    
    return ImmutableProjectResource.builder()
      .project(project)
      .access(access.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
      .users(users.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
      .groups(groups.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
      .groupUsers(groupUsers.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
      .build();
  }
}
