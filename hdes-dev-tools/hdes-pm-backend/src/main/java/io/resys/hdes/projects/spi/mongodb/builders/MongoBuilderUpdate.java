package io.resys.hdes.projects.spi.mongodb.builders;

import java.util.ArrayList;

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
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.model.Updates;

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
import io.resys.hdes.projects.api.PmRepository.GroupUser;
import io.resys.hdes.projects.api.PmRepository.Project;
import io.resys.hdes.projects.api.PmRepository.User;
import io.resys.hdes.projects.api.PmRepository.UserStatus;
import io.resys.hdes.projects.spi.mongodb.codecs.AccessCodec;
import io.resys.hdes.projects.spi.mongodb.codecs.CodecUtil;
import io.resys.hdes.projects.spi.mongodb.codecs.GroupCodec;
import io.resys.hdes.projects.spi.mongodb.codecs.GroupUserCodec;
import io.resys.hdes.projects.spi.mongodb.codecs.ProjectCodec;
import io.resys.hdes.projects.spi.mongodb.codecs.UserCodec;
import io.resys.hdes.projects.spi.mongodb.queries.MongoQuery;
import io.resys.hdes.projects.spi.mongodb.queries.MongoQuery.QueryResultWithAccess;
import io.resys.hdes.projects.spi.mongodb.queries.MongoQueryDefault;
import io.resys.hdes.projects.spi.mongodb.support.MongoWrapper;
import io.resys.hdes.projects.spi.support.RepoAssert;

public class MongoBuilderUpdate implements MongoBuilder {
  private static final Logger LOGGER = LoggerFactory.getLogger(MongoBuilderUpdate.class);
  
  private final MongoWrapper mongo;
  private final MongoQuery query;
  private final ImmutableMongoBuilderTree.Builder collect;
  
  public MongoBuilderUpdate(MongoWrapper mongo) {
    this.mongo = mongo;
    this.query = new MongoQueryDefault(mongo);
    this.collect = ImmutableMongoBuilderTree.builder();
  }

  @Override
  public MongoBuilderTree build() {
    MongoBuilderTree tree = collect.build();
    if(LOGGER.isDebugEnabled()) {
      LOGGER.debug(new StringBuilder()
          .append("Tree has been UPDATED: ").append(System.lineSeparator())
          .append(tree.toString()).toString());
    }
    return tree;
  }
  
  @Override
  public ProjectVisitor visitProject() {
    return new ProjectVisitor() {
      private String id;
      private String rev;
      private String name;
      private List<String> users;
      private List<String> groups;
      
      @Override
      public Project build() throws PmException {
        RepoAssert.notEmptyAll(() -> "define id and rev!", id, rev);        
        RepoAssert.notEmpty(name, () -> "name not defined!");
        
        final var queryResult = query.project().id(id).rev(rev).getWithFilter();
        final Project project;
        if(name.equals(queryResult.getValue().getName())) {
          project = queryResult.getValue();
        } else {
          final var newRev = UUID.randomUUID().toString();
          mongo.getDb().getCollection(mongo.getConfig().getProjects(), Project.class)
            .updateOne(queryResult.getFilter(), Updates.combine(
              Updates.set(ProjectCodec.NAME, name), 
              Updates.set(CodecUtil.REV, newRev)));
          project = ImmutableProject.builder()
              .from(queryResult.getValue())
              .name(name).rev(newRev)
              .build();
        }
        collect.putProject(project.getId(), project);
        if(users == null && groups == null) {
          return project;
        }
        
        // Association updates
        final var currentAccess = query.access().project(id).findAll();
        
        if(users != null) {
          final var currentUsers = currentAccess.stream()
              .filter(a -> a.getUserId().isPresent())
              .map(a -> a.getUserId().get())
              .collect(Collectors.toList());

          // delete users
          final var deleteUsers = currentUsers.stream()
            .filter(a -> !users.contains(a))
            .collect(Collectors.toList());
          query.access().user(deleteUsers).delete();
          
          // create new users
          this.users.stream()
            .filter(userId -> !currentUsers.contains(userId))
            .map(id -> query.user().id(id).get())
            .forEach(user -> visitAccess().visitProject(project.getId()).visitUser(user.getId()).build());
        }
        
        if(groups != null) {
          final var currentGroups = currentAccess.stream()
              .filter(a -> a.getGroupId().isPresent())
              .map(a -> a.getGroupId().get())
              .collect(Collectors.toList());
        
          // delete groups
          final var deleteGroups = currentGroups.stream()
            .filter(a -> !groups.contains(a))
            .collect(Collectors.toList());
          query.access().group(deleteGroups).delete();
          
          
          // create new groups
          this.groups.stream()
            .filter(groupId -> !currentGroups.contains(groupId))
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
        this.users = users;
        return this;
      }
      @Override
      public ProjectVisitor visitGroups(List<String> groups) {
        this.groups = groups;
        return this;
      }
      @Override
      public ProjectVisitor visitName(String name) {
        this.name = name;
        return this;
      }
      @Override
      public ProjectVisitor visitRev(String rev) {
        this.rev = rev;
        return this;
      }
      @Override
      public ProjectVisitor visitId(String id) {
        this.id = id;
        return this;
      }
    };
  }
  @Override
  public GroupVisitor visitGroup() {
    return new GroupVisitor() {
      private String id;
      private String rev;
      private String name;
      private List<String> users;
      private List<String> projects;
      
      @Override
      public Group build() {
        RepoAssert.notEmptyAll(() -> "define id and rev!", id, rev);      
        RepoAssert.notEmpty(name, () -> "name not defined!");
        
        final var queryResult = query.group().id(id).rev(rev).getWithFilter();
        final Group group;
        
        if(name.equals(queryResult.getValue().getName())) {
          group = queryResult.getValue();
        } else {
          final var newRev = UUID.randomUUID().toString();
          mongo.getDb().getCollection(mongo.getConfig().getGroups(), Group.class)
            .updateOne(queryResult.getFilter(), Updates.combine(
              Updates.set(GroupCodec.NAME, name), 
              Updates.set(CodecUtil.REV, newRev)));
          
          group = ImmutableGroup.builder()
              .from(queryResult.getValue())
              .rev(newRev)
              .name(name)
              .build();
        }
        collect.putGroups(group.getId(), group);
        if(users == null && projects == null) {
          return group;
        }
        
        // Association updates
        final var currentAccess = query.access().group(id).findAll();
        
        if(users != null) {
          final var currentUsers = currentAccess.stream()
              .filter(a -> a.getUserId().isPresent())
              .map(a -> a.getUserId().get())
              .collect(Collectors.toList());

          // delete users
          final var deleteUsers = currentUsers.stream()
            .filter(a -> !users.contains(a))
            .collect(Collectors.toList());
          query.access().user(deleteUsers).delete();
          
          // create new users
          this.users.stream()
            .filter(userId -> !currentUsers.contains(userId))
            .map(id -> query.user().id(id).get())
            .forEach(user -> visitGroupUser().visitGroup(group.getId()).visitUser(user.getId()).build());
        }
        if(projects != null) {
          final var currentProjects = currentAccess.stream()
              .map(a -> a.getProjectId())
              .collect(Collectors.toList());

          // delete projects
          final var deleteProjects = currentProjects.stream()
            .filter(a -> !projects.contains(a))
            .collect(Collectors.toList());
          query.access().project(deleteProjects).delete();
          
          
          // create new projects
          this.projects.stream()
            .filter(projectId -> !currentProjects.contains(projectId))
            .map(id -> query.project().id(id).get())
            .forEach(project -> visitAccess().visitProject(project.getId()).visitGroup(group.getId()).build());
        }
        
        return group;
      }
      @Override
      public GroupVisitor visit(Group entity) {
        return visitId(entity.getId())
            .visitRev(entity.getRev())
            .visitName(entity.getName());
      }
      @Override
      public GroupVisitor visitUsers(List<String> users) {
        this.users = users;
        return this;
      }
      @Override
      public GroupVisitor visitProjects(List<String> projects) {
        this.projects = projects;
        return this;
      }
      @Override
      public GroupVisitor visitName(String name) {
        this.name = name;
        return this;
      }
      @Override
      public GroupVisitor visitRev(String rev) {
        this.rev = rev;
        return this;
      }
      @Override
      public GroupVisitor visitId(String id) {
        this.id = id;
        return this;
      }
    };
  }
  @Override
  public UserVisitor visitUser() {
    return new UserVisitor() {
      private String id;
      private String rev;
      private String name;
      private String email;
      private Optional<String> externalId;
      private String token;
      private UserStatus status;
      private List<String> groups;
      private List<String> projects;
      
      @Override
      public User build() {
        RepoAssert.notEmptyAll(() -> "define id and rev!", id, rev);    
        QueryResultWithAccess<User> queryResult = query.user().id(id).rev(rev).getWithFilter();
        
        User oldValue = queryResult.getValue();
        if(token != null && token.equals(oldValue.getToken()) && query.user().token(token).findOne().isPresent()) {
          throw new PmException(ImmutableConstraintViolation.builder()
              .id(queryResult.getValue().getId())
              .rev(queryResult.getValue().getRev())
              .constraint(ConstraintType.NOT_UNIQUE)
              .type(ErrorType.USER)
              .build(), () -> "entity: 'user' with token: '" + token + "' already exists!");
        }
        
        final User user;
        if(queryResult.getValue().equals(
            ImmutableUser.builder().from(queryResult.getValue())
            .name(name == null ? oldValue.getName() : name)
            .email(email == null ? oldValue.getEmail() : email)
            .token(token == null ? oldValue.getToken() : token)
            .status(status == null ? oldValue.getStatus() : status)
            .externalId(externalId == null ? oldValue.getExternalId() : externalId)
            .build())) {
          
          user = queryResult.getValue();
        } else {
          final var newRev = UUID.randomUUID().toString();
          final List<Bson> updates = new ArrayList<>();
          updates.add(Updates.set(CodecUtil.REV, newRev));
          
          if(name != null && !name.equals(oldValue.getName())) {
            updates.add(Updates.set(UserCodec.NAME, name));
          }
          if(email != null && !email.equals(oldValue.getEmail())) {
            updates.add(Updates.set(UserCodec.EMAIL, email));
          }
          if(externalId != null && !externalId.equals(oldValue.getExternalId())) {
            updates.add(Updates.set(UserCodec.EXTERNAL_ID, externalId));
          }
          if(token != null && !token.equals(oldValue.getToken())) {
            updates.add(Updates.set(UserCodec.TOKEN, token));
          }
          if(status != null && !status.equals(oldValue.getStatus())) {
            updates.add(Updates.set(UserCodec.STATUS, status.name()));
          }
          mongo.getDb().getCollection(mongo.getConfig().getUsers(), User.class)
          .updateOne(queryResult.getFilter(), Updates.combine(updates));
        
          user = query.user().id(id).get();          
        }
        
        if(groups == null && projects == null) {
          return user;
        }
        
        // Association updates
        final var currentAccess = query.access().user(id).findAll();
        
        if(groups != null) {
          final var currentGroups = currentAccess.stream()
              .filter(a -> a.getGroupId().isPresent())
              .map(a -> a.getGroupId().get())
              .collect(Collectors.toList());

          // delete groups
          final var deleteGroups = currentGroups.stream()
            .filter(a -> !groups.contains(a))
            .collect(Collectors.toList());
          query.access().group(deleteGroups).delete();
          
          // create new groups
          this.groups.stream()
            .filter(groupId -> !currentGroups.contains(groupId))
            .map(id -> query.group().id(id).get())
            .forEach(group -> visitGroupUser().visitGroup(group.getId()).visitUser(user.getId()).build());
        }
        if(projects != null) {
          final var currentProjects = currentAccess.stream()
              .map(a -> a.getProjectId())
              .collect(Collectors.toList());

          // delete project
          final var deleteProjects = currentProjects.stream()
            .filter(a -> !projects.contains(a))
            .collect(Collectors.toList());
          query.access().project(deleteProjects).delete();
          
          // create new projects
          this.projects.stream()
            .filter(projectId -> !currentProjects.contains(projectId))
            .map(id -> query.project().id(id).get())
            .forEach(project -> visitAccess().visitProject(project.getId()).visitUser(user.getId()).build());
        }
        return user;
      }
      @Override
      public UserVisitor visitProjects(List<String> projects) {
        this.projects = projects;
        return this;
      }
      @Override
      public UserVisitor visitName(String name) {
        this.name = name;
        return this;
      }
      @Override
      public UserVisitor visitGroups(List<String> groups) {
        this.groups = groups;
        return this;
      }
      @Override
      public UserVisitor visitEmail(String email) {
        this.email = email;
        return this;
      }
      @Override
      public UserVisitor visitToken(String token) {
        this.token = token;
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
      public UserVisitor visit(User entity) {
        return visitId(entity.getId())
            .visitRev(entity.getRev())
            .visitName(entity.getName())
            .visitStatus(entity.getStatus())
            .visitToken(entity.getToken())
            .visitExternalId(entity.getExternalId())
            .visitEmail(entity.getEmail());
      }
      @Override
      public UserVisitor visitRev(String rev) {
        this.rev = rev;
        return this;
      }
      @Override
      public UserVisitor visitId(String id) {
        this.id = id;
        return this;
      }
    };
  }

  @Override
  public GroupUserVisitor visitGroupUser() {
    return new GroupUserVisitor() {
      private String id;
      private String rev;
      private String userId;
      private String groupId;
      @Override
      public GroupUser build() {
        RepoAssert.notEmptyAll(() -> "define id and rev!", id, rev);        
        RepoAssert.notEmpty(userId, () -> "userId not defined!");
        RepoAssert.notEmpty(groupId, () -> "groupId not defined!");
        
        QueryResultWithAccess<GroupUser> queryResult = query.groupUser().id(id).rev(rev).getWithFilter();
        final var newRev = UUID.randomUUID().toString();
        
        mongo.getDb().getCollection(mongo.getConfig().getGroupUsers(), GroupUser.class)
          .updateOne(queryResult.getFilter(), Updates.combine(
            Updates.set(GroupUserCodec.USER_ID, userId), 
            Updates.set(GroupUserCodec.GROUP_ID, groupId), 
            Updates.set(CodecUtil.REV, newRev)));
        
        GroupUser groupUser = ImmutableGroupUser.builder()
            .from(queryResult.getValue())
            .rev(newRev)
            .userId(userId)
            .groupId(groupId)
            .build();
        collect.putGroupUsers(groupUser.getId(), groupUser);
        return groupUser;
      }
      @Override
      public GroupUserVisitor visitRev(String rev) {
        this.rev = rev;
        return this;
      }
      @Override
      public GroupUserVisitor visitId(String id) {
        this.id = id;
        return this;
      }
      @Override
      public GroupUserVisitor visit(GroupUser entity) {
        return visitId(entity.getId())
            .visitRev(entity.getRev())
            .visitUser(entity.getUserId())
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
      private String id;
      private String rev;
      private String userId;
      private String groupId;
      private String projectId;
      private String comment;
      
      @Override
      public Access build() {
        RepoAssert.notEmptyAll(() -> "define id and rev!", id, rev);        
        RepoAssert.notEmptyAtLeastOne(() -> "userId or groupId not defined!", groupId, userId);
        RepoAssert.notEmpty(projectId, () -> "projectId not defined!");
        
        QueryResultWithAccess<Access> queryResult = query.access().id(id).rev(rev).getWithFilter();
        final var newRev = UUID.randomUUID().toString();
        
        mongo.getDb().getCollection(mongo.getConfig().getAccess(), Access.class)
          .updateOne(queryResult.getFilter(), Updates.combine(
            Updates.set(AccessCodec.GROUP_ID, groupId), 
            Updates.set(AccessCodec.USER_ID, userId),
            Updates.set(AccessCodec.PROJECT_ID, projectId),
            Updates.set(AccessCodec.COMMENT, comment),
            Updates.set(CodecUtil.REV, newRev)));
        
        Access access = ImmutableAccess.builder()
            .from(queryResult.getValue())
            .rev(newRev)
            .userId(userId)
            .groupId(groupId)
            .projectId(projectId)
            .comment(comment)
            .build();
        
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
        return visitId(entity.getId())
            .visitRev(entity.getRev())
            .visitComment(entity.getComment().orElse(null))
            .visitGroup(entity.getGroupId().orElse(null))
            .visitUser(entity.getUserId().orElse(null))
            .visitProject(entity.getProjectId());
      }
      @Override
      public AccessVisitor visitRev(String rev) {
        this.rev = rev;
        return this;
      }
      @Override
      public AccessVisitor visitId(String id) {
        this.id = id;
        return this;
      }
    };
  }
}
