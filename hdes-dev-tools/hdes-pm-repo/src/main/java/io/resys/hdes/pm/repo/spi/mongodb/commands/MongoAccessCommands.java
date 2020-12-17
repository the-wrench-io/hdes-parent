package io.resys.hdes.pm.repo.spi.mongodb.commands;

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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;

import org.bson.conversions.Bson;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import io.resys.hdes.pm.repo.api.ImmutableAccess;
import io.resys.hdes.pm.repo.api.ImmutableConstraintViolation;
import io.resys.hdes.pm.repo.api.ImmutableRevisionConflict;
import io.resys.hdes.pm.repo.api.PmException;
import io.resys.hdes.pm.repo.api.PmException.ConstraintType;
import io.resys.hdes.pm.repo.api.PmException.ErrorType;
import io.resys.hdes.pm.repo.api.PmRepository.Access;
import io.resys.hdes.pm.repo.api.PmRevException;
import io.resys.hdes.pm.repo.api.PmRevException.RevisionType;
import io.resys.hdes.pm.repo.api.commands.AccessCommands;
import io.resys.hdes.pm.repo.api.commands.GroupCommands;
import io.resys.hdes.pm.repo.api.commands.ProjectCommands;
import io.resys.hdes.pm.repo.api.commands.UserCommands;
import io.resys.hdes.pm.repo.spi.mongodb.PersistentCommand;
import io.resys.hdes.pm.repo.spi.mongodb.PersistentCommand.MongoDbConfig;
import io.resys.hdes.pm.repo.spi.mongodb.codecs.AccessCodec;
import io.resys.hdes.pm.repo.spi.support.RepoAssert;

public class MongoAccessCommands implements AccessCommands {

  private final PersistentCommand persistentCommand;
  private final ProjectCommands projectCommands;
  private final UserCommands userCommands;
  private final GroupCommands groupCommands;

  public MongoAccessCommands(PersistentCommand persistentCommand, ProjectCommands projectCommands, UserCommands userCommands, GroupCommands groupCommands) {
    super();
    this.persistentCommand = persistentCommand;
    this.projectCommands = projectCommands;
    this.userCommands = userCommands;
    this.groupCommands = groupCommands;
  }

  @Override
  public AccessCreateBuilder create() {
    
    return new AccessCreateBuilder() {
      private String name;
      private String projectId;
      private String userId;
      private String groupId;
      
      @Override
      public AccessCreateBuilder name(String name) {
        this.name = name;
        return this;
      }
      @Override
      public AccessCreateBuilder projectId(String projectId) {
        this.projectId = projectId;
        return this;
      }
      @Override
      public AccessCreateBuilder userId(String userId) {
        this.userId = userId;
        return this;
      }
      @Override
      public AccessCreateBuilder groupId(String groupId) {
        this.groupId = groupId;
        return this;
      }
      @Override
      public Access build() throws PmException {
        RepoAssert.notEmpty(name, () -> "name not defined!");
        RepoAssert.isTrue(userId == null || groupId == null, () -> "both userId and groupId can't be used simultaneously!");
        
        Optional<Access> conflict = query().findByName(name);
        if(conflict.isPresent()) {
          throw new PmException(ImmutableConstraintViolation.builder()
              .id(conflict.get().getId())
              .rev(conflict.get().getRev())
              .constraint(ConstraintType.NOT_UNIQUE)
              .type(ErrorType.ACCESS)
              .build(), "entity: 'access' with name: '" + name + "' already exists!");
        }
        
        if(userId != null) {
          RepoAssert.notNull(userCommands.query().id(userId), () -> "user not found");
        }
        if(groupId != null) {
          RepoAssert.notNull(groupCommands.query().id(groupId), () -> "group not found");
        }
        
        RepoAssert.notNull(projectCommands.query().id(projectId), () -> "user not found");
        
        Access project = ImmutableAccess.builder()
            .id(UUID.randomUUID().toString())
            .rev(UUID.randomUUID().toString())
            .name(name)
            .projectId(projectId)
            .userId(Optional.ofNullable(userId))
            .groupId(Optional.ofNullable(groupId))
            .created(LocalDateTime.now())
            .build();
        return persistentCommand
            .create(visitor -> visitor.visitAccess(project)).getAccess()
            .get(project.getId());
      }
    };
  }
  
  @Override
  public AccessQueryBuilder query() {
    return new AccessQueryBuilder() {
      private String projectId;
      private String userId;
      private String groupId;
      @Override
      public AccessQueryBuilder projectId(String projectId) {
        this.projectId = projectId;
        return this;
      }
      @Override
      public AccessQueryBuilder userId(String userId) {
        this.userId = userId;
        return this;
      }
      @Override
      public AccessQueryBuilder groupId(String groupId) {
        this.groupId = groupId;
        return this;
      }
      @Override
      public List<Access> find() {
        BiFunction<MongoClient, MongoDbConfig, List<Access>> mapper = (client, config) -> {
          List<Access> result = new ArrayList<>();
          List<Bson> filters = new ArrayList<>();
          
          if(userId != null) {
            filters.add(Filters.eq(AccessCodec.USER_ID, userId));
          }
          if(projectId != null) {
            filters.add(Filters.eq(AccessCodec.PROJECT_ID, projectId));
          }
          if(groupId != null) {
            filters.add(Filters.eq(AccessCodec.GROUP_ID, groupId));
          }
          
          MongoCollection<Access> collection = client
            .getDatabase(config.getDb())
            .getCollection(config.getAccess(), Access.class);
          
          if(filters.isEmpty()) {
            collection.find().forEach(result::add);
          } else {
            collection
              .find(Filters.and(filters))
              .forEach(result::add); 
          }
          return Collections.unmodifiableList(result);
          
        };
        return persistentCommand.map(mapper);
      }
      
      @Override
      public Access id(String id) throws PmException {
        RepoAssert.notNull(id, () -> "id not defined!");
        
        BiFunction<MongoClient, MongoDbConfig, Optional<Access>> mapper = (client, config) -> {
          Access value = client
              .getDatabase(config.getDb())
              .getCollection(config.getAccess(), Access.class)
              .find(Filters.eq(AccessCodec.ID, id))
              .first();
          return Optional.ofNullable(value);
        };
        Optional<Access> result = persistentCommand.map(mapper);
        if(result.isEmpty()) {
          throw new PmException(ImmutableConstraintViolation.builder()
              .id(id)
              .rev("any")
              .constraint(ConstraintType.NOT_FOUND)
              .type(ErrorType.ACCESS)
              .build(), "entity: 'access' not found with id: '" + id + "'!"); 
        }
        return result.get();
      }
      
      @Override
      public Access rev(String id, String rev) throws PmException {
        RepoAssert.notNull(id, () -> "id not defined!");
        RepoAssert.notNull(rev, () -> "rev not defined!");
        Access project = id(id);
        
        if(!rev.equals(project.getRev())) {
          throw new PmRevException(ImmutableRevisionConflict.builder()
              .id(project.getId())
              .revToUpdate(rev)
              .rev(project.getRev())
              .type(RevisionType.ACCESS)
              .build(), "revision conflict: 'access' with id: '" + project.getId() + "', revs: " + project.getRev() + " != " + rev + "!");
        }
        return project;
      }
      
      @Override
      public Optional<Access> findByName(String name) {
        RepoAssert.notNull(name, () -> "name not defined!");
        BiFunction<MongoClient, MongoDbConfig, Optional<Access>> mapper = (client, config) -> {
          Access value = client
              .getDatabase(config.getDb())
              .getCollection(config.getAccess(), Access.class)
              .find(Filters.eq(AccessCodec.NAME, name))
              .first();
          return Optional.ofNullable(value);
        };
        return persistentCommand.map(mapper);
      }
      
      @Override
      public Optional<Access> find(String id) {
        BiFunction<MongoClient, MongoDbConfig, Optional<Access>> mapper = (client, config) -> {
          Access value = client
              .getDatabase(config.getDb())
              .getCollection(config.getAccess(), Access.class)
              .find(Filters.eq(AccessCodec.ID, id))
              .first();
          return Optional.ofNullable(value);
        };
        return persistentCommand.map(mapper);
      }
    };
  }

  @Override
  public AccessUpdateBuilder update() {
    return new AccessUpdateBuilder() {
      private String id;
      private String rev;
      private String name;
      
      @Override
      public AccessUpdateBuilder rev(String rev) {
        this.rev = rev;
        return this;
      }
      @Override
      public AccessUpdateBuilder name(String name) {
        this.name = name;
        return this;
      }
      @Override
      public AccessUpdateBuilder id(String id) {
        this.id = id;
        return this;
      }
      @Override
      public Access build() throws PmException {
        RepoAssert.notNull(id, () -> "id not defined!");
        RepoAssert.notNull(rev, () -> "rev not defined!");
        RepoAssert.notNull(name, () -> "name not defined!");
        
        Access access = ImmutableAccess.builder()
            .from(query().rev(id, rev))
            .name(name)
            .build();
        return persistentCommand
            .update(visitor -> visitor.visitAccess(access))
            .getAccess().get(access.getId());
      }
    };
  }

  @Override
  public AccessDeleteBuilder delete() {
    return new AccessDeleteBuilder() {
      
      private String id;
      private String rev;
      private String projectId;
      private String userId;
      
      @Override
      public AccessDeleteBuilder rev(String id, String rev) {
        this.id = id;
        return this;
      }
      @Override
      public AccessDeleteBuilder projectId(String projectId) {
        this.projectId = projectId;
        return this;
      }
      @Override
      public AccessDeleteBuilder userId(String userId) {
        this.userId = userId;
        return this;
      }
      @Override
      public List<Access> build() throws PmException {
        
        if(id != null && rev != null) {
          RepoAssert.notNull(id, () -> "id not defined!");
          RepoAssert.notNull(rev, () -> "rev not defined!");
          Access project = query().rev(id, rev);
          return new ArrayList<>(
              persistentCommand
              .delete(visitor -> visitor.visitAccess(project))
              .getAccess().values());
        }
        RepoAssert.isTrue(projectId != null || userId != null, () -> "userId, projectId or (id and rev) not defined!");
        
        final List<Access> access = query().projectId(projectId).userId(userId).find();
        
        return new ArrayList<>(
            persistentCommand
            .delete(visitor -> access.forEach(a -> visitor.visitAccess(a)))
            .getAccess().values());
      }
    };
  }
}
