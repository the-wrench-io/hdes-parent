package io.resys.hdes.projects.spi.mongodb.visitors;

import java.util.function.Consumer;

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

import org.bson.conversions.Bson;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import io.resys.hdes.projects.api.ImmutableConstraintViolation;
import io.resys.hdes.projects.api.ImmutableRevisionConflict;
import io.resys.hdes.projects.api.PmException;
import io.resys.hdes.projects.api.PmException.ConstraintType;
import io.resys.hdes.projects.api.PmException.ErrorType;
import io.resys.hdes.projects.api.PmRepository.Access;
import io.resys.hdes.projects.api.PmRepository.Group;
import io.resys.hdes.projects.api.PmRepository.GroupUser;
import io.resys.hdes.projects.api.PmRepository.Project;
import io.resys.hdes.projects.api.PmRepository.User;
import io.resys.hdes.projects.api.PmRevException;
import io.resys.hdes.projects.api.PmRevException.RevisionType;
import io.resys.hdes.projects.spi.mongodb.ImmutablePersistedEntities;
import io.resys.hdes.projects.spi.mongodb.PersistentCommand.EntityVisitor;
import io.resys.hdes.projects.spi.mongodb.PersistentCommand.MongoDbConfig;
import io.resys.hdes.projects.spi.mongodb.codecs.AccessCodec;
import io.resys.hdes.projects.spi.mongodb.codecs.GroupCodec;
import io.resys.hdes.projects.spi.mongodb.codecs.GroupUserCodec;
import io.resys.hdes.projects.spi.mongodb.codecs.ProjectCodec;
import io.resys.hdes.projects.spi.mongodb.codecs.UserCodec;
import io.resys.hdes.projects.spi.support.RepoAssert;

public class DeleteEntityVisitor implements EntityVisitor {

  private final MongoClient client;
  private final MongoDbConfig config;
  private final ImmutablePersistedEntities.Builder collect;
  
  
  public DeleteEntityVisitor(MongoClient client, MongoDbConfig config, ImmutablePersistedEntities.Builder collect) {
    super();
    this.client = client;
    this.config = config;
    this.collect = collect;
  }

  public static Builder builder() {
    return new Builder();
  }
    
  public static class Builder {
    private ImmutablePersistedEntities.Builder collect;
    private MongoDbConfig config;
    private MongoClient client;
    
    public Builder collect(ImmutablePersistedEntities.Builder collect) {
      this.collect = collect;
      return this;
    }
    public Builder client(MongoClient client) {
      this.client = client;
      return this;
    }  
    public Builder config(MongoDbConfig config) {
      this.config = config;
      return this;
    }
    public DeleteEntityVisitor build() {
      RepoAssert.notNull(collect, () -> "collect not defined!");
      RepoAssert.notNull(client, () -> "client not defined!");
      RepoAssert.notNull(config, () -> "config not defined!");
      
      return new DeleteEntityVisitor(client, config, collect);
    }
  }

  @Override
  public Project visitProject(Project project) {
    final MongoCollection<Project> collection = client
        .getDatabase(config.getDb())
        .getCollection(config.getProjects(), Project.class);
    
    final Bson filter = Filters.eq(ProjectCodec.ID, project.getId());
    final Project value = collection.find(filter).first();
    
    if(value == null) {
      throw new PmException(ImmutableConstraintViolation.builder()
          .id(project.getId())
          .rev(project.getRev())
          .constraint(ConstraintType.NOT_FOUND)
          .type(ErrorType.PROJECT)
          .build(), "entity not found: 'project' with id: '" + project.getId() + "'!");
    }
    
    if(!value.getRev().equals(project.getRev())) {
      throw new PmRevException(ImmutableRevisionConflict.builder()
          .id(project.getId())
          .revToUpdate(project.getRev())
          .rev(value.getRev())
          .build(), "revision conflict: 'project' with id: '" + project.getId() + "', revs: " + project.getRev() + " != " + value.getRev() + "!");
    }
    collection.deleteOne(filter);
    
    // Delete all access associated with the project
    Consumer<Access> consumer = access -> visitAccess(access);
    
    client
      .getDatabase(config.getDb())
      .getCollection(config.getAccess(), Access.class)
      .find(Filters.eq(AccessCodec.PROJECT_ID, project.getId()))
      .forEach(consumer);
    
    return project;
  }

  @Override
  public Access visitAccess(Access access) {
    final MongoCollection<Access> collection = client
        .getDatabase(config.getDb())
        .getCollection(config.getAccess(), Access.class);
    
    final Bson filter = Filters.eq(AccessCodec.ID, access.getId());
    final Access value = collection.find(filter).first();
    
    if(value == null) {
      throw new PmException(ImmutableConstraintViolation.builder()
          .id(access.getId())
          .rev(access.getRev())
          .constraint(ConstraintType.NOT_FOUND)
          .type(ErrorType.ACCESS)
          .build(), "entity not found: 'access' with id: '" + access.getId() + "'!");
    }
    
    if(!value.getRev().equals(access.getRev())) {
      throw new PmRevException(ImmutableRevisionConflict.builder()
          .id(access.getId())
          .revToUpdate(access.getRev())
          .rev(value.getRev())
          .build(), "revision conflict: 'access' with id: '" + access.getId() + "', revs: " + access.getRev() + " != " + value.getRev() + "!");
    }
    
    
    collection.deleteOne(filter);
    collect.putAccess(access.getId(), access);
    return access;
  }

  @Override
  public User visitUser(User user) {
    final MongoCollection<Access> collection = client
        .getDatabase(config.getDb())
        .getCollection(config.getAccess(), Access.class);
    
    final Bson filter = Filters.eq(UserCodec.ID, user.getId());
    final Access value = collection.find(filter).first();
    
    if(value == null) {
      throw new PmException(ImmutableConstraintViolation.builder()
          .id(user.getId())
          .rev(user.getRev())
          .constraint(ConstraintType.NOT_FOUND)
          .type(ErrorType.USER)
          .build(), "entity not found: 'user' with id: '" + user.getId() + "'!");
    }
    
    if(!value.getRev().equals(user.getRev())) {
      throw new PmRevException(ImmutableRevisionConflict.builder()
          .id(user.getId())
          .revToUpdate(user.getRev())
          .rev(value.getRev())
          .build(), "revision conflict: 'user' with id: '" + user.getId() + "', revs: " + user.getRev() + " != " + value.getRev() + "!");
    }
    
    // Delete all access associated with the user
    Consumer<Access> consumer = access -> visitAccess(access);
    client
      .getDatabase(config.getDb())
      .getCollection(config.getAccess(), Access.class)
      .find(Filters.eq(AccessCodec.USER_ID, user.getId()))
      .forEach(consumer);

    collection.deleteOne(filter);
    collect.putUser(user.getId(), user);
    return user;
  }

  @Override
  public Group visitGroup(Group group) {
    final MongoCollection<Group> collection = client
        .getDatabase(config.getDb())
        .getCollection(config.getGroups(), Group.class);
    
    final Bson filter = Filters.eq(GroupCodec.ID, group.getId());
    final Group value = collection.find(filter).first();
    
    if(value == null) {
      throw new PmException(ImmutableConstraintViolation.builder()
          .id(group.getId())
          .rev(group.getRev())
          .constraint(ConstraintType.NOT_FOUND)
          .type(ErrorType.GROUP)
          .build(), "entity: 'group' not found with id: '" + group.getId() + "'!");
    }
    
    if(!value.getRev().equals(group.getRev())) {
      throw new PmRevException(ImmutableRevisionConflict.builder()
          .id(group.getId())
          .type(RevisionType.GROUP)
          .revToUpdate(group.getRev())
          .rev(value.getRev())
          .build(), "revision conflict: 'group' with id: '" + group.getId() + "', revs: " + group.getRev() + " != " + value.getRev() + "!");
    }
    collection.deleteOne(filter);
    
    // Delete all group users associated with the project
    Consumer<GroupUser> consumerGroupUser = groupUser -> visitGroupUser(groupUser);
    client
      .getDatabase(config.getDb())
      .getCollection(config.getGroupUsers(), GroupUser.class)
      .find(Filters.eq(GroupUserCodec.GROUP_ID, group.getId()))
      .forEach(consumerGroupUser);
    
    
    // Delete all access associated with the group
    Consumer<Access> consumerAccess = access -> visitAccess(access);
    client
      .getDatabase(config.getDb())
      .getCollection(config.getAccess(), Access.class)
      .find(Filters.eq(AccessCodec.GROUP_ID, group.getId()))
      .forEach(consumerAccess);
    
    return group;
  }
  @Override
  public GroupUser visitGroupUser(GroupUser groupUser) {
    final MongoCollection<GroupUser> collection = client
        .getDatabase(config.getDb())
        .getCollection(config.getGroupUsers(), GroupUser.class);
    
    final Bson filter = Filters.eq(GroupUserCodec.ID, groupUser.getId());
    final GroupUser value = collection.find(filter).first();
    
    if(value == null) {
      throw new PmException(ImmutableConstraintViolation.builder()
          .id(groupUser.getId())
          .rev(groupUser.getRev())
          .constraint(ConstraintType.NOT_FOUND)
          .type(ErrorType.GROUP_USER)
          .build(), "entity: 'group user' not found with id: '" + groupUser.getId() + "'!");
    }
    
    if(!value.getRev().equals(groupUser.getRev())) {
      throw new PmRevException(ImmutableRevisionConflict.builder()
          .id(groupUser.getId())
          .type(RevisionType.GROUP_USER)
          .revToUpdate(groupUser.getRev())
          .rev(value.getRev())
          .build(), "revision conflict: 'group user' with id: '" + groupUser.getId() + "', revs: " + groupUser.getRev() + " != " + value.getRev() + "!");
    }
    
    collection.deleteOne(filter);
    collect.putGroupUsers(groupUser.getId(), groupUser);
    return groupUser;
  }
}
