package io.resys.hdes.projects.spi.mongodb.commands;
 
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.resys.hdes.projects.api.ImmutableConstraintViolation;
import io.resys.hdes.projects.api.PmException;
import io.resys.hdes.projects.api.PmException.ConstraintType;
import io.resys.hdes.projects.api.PmException.ErrorType;
import io.resys.hdes.projects.api.PmRepository.Access;
import io.resys.hdes.projects.api.PmRepository.Group;
import io.resys.hdes.projects.api.PmRepository.GroupUser;
import io.resys.hdes.projects.api.PmRepository.Project;
import io.resys.hdes.projects.api.PmRepository.User;
import io.resys.hdes.projects.api.commands.AccessCommands;
import io.resys.hdes.projects.api.commands.BatchCommands;
import io.resys.hdes.projects.api.commands.GroupCommands;
import io.resys.hdes.projects.api.commands.GroupUserCommands;
import io.resys.hdes.projects.api.commands.ImmutableGroupResource;
import io.resys.hdes.projects.api.commands.ImmutableProjectResource;
import io.resys.hdes.projects.api.commands.ImmutableUserResource;
import io.resys.hdes.projects.api.commands.ProjectCommands;
import io.resys.hdes.projects.api.commands.UserCommands;
import io.resys.hdes.projects.spi.support.RepoAssert;

public class MongoBatchCommands implements BatchCommands {

  private final ProjectCommands projectCommands;
  private final UserCommands userCommands;
  private final AccessCommands accessCommands;
  private final GroupCommands groupCommands;
  private final GroupUserCommands groupUserCommands;
  
  public MongoBatchCommands(
      ProjectCommands projectCommands, 
      UserCommands userCommands,
      AccessCommands accessCommands,
      GroupCommands groupCommands,
      GroupUserCommands groupUserCommands) {
    super();
    this.projectCommands = projectCommands;
    this.userCommands = userCommands;
    this.accessCommands = accessCommands;
    this.groupCommands = groupCommands;
    this.groupUserCommands = groupUserCommands;
  }

  @Override
  public BatchProjectBuilder createProject() {
    return new BatchProjectBuilder() {
      private String projectName;
      private boolean createUsersIfNotFound;
      private List<String> users = Collections.emptyList();
      private List<String> groups = Collections.emptyList();
      
      @Override
      public BatchProjectBuilder projectName(String projectName) {
        this.projectName = projectName;
        return this;
      }
      @Override
      public BatchProjectBuilder createUser(boolean createUsersIfNotFound) {
        this.createUsersIfNotFound = createUsersIfNotFound;
        return this;
      }
      @Override
      public BatchProjectBuilder users(String... userIdOrExternalIdOrValue) {
        users = new ArrayList<>(Arrays.asList(userIdOrExternalIdOrValue));
        return this;
      }
      @Override
      public BatchProjectBuilder groups(String... groupNames) {
        groups = new ArrayList<>(Arrays.asList(groupNames));
        return this;
      }
      private ProjectResource buildByUsers() {
        final var project = projectCommands.create().name(projectName).build();
        final var builder = ImmutableProjectResource.builder().project(project);
        
        for(String userFilter : users) {
          // get user by: 
          //   * id/value/externalId 
          //   * create new user
          final var user = userCommands.query().find(userFilter)
            .orElseGet(() -> userCommands.query().findByValue(userFilter)
                .orElseGet(() -> userCommands.query().findByExternalId(userFilter)
                    .orElseGet(() -> {
                      if(createUsersIfNotFound) {
                        return userCommands.create().value(userFilter).build();  
                      }
                      throw new PmException(ImmutableConstraintViolation.builder()
                          .id(userFilter)
                          .rev("")
                          .constraint(ConstraintType.NOT_FOUND)
                          .type(ErrorType.USER)
                          .build(), "entity: 'user' not found one of the keys: 'name/externalId/id' = '" + userFilter + "'!");
                    })));
          final var access = accessCommands.create()
              .projectId(project.getId())
              .userId(user.getId())
              .name(projectName + "-" + userFilter)
              .build();
          
          builder
            .putUsers(user.getId(), user)
            .putAccess(access.getId(), access);
        }
        
        return builder.build();
      }
      
      private ProjectResource buildByGroups() {
        final var project = projectCommands.create().name(projectName).build();
        final var builder = ImmutableProjectResource.builder().project(project);
        
        for(String groupFilter : groups) {
          // get user by: 
          //   * id/name 
          //   * create new user
          final var group = groupCommands.query().find(groupFilter)
            .orElseGet(() -> groupCommands.query().findByName(groupFilter)
              .orElseGet(() -> {
                if(createUsersIfNotFound) {
                  return groupCommands.create().name(groupFilter).build();  
                }
                
                throw new PmException(ImmutableConstraintViolation.builder()
                    .id(groupFilter)
                    .rev("")
                    .constraint(ConstraintType.NOT_FOUND)
                    .type(ErrorType.GROUP)
                    .build(), "entity: 'group' not found one of the keys: 'id/name' = '" + groupFilter + "'!");
              }));
    
          final var access = accessCommands.create()
              .projectId(project.getId())
              .groupId(group.getId())
              .name(projectName + "-" + groupFilter)
              .build();
          
          builder
            .putGroups(group.getId(), group)
            .putAccess(access.getId(), access);
        }
        
        return builder.build();
      }
      
      
      @Override
      public ProjectResource build() {
        RepoAssert.isTrue(users.isEmpty() || groups.isEmpty(), () -> "users and groups can't be used at the same time!");
        if(!users.isEmpty()) {
          return buildByUsers();
        }
        return buildByGroups();
      }
    };
  }

  @Override
  public BatchProjectQuery queryProjects() {
    return new BatchProjectQuery() {
      private ProjectResource map(Project project) {
        List<Access> access = accessCommands.query().projectId(project.getId()).find();
        List<User> users = new ArrayList<>(access
            .stream().filter(e -> e.getUserId().isPresent())
            .map(e -> e.getUserId().get()).collect(Collectors.toSet())
            .stream().map(e -> userCommands.query().id(e))
            .collect(Collectors.toList()));
        
        Set<String> groupIds = access.stream()
            .filter(e -> e.getGroupId().isPresent())
            .map(e -> e.getGroupId().get())
            .collect(Collectors.toSet());
        
        List<GroupUser> groupUsers = groupIds
            .stream().map(e -> groupUserCommands.query().id(e))
            .collect(Collectors.toList());
        List<Group> groups = groupIds
            .stream().map(e -> groupCommands.query().id(e))
            .collect(Collectors.toList());

        // Add users from group
        groupUsers.forEach(user -> users.add(userCommands.query().id(user.getUserId())));
        
        return ImmutableProjectResource.builder()
          .project(project)
          .access(access.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
          .users(users.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
          .groups(groups.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
          .groupUsers(groupUsers.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
          .build();
      }
      @Override
      public ProjectResource get(String id) {
        return map(projectCommands.query().id(id));
      }
      @Override
      public ProjectResource get(String id, String rev) {
        return map(projectCommands.query().rev(id, rev));
      }
      @Override
      public List<ProjectResource> find() {
        return projectCommands.query().find().stream()
            .map(this::map)
            .collect(Collectors.toList());
      }
    };
  }

  @Override
  public BatchUserQuery queryUsers() {
    return new BatchUserQuery() {
      private UserResource map(User user) {
        List<GroupUser> groupUsers = groupUserCommands.query().userId(user.getId()).find();
        List<Group> groups = groupUsers
            .stream().map(e -> e.getGroupId()).collect(Collectors.toSet())
            .stream().map(e -> groupCommands.query().id(e))
            .collect(Collectors.toList());
        
        List<Access> access = new ArrayList<>(accessCommands.query().userId(user.getId()).find());
        groups.stream().map(g -> g.getId())
          .forEach(groupId -> accessCommands.query().groupId(groupId).find().forEach(a -> access.add(a)));        

        List<Project> projects = access
            .stream().map(e -> e.getProjectId()).collect(Collectors.toSet())
            .stream().map(e -> projectCommands.query().id(e))
            .collect(Collectors.toList());

        return ImmutableUserResource.builder()
          .user(user)
          .access(access.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
          .projects(projects.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
          .groups(groups.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
          .groupUsers(groupUsers.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
          .build();
      }
      @Override
      public UserResource get(String idOrValueOrExternalIdOrToken) {
        return map(userCommands.query().any(idOrValueOrExternalIdOrToken));
      }
      @Override
      public List<UserResource> find() {
        return userCommands.query().find().stream()
            .map(this::map)
            .collect(Collectors.toList());
      }
    };
  }

  @Override
  public BatchGroupQuery queryGroups() {
    return new BatchGroupQuery() {
      private GroupResource map(Group group) {
        List<Access> access = accessCommands.query().groupId(group.getId()).find();
        List<Project> projects = access
            .stream().map(e -> e.getProjectId()).collect(Collectors.toSet())
            .stream().map(e -> projectCommands.query().id(e))
            .collect(Collectors.toList());
        List<GroupUser> groupUsers = groupUserCommands.query().groupId(group.getId()).find();
        List<User> users = groupUsers.stream().map(user -> userCommands.query().id(user.getUserId())).collect(Collectors.toList());
        
        return ImmutableGroupResource.builder()
          .group(group)
          .users(users.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
          .access(access.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
          .projects(projects.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
          .groupUser(groupUsers.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
          .build();
      }
      @Override
      public GroupResource get(String idOrName) {
        return map(groupCommands.query().any(idOrName));
      }
      @Override
      public List<GroupResource> find() {
        return groupCommands.query().find().stream()
            .map(this::map)
            .collect(Collectors.toList());
      }
    };
  }

  @Override
  public ProjectResource createOrUpdateProject(BatchProject project) {
    
    if(project.getId() != null) {
      RepoAssert.notNull(project.getRev(), () -> "Define rev with id!");
      ProjectResource resource = queryProjects().get(project.getId(), project.getRev());
      
      List<Group> removeGroups = resource.getGroups().values().stream()
          .filter(e -> !project.getGroups().contains(e.getId()))
          .collect(Collectors.toList());
      List<User> removeUsers = resource.getUsers().values().stream()
          .filter(e -> !project.getUsers().contains(e.getId()))
          .collect(Collectors.toList());

      List<String> addGroups = project.getGroups().stream()
          .filter(e -> !project.getGroups().contains(e))
          .collect(Collectors.toList());
      List<String> addUsers = project.getUsers().stream()
          .filter(e -> project.getUsers().contains(e))
          .collect(Collectors.toList());
      
      return ;
    }
    
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GroupResource createOrUpdateGroup(BatchGroup group) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public UserResource createOrUpdateUser(BatchUser user) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public BatchGroupUsersBuilder createGroupUsers() {
    return new BatchGroupUsersBuilder() {
      private String groupIdOrName;
      private boolean createUsersIfNotFound;
      private List<String> users = Collections.emptyList();
      @Override
      public BatchGroupUsersBuilder users(String... userIdOrExternalIdOrValue) {
        users = new ArrayList<>(Arrays.asList(userIdOrExternalIdOrValue));
        return this;
      }
      @Override
      public BatchGroupUsersBuilder groupId(String groupIdOrName) {
        this.groupIdOrName = groupIdOrName;
        return this;
      }
      @Override
      public BatchGroupUsersBuilder createUser(boolean createUsersIfNotFound) {
        this.createUsersIfNotFound = createUsersIfNotFound;
        return this;
      }
      @Override
      public GroupResource build() {
        Group group = groupCommands.query().any(groupIdOrName);
        List<User> foundUsers = new ArrayList<>();
        
        for(String userFilter : users) {
          // get user by: 
          //   * id/value/externalId 
          //   * create new user
          final var user = userCommands.query().find(userFilter)
            .orElseGet(() -> userCommands.query().findByValue(userFilter)
                .orElseGet(() -> userCommands.query().findByExternalId(userFilter)
                    .orElseGet(() -> {
                      if(createUsersIfNotFound) {
                        return userCommands.create().value(userFilter).build();  
                      }
                      throw new PmException(ImmutableConstraintViolation.builder()
                          .id(userFilter)
                          .rev("")
                          .constraint(ConstraintType.NOT_FOUND)
                          .type(ErrorType.USER)
                          .build(), "entity: 'user' not found one of the keys: 'name/externalId/id' = '" + userFilter + "'!");
                    })));
          
          foundUsers.add(user);
        }
        for(User user : foundUsers) {
          groupUserCommands.create().groupId(group.getId()).userId(user.getId()).build();
        }
        return queryGroups().get(groupIdOrName);
      }
    };
  }
}
