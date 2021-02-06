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
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.resys.hdes.projects.api.PmException;
import io.resys.hdes.projects.api.PmRepository.Access;
import io.resys.hdes.projects.api.PmRepository.Group;
import io.resys.hdes.projects.api.PmRepository.GroupType;
import io.resys.hdes.projects.api.PmRepository.GroupUser;
import io.resys.hdes.projects.api.PmRepository.Project;
import io.resys.hdes.projects.api.PmRepository.User;
import io.resys.hdes.projects.api.PmRepository.UserStatus;
import io.resys.hdes.projects.spi.mongodb.queries.MongoQuery;
import io.resys.hdes.projects.spi.mongodb.queries.MongoQuery.QueryResultWithAccess;
import io.resys.hdes.projects.spi.mongodb.queries.MongoQueryDefault;
import io.resys.hdes.projects.spi.mongodb.support.MongoWrapper;
import io.resys.hdes.projects.spi.support.RepoAssert;

public class MongoBuilderDelete implements MongoBuilder {
  private static final Logger LOGGER = LoggerFactory.getLogger(MongoBuilderDelete.class);
  
  private final MongoWrapper mongo;
  private final MongoQuery query;
  private final ImmutableMongoBuilderTree.Builder collect;
  
  public MongoBuilderDelete(MongoWrapper mongo) {
    this.mongo = mongo;
    this.query = new MongoQueryDefault(mongo);
    this.collect = ImmutableMongoBuilderTree.builder();
  }

  @Override
  public MongoBuilderTree build() {
    MongoBuilderTree tree = collect.build();
    if(LOGGER.isDebugEnabled()) {
      LOGGER.debug(new StringBuilder()
          .append("Tree has been DELETED: ").append(System.lineSeparator())
          .append(tree.toString()).toString());
    }
    return tree;
  }
  
  @Override
  public ProjectVisitor visitProject() {
    return new ProjectVisitor() {
      private String id;
      private String rev;
      
      @Override
      public Project build() throws PmException {
        RepoAssert.notEmptyAll(() -> "define id and rev!", id, rev);        
        
        QueryResultWithAccess<Project> queryResult = query.project().id(id).rev(rev).getWithFilter();
        mongo.getDb().getCollection(mongo.getConfig().getProjects(), Project.class)
          .deleteOne(queryResult.getFilter());
        
        mongo.getDb().getCollection(mongo.getConfig().getAccess(), Access.class)
          .deleteMany(query.access().project(queryResult.getValue().getId()).filters());
        
        collect.putProject(queryResult.getValue().getId(), queryResult.getValue());
        
        return queryResult.getValue();
      }
      @Override
      public ProjectVisitor visit(Project project) {
        return visitId(project.getId())
            .visitRev(project.getRev());
      }
      @Override
      public ProjectVisitor visitUsers(List<String> users) {
        return this;
      }
      @Override
      public ProjectVisitor visitGroups(List<String> groups) {
        return this;
      }
      @Override
      public ProjectVisitor visitName(String name) {
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
      
      @Override
      public Group build() {
        RepoAssert.notEmptyAll(() -> "define id and rev!", id, rev);
        
        QueryResultWithAccess<Group> queryResult = query.group().id(id).rev(rev).getWithFilter();
        mongo.getDb().getCollection(mongo.getConfig().getGroups(), Group.class)
          .deleteOne(queryResult.getFilter());
        
        mongo.getDb().getCollection(mongo.getConfig().getGroupUsers(), GroupUser.class)
          .deleteMany(query.groupUser().group(queryResult.getValue().getId()).filters());
        
        mongo.getDb().getCollection(mongo.getConfig().getAccess(), Access.class)
          .deleteMany(query.access().group(queryResult.getValue().getId()).filters());
        
        collect.putGroups(queryResult.getValue().getId(), queryResult.getValue());
        
        return queryResult.getValue();
      }
      @Override
      public GroupVisitor visit(Group entity) {
        return visitId(entity.getId())
            .visitRev(entity.getRev());
      }
      @Override
      public GroupVisitor visitUsers(List<String> users) {
        return this;
      }
      @Override
      public GroupVisitor visitProjects(List<String> projects) {
        return this;
      }
      @Override
      public GroupVisitor visitName(String name) {
        return this;
      }
      @Override
      public GroupVisitor visitMatcher(String matcher) {
        return this;
      }
      @Override
      public GroupVisitor visitType(GroupType type) {
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
      
      @Override
      public User build() {
        RepoAssert.notEmptyAll(() -> "define id and rev!", id, rev);
        
        QueryResultWithAccess<User> queryResult = query.user().id(id).rev(rev).getWithFilter();
        mongo.getDb().getCollection(mongo.getConfig().getUsers(), User.class)
          .deleteOne(queryResult.getFilter());
        
        mongo.getDb().getCollection(mongo.getConfig().getGroupUsers(), GroupUser.class)
          .deleteMany(query.groupUser().user(queryResult.getValue().getId()).filters());
        
        mongo.getDb().getCollection(mongo.getConfig().getAccess(), Access.class)
          .deleteMany(query.access().user(queryResult.getValue().getId()).filters());
        
        collect.putUser(queryResult.getValue().getId(), queryResult.getValue());
        
        return queryResult.getValue();
      }
      @Override
      public UserVisitor visitProjects(List<String> projects) {
        return this;
      }
      @Override
      public UserVisitor visitName(String name) {
        return this;
      }
      @Override
      public UserVisitor visitGroups(List<String> groups) {
        return this;
      }
      @Override
      public UserVisitor visitEmail(String email) {
        return this;
      }
      @Override
      public UserVisitor visitExternalId(Optional<String> externalId) {
        return this;
      }
      @Override
      public UserVisitor visitStatus(UserStatus status) {
        return this;
      }
      @Override
      public UserVisitor visit(User entity) {
        return visitId(entity.getId())
            .visitRev(entity.getRev());
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
      @Override
      public UserVisitor visitToken(String token) {
        return this;
      }
    };
  }

  @Override
  public GroupUserVisitor visitGroupUser() {
    return new GroupUserVisitor() {
      private String id;
      private String rev;
      
      @Override
      public GroupUser build() {
        RepoAssert.notEmptyAll(() -> "define id and rev!", id, rev);
        
        QueryResultWithAccess<GroupUser> queryResult = query.groupUser().id(id).rev(rev).getWithFilter();
        mongo.getDb().getCollection(mongo.getConfig().getGroupUsers(), GroupUser.class).deleteOne(queryResult.getFilter());
        collect.putGroupUsers(queryResult.getValue().getId(), queryResult.getValue());
        return queryResult.getValue();
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
            .visitRev(entity.getRev());
      } 
      @Override
      public GroupUserVisitor visitUser(String userId) {
        return this;
      }      
      @Override
      public GroupUserVisitor visitGroup(String groupId) {
        return this;
      }
    };
  }

  @Override
  public AccessVisitor visitAccess() {
    return new AccessVisitor() {
      private String id;
      private String rev;
      
      @Override
      public Access build() {
        RepoAssert.notEmptyAll(() -> "define id and rev!", id, rev);

        QueryResultWithAccess<Access> queryResult = query.access().id(id).rev(rev).getWithFilter();
        mongo.getDb().getCollection(mongo.getConfig().getAccess(), Access.class).deleteOne(queryResult.getFilter());
        collect.putAccess(queryResult.getValue().getId(), queryResult.getValue());
        
        
        return queryResult.getValue();
      }
      @Override
      public AccessVisitor visitUser(String userId) {
        return this;
      }
      @Override
      public AccessVisitor visitProject(String projectId) {
        return this;
      }
      @Override
      public AccessVisitor visitGroup(String groupId) {
        return this;
      }
      @Override
      public AccessVisitor visitComment(String comment) {
        return this;
      }
      @Override
      public AccessVisitor visit(Access entity) {
        return visitId(entity.getId())
            .visitRev(entity.getRev());
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
