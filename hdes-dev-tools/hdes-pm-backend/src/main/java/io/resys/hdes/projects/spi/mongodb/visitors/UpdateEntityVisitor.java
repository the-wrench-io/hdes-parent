package io.resys.hdes.projects.spi.mongodb.visitors;

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

import java.util.UUID;

import org.bson.conversions.Bson;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import io.resys.hdes.projects.api.ImmutableAccess;
import io.resys.hdes.projects.api.ImmutableConstraintViolation;
import io.resys.hdes.projects.api.ImmutableGroup;
import io.resys.hdes.projects.api.ImmutableProject;
import io.resys.hdes.projects.api.ImmutableRevisionConflict;
import io.resys.hdes.projects.api.ImmutableUser;
import io.resys.hdes.projects.api.PmException;
import io.resys.hdes.projects.api.PmException.ConstraintType;
import io.resys.hdes.projects.api.PmException.ErrorType;
import io.resys.hdes.projects.api.PmRepository.Access;
import io.resys.hdes.projects.api.PmRepository.Group;
import io.resys.hdes.projects.api.PmRepository.GroupUser;
import io.resys.hdes.projects.api.PmRepository.Project;
import io.resys.hdes.projects.api.PmRepository.User;
import io.resys.hdes.projects.api.PmRevException;
import io.resys.hdes.projects.spi.mongodb.ImmutablePersistedEntities;
import io.resys.hdes.projects.spi.mongodb.PersistentCommand.EntityVisitor;
import io.resys.hdes.projects.spi.mongodb.PersistentCommand.MongoDbConfig;
import io.resys.hdes.projects.spi.mongodb.codecs.AccessCodec;
import io.resys.hdes.projects.spi.mongodb.codecs.GroupCodec;
import io.resys.hdes.projects.spi.mongodb.codecs.ProjectCodec;
import io.resys.hdes.projects.spi.mongodb.codecs.UserCodec;
import io.resys.hdes.projects.spi.support.RepoAssert;

public class UpdateEntityVisitor implements EntityVisitor {

  private final MongoClient client;
  private final MongoDbConfig config;
  private final ImmutablePersistedEntities.Builder collect;
  
  
  public UpdateEntityVisitor(MongoClient client, MongoDbConfig config, ImmutablePersistedEntities.Builder collect) {
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
    public UpdateEntityVisitor build() {
      RepoAssert.notNull(collect, () -> "collect not defined!");
      RepoAssert.notNull(client, () -> "client not defined!");
      RepoAssert.notNull(config, () -> "config not defined!");
      
      return new UpdateEntityVisitor(client, config, collect);
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
    
    final var newRev = UUID.randomUUID().toString();
    collection.updateOne(filter, Updates.combine(
        Updates.set(ProjectCodec.NAME, project.getName()), 
        Updates.set(ProjectCodec.REV, newRev)));
    
    final var result = ImmutableProject.builder().from(project).rev(newRev).build();
    collect.putProject(result.getId(), result);
    return result;
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
    
    final var newRev = UUID.randomUUID().toString();
    collection.updateOne(filter, Updates.combine(
        Updates.set(AccessCodec.NAME, access.getName()), 
        Updates.set(AccessCodec.REV, newRev)));
    
    final var result = ImmutableAccess.builder().from(access).rev(newRev).build();
    collect.putAccess(result.getId(), result);
    return result;
  }
  
  @Override
  public User visitUser(User user) {
    final MongoCollection<User> collection = client
        .getDatabase(config.getDb())
        .getCollection(config.getUsers(), User.class);
    
    final Bson filter = Filters.eq(UserCodec.ID, user.getId());
    final User value = collection.find(filter).first();
    
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
    
    if(!value.getToken().equals(user.getToken())) {
      final User tokenUser = collection.find(Filters.eq(UserCodec.TOKEN, user.getToken())).first(); 
      if(tokenUser != null) {
        throw new PmException(ImmutableConstraintViolation.builder()
            .id(user.getId())
            .rev(user.getRev())
            .constraint(ConstraintType.NOT_UNIQUE)
            .type(ErrorType.USER)
            .build(), "entity: 'user' field: 'token' not unique with id: '" + user.getId() + "', token: '" + user.getToken() + "'!");
      }
    }
    
    if(user.getExternalId().isPresent() && !user.getExternalId().get().equals(value.getExternalId().orElse(null))) {
      final User externalIdUser = collection.find(Filters.eq(UserCodec.EXTERNAL_ID, user.getExternalId())).first(); 
      if(externalIdUser != null) {
        throw new PmException(ImmutableConstraintViolation.builder()
            .id(user.getId())
            .rev(user.getRev())
            .constraint(ConstraintType.NOT_UNIQUE)
            .type(ErrorType.USER)
            .build(), "entity: 'user' field: 'externalId' not unique with id: '" + user.getId() + "', externalId: '" + user.getExternalId().get() + "'!");
      }
    }
    
    
    final var newRev = UUID.randomUUID().toString();
    collection.updateOne(filter, Updates.combine(
        Updates.set(UserCodec.NAME, user.getName()), 
        Updates.set(UserCodec.TOKEN, user.getToken()), 
        Updates.set(UserCodec.REV, newRev)));
    
    final var result = ImmutableUser.builder().from(user).rev(newRev).build();
    collect.putUser(result.getId(), result);
    return result;
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
          .revToUpdate(group.getRev())
          .rev(value.getRev())
          .build(), "revision conflict: 'group' with id: '" + group.getId() + "', revs: " + group.getRev() + " != " + value.getRev() + "!");
    }
    
    final var newRev = UUID.randomUUID().toString();
    collection.updateOne(filter, Updates.combine(
        Updates.set(ProjectCodec.NAME, group.getName()), 
        Updates.set(ProjectCodec.REV, newRev)));
    
    final var result = ImmutableGroup.builder().from(group).rev(newRev).build();
    collect.putGroups(result.getId(), result);
    return result;
  }

  @Override
  public GroupUser visitGroupUser(GroupUser groupUser) {
    return groupUser;
  }
}
