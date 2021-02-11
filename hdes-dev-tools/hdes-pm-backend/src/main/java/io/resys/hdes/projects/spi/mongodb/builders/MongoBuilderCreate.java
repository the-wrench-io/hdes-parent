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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.resys.hdes.projects.api.ImmutableAccess;
import io.resys.hdes.projects.api.ImmutableConstraintViolation;
import io.resys.hdes.projects.api.ImmutableGroup;
import io.resys.hdes.projects.api.ImmutableGroupUser;
import io.resys.hdes.projects.api.ImmutableProject;
import io.resys.hdes.projects.api.ImmutableUser;
import io.resys.hdes.projects.api.PmException;
import io.resys.hdes.projects.api.PmException.ConstraintType;
import io.resys.hdes.projects.api.PmException.ErrorType;
import io.resys.hdes.projects.api.PmRepository.Access;
import io.resys.hdes.projects.api.PmRepository.Group;
import io.resys.hdes.projects.api.PmRepository.GroupType;
import io.resys.hdes.projects.api.PmRepository.GroupUser;
import io.resys.hdes.projects.api.PmRepository.Project;
import io.resys.hdes.projects.api.PmRepository.User;
import io.resys.hdes.projects.api.PmRepository.UserStatus;
import io.resys.hdes.projects.spi.mongodb.queries.MongoQuery;
import io.resys.hdes.projects.spi.mongodb.queries.MongoQueryDefault;
import io.resys.hdes.projects.spi.mongodb.support.MongoWrapper;
import io.resys.hdes.projects.spi.support.RepoAssert;

public class MongoBuilderCreate implements MongoBuilder {
  private static final Logger LOGGER = LoggerFactory.getLogger(MongoBuilderCreate.class);
  
  private final MongoWrapper mongo;
  private final MongoQuery query;
  private final ImmutableMongoBuilderTree.Builder collect;
  
  public MongoBuilderCreate(MongoWrapper mongo) {
    this.mongo = mongo;
    this.query = new MongoQueryDefault(mongo);
    this.collect = ImmutableMongoBuilderTree.builder();
  }

  @Override
  public MongoBuilderTree build() {
    MongoBuilderTree tree = collect.build();
    if(LOGGER.isDebugEnabled()) {
      LOGGER.debug(new StringBuilder()
          .append("Tree has been CREATED: ").append(System.lineSeparator())
          .append(tree.toString()).toString());
    }
    return tree;
  }
  
  @Override
  public ProjectVisitor visitProject() {
    return new ProjectVisitor() {
      private String name;
      private Set<String> users;
      private Set<String> groups;
      
      @Override
      public Project build() throws PmException {
        RepoAssert.notEmpty(name, () -> "name not defined!");
        Optional<Project> conflict = query.project().name(name).findOne();
        if(conflict.isPresent()) {
          throw new PmException(ImmutableConstraintViolation.builder()
              .id(conflict.get().getId())
              .rev(conflict.get().getRev())
              .constraint(ConstraintType.NOT_UNIQUE)
              .type(ErrorType.PROJECT)
              .build(), () -> "entity: 'project' with name: '" + name + "' already exists!");
        }
        
        final var project = ImmutableProject.builder()
            .id(UUID.randomUUID().toString())
            .rev(UUID.randomUUID().toString())
            .name(name)
            .created(LocalDateTime.now())
            .build();
        
        mongo.getDb().getCollection(mongo.getConfig().getProjects(), Project.class).insertOne(project);
        collect.putProject(project.getId(), project);
        
        if(users != null) {
          this.users.stream()
            .map(id -> query.user().id(id).get())
            .forEach(user -> visitAccess().visitProject(project.getId()).visitUser(user.getId()).build());
        }
        if(groups != null) {
          this.groups.stream()
            .map(id -> query.group().id(id).get())
            .forEach(group -> visitAccess().visitProject(project.getId()).visitGroup(group.getId()).build());
        }
        return project;
      }
      @Override
      public ProjectVisitor visit(Project project) {
        return visitId(project.getId())
            .visitRev(project.getRev())
            .visitName(project.getName());
      }
      @Override
      public ProjectVisitor visitUsers(List<String> users) {
        this.users = users == null ? null : new HashSet<>(users);
        return this;
      }
      @Override
      public ProjectVisitor visitGroups(List<String> groups) {
        this.groups = groups == null ? null : new HashSet<>(groups);
        return this;
      }
      @Override
      public ProjectVisitor visitName(String name) {
        this.name = name;
        return this;
      }
      @Override
      public ProjectVisitor visitRev(String rev) {
        RepoAssert.fail(() -> "rev can't be defined!");
        return this;
      }
      @Override
      public ProjectVisitor visitId(String id) {
        RepoAssert.fail(() -> "id can't be defined!");
        return this;
      }
    };
  }
  @Override
  public GroupVisitor visitGroup() {
    return new GroupVisitor() {
      private String name;
      private Set<String> users;
      private Set<String> projects;
      private GroupType type;
      private String matcher;
      
      @Override
      public Group build() {
        RepoAssert.notEmpty(name, () -> "name not defined!");
        Optional<Group> conflict = query.group().name(name).findOne();
        if(conflict.isPresent()) {
          throw new PmException(ImmutableConstraintViolation.builder()
              .id(conflict.get().getId())
              .rev(conflict.get().getRev())
              .constraint(ConstraintType.NOT_UNIQUE)
              .type(ErrorType.GROUP)
              .build(), () -> "entity: 'group' with name: '" + name + "' already exists!");
        }
        
        final var matcher = this.matcher == null || this.matcher.isBlank() ? null : this.matcher;
        if(matcher != null) {
          try {
            Pattern.compile(matcher);
          } catch(PatternSyntaxException e) {
            throw new PmException(ImmutableConstraintViolation.builder()
                .id("not-created")
                .rev("not-created")
                .constraint(ConstraintType.INVALID_DATA)
                .type(ErrorType.GROUP)
                .build(), () -> "entity: 'group' with name: '" + name + "' has error in the matcher: '" + matcher + "', " + e.getMessage() + "!");
          }
        }
        
        
        final var type = this.type != null ? this.type : GroupType.USER;
        final var group = ImmutableGroup.builder()
            .id(UUID.randomUUID().toString())
            .rev(UUID.randomUUID().toString())
            .name(name)
            .type(type)
            .matcher(Optional.ofNullable(matcher))
            .created(LocalDateTime.now())
            .build();
        
        mongo.getDb().getCollection(mongo.getConfig().getGroups(), Group.class).insertOne(group);
        collect.putGroups(group.getId(), group);
        
        if(users != null) {
          this.users.stream()
            .map(id -> query.user().id(id).get())
            .forEach(user -> visitGroupUser().visitGroup(group.getId()).visitUser(user.getId()).build());
        }
        
        // Save only for user projects, ADMIN groups have access to everything
        if(projects != null && type == GroupType.USER) {
          this.projects.stream()
            .map(id -> query.project().id(id).get())
            .forEach(project -> visitAccess().visitProject(project.getId()).visitGroup(group.getId()).build());
        }
        return group;
      }

      @Override
      public GroupVisitor visitType(@Nullable GroupType type) {
        this.type = type;
        return this;
      }
      @Override
      public GroupVisitor visit(Group entity) {
        return visitName(entity.getName())
            .visitType(entity.getType())
            .visitMatcher(entity.getMatcher().orElse(null));
      }
      @Override
      public GroupVisitor visitUsers(List<String> users) {
        this.users = users == null ? null : new HashSet<>(users);
        return this;
      }
      @Override
      public GroupVisitor visitProjects(List<String> projects) {
        this.projects = projects == null ? null : new HashSet<>(projects);
        return this;
      }
      @Override
      public GroupVisitor visitName(String name) {
        this.name = name;
        return this;
      }
      @Override
      public GroupVisitor visitMatcher(String matcher) {
        this.matcher = matcher;
        return this;
      }
      @Override
      public GroupVisitor visitRev(String rev) {
        RepoAssert.fail(() -> "rev can't be defined!");
        return this;
      }
      @Override
      public GroupVisitor visitId(String id) {
        RepoAssert.fail(() -> "id can't be defined!");
        return this;
      }
    };
  }
  @Override
  public UserVisitor visitUser() {
    return new UserVisitor() {
      private String name;
      private String email;
      private Optional<String> externalId;
      private UserStatus status;
      private Set<String> groups;
      private Set<String> projects;
      
      @Override
      public User build() {
        RepoAssert.notEmpty(name, () -> "name not defined!");

        Optional<User> conflict = query.user().name(name).findOne();
        if(conflict.isPresent()) {
          throw new PmException(ImmutableConstraintViolation.builder()
              .id(conflict.get().getId())
              .rev(conflict.get().getRev())
              .constraint(ConstraintType.NOT_UNIQUE)
              .type(ErrorType.USER)
              .build(), () -> "entity: 'user' with name: '" + name + "' already exists!");
        }
        
        Set<String> groupMatches = query.group().matches(name, email).stream()
            .map(g -> g.getId())
            .collect(Collectors.toSet());
        if(!groupMatches.isEmpty()) {
          if(groups == null) {
            groups = new HashSet<>();
          }
          groups.addAll(groupMatches);
        }
        
        
        final var user = ImmutableUser.builder()
            .id(UUID.randomUUID().toString())
            .rev(UUID.randomUUID().toString())
            .name(name)
            .externalId(externalId == null ? Optional.empty() : externalId)
            .email(Optional.ofNullable(email))
            .status(status == null ? UserStatus.PENDING : status)
            .token(UUID.randomUUID().toString())
            .created(LocalDateTime.now())
            .build();
        
        mongo.getDb().getCollection(mongo.getConfig().getUsers(), User.class).insertOne(user);
        collect.putUser(user.getId(), user);
        
        if(groups != null) {
          this.groups.stream()
            .map(id -> query.group().id(id).get())
            .forEach(group -> visitGroupUser().visitGroup(group.getId()).visitUser(user.getId()).build());
        }
        if(projects != null) {
          this.projects.stream()
            .map(id -> query.project().id(id).get())
            .forEach(project -> visitAccess().visitProject(project.getId()).visitUser(user.getId()).build());
        }
        return user;
      }
      @Override
      public UserVisitor visitProjects(List<String> projects) {
        this.projects = projects == null ? null : new HashSet<>(projects);
        return this;
      }
      @Override
      public UserVisitor visitName(String name) {
        this.name = name;
        return this;
      }
      @Override
      public UserVisitor visitGroups(List<String> groups) {
        this.groups = groups == null ? null : new HashSet<>(groups);
        return this;
      }
      @Override
      public UserVisitor visitEmail(String email) {
        this.email = email;
        return this;
      }
      @Override
      public UserVisitor visitExternalId(Optional<String> externalId) {
        this.externalId = externalId;
        return this;
      }
      @Override
      public UserVisitor visitStatus(UserStatus status) {
        this.status = status;
        return this;
      }
      @Override
      public UserVisitor visitToken(String token) {
        RepoAssert.fail(() -> "token can't be defined!");
        return this;
      }
      @Override
      public UserVisitor visit(User entity) {
        return visitName(entity.getName())
            .visitStatus(entity.getStatus())
            .visitExternalId(entity.getExternalId())
            .visitEmail(entity.getEmail().orElse(null));
      }
      @Override
      public UserVisitor visitRev(String rev) {
        RepoAssert.fail(() -> "rev can't be defined!");
        return this;
      }
      @Override
      public UserVisitor visitId(String id) {
        RepoAssert.fail(() -> "id can't be defined!");
        return this;
      }
    };
  }

  @Override
  public GroupUserVisitor visitGroupUser() {
    return new GroupUserVisitor() {
      private String userId;
      private String groupId;
      @Override
      public GroupUser build() {
        RepoAssert.notEmpty(userId, () -> "userId not defined!");
        RepoAssert.notEmpty(groupId, () -> "groupId not defined!");
        Optional<GroupUser> conflict = query.groupUser().user(userId).group(groupId).findOne();
        if(conflict.isPresent()) {
          throw new PmException(ImmutableConstraintViolation.builder()
              .id(conflict.get().getId())
              .rev(conflict.get().getRev())
              .constraint(ConstraintType.NOT_UNIQUE)
              .type(ErrorType.GROUP_USER)
              .build(), () -> "entity: 'group user' with userId: '" + userId + "' and groupId: '" + groupId + "' already exists!");
        }
        
        GroupUser groupUser = ImmutableGroupUser.builder()
            .id(UUID.randomUUID().toString())
            .rev(UUID.randomUUID().toString())
            .userId(userId)
            .groupId(groupId)
            .created(LocalDateTime.now())
            .build();
        
        mongo.getDb().getCollection(mongo.getConfig().getGroupUsers(), GroupUser.class).insertOne(groupUser);
        collect.putGroupUsers(groupUser.getId(), groupUser);
        return groupUser;
      }
      @Override
      public GroupUserVisitor visitRev(String rev) {
        RepoAssert.fail(() -> "rev can't be defined!");
        return this;
      }
      @Override
      public GroupUserVisitor visitId(String id) {
        RepoAssert.fail(() -> "id can't be defined!");
        return this;
      }
      @Override
      public GroupUserVisitor visit(GroupUser entity) {
        return visitUser(entity.getUserId())
            .visitGroup(entity.getGroupId());
      } 
      @Override
      public GroupUserVisitor visitUser(String userId) {
        this.userId = userId;
        return this;
      }      
      @Override
      public GroupUserVisitor visitGroup(String groupId) {
        this.groupId = groupId;
        return this;
      }
    };
  }

  @Override
  public AccessVisitor visitAccess() {
    return new AccessVisitor() {
      private String userId;
      private String groupId;
      private String projectId;
      private String comment;
      
      @Override
      public Access build() {
        RepoAssert.notEmptyAtLeastOne(() -> "userId or groupId not defined!", groupId, userId);
        RepoAssert.notEmpty(projectId, () -> "projectId not defined!");
        
        Optional<Access> conflict = query.access().project(projectId).user(userId).group(groupId).findOne();
        if(conflict.isPresent()) {
          throw new PmException(ImmutableConstraintViolation.builder()
              .id(conflict.get().getId())
              .rev(conflict.get().getRev())
              .constraint(ConstraintType.NOT_UNIQUE)
              .type(ErrorType.ACCESS)
              .build(), () -> "entity: 'access' with "
                  + "projectId: '" + projectId + "' and "
                  + "userId: '" + userId + "' and "
                  + "groupId: '" + groupId  + "' and "
                  + "userId: '" + userId  + "' already exists!");
        }
        
        Access access = ImmutableAccess.builder()
            .id(UUID.randomUUID().toString())
            .rev(UUID.randomUUID().toString())
            .userId(Optional.ofNullable(userId))
            .groupId(Optional.ofNullable(groupId))
            .projectId(projectId)
            .comment(Optional.ofNullable(comment))
            .created(LocalDateTime.now())
            .build();
        
        mongo.getDb().getCollection(mongo.getConfig().getAccess(), Access.class).insertOne(access);
        collect.putAccess(access.getId(), access);
        return access;
      }
      @Override
      public AccessVisitor visitUser(String userId) {
        this.userId = userId;
        return this;
      }
      @Override
      public AccessVisitor visitProject(String projectId) {
        this.projectId = projectId;
        return this;
      }
      @Override
      public AccessVisitor visitGroup(String groupId) {
        this.groupId = groupId;
        return this;
      }
      @Override
      public AccessVisitor visitComment(String comment) {
        this.comment = comment;
        return this;
      }
      @Override
      public AccessVisitor visit(Access entity) {
        return visitComment(entity.getComment().orElse(null))
            .visitGroup(entity.getGroupId().orElse(null))
            .visitUser(entity.getUserId().orElse(null))
            .visitProject(entity.getProjectId());
      }
      @Override
      public AccessVisitor visitRev(String rev) {
        RepoAssert.fail(() -> "rev can't be defined!");
        return this;
      }
      @Override
      public AccessVisitor visitId(String id) {
        RepoAssert.fail(() -> "id can't be defined!");
        return this;
      }
    };
  }
}
