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

import io.resys.hdes.pm.repo.api.ImmutableConstraintViolation;
import io.resys.hdes.pm.repo.api.ImmutableGroupUser;
import io.resys.hdes.pm.repo.api.ImmutableRevisionConflict;
import io.resys.hdes.pm.repo.api.PmException;
import io.resys.hdes.pm.repo.api.PmException.ConstraintType;
import io.resys.hdes.pm.repo.api.PmException.ErrorType;
import io.resys.hdes.pm.repo.api.PmRepository.GroupUser;
import io.resys.hdes.pm.repo.api.PmRevException;
import io.resys.hdes.pm.repo.api.commands.GroupCommands;
import io.resys.hdes.pm.repo.api.commands.GroupUserCommands;
import io.resys.hdes.pm.repo.api.commands.UserCommands;
import io.resys.hdes.pm.repo.spi.mongodb.PersistentCommand;
import io.resys.hdes.pm.repo.spi.mongodb.PersistentCommand.MongoDbConfig;
import io.resys.hdes.pm.repo.spi.mongodb.codecs.GroupUserCodec;
import io.resys.hdes.pm.repo.spi.support.RepoAssert;

public class MongoGroupUserCommands implements GroupUserCommands {

  private final PersistentCommand persistentCommand;
  private final GroupCommands groupCommands;
  private final UserCommands userCommands;

  public MongoGroupUserCommands(PersistentCommand persistentCommand, GroupCommands groupCommands, UserCommands userCommands) {
    super();
    this.persistentCommand = persistentCommand;
    this.groupCommands = groupCommands;
    this.userCommands = userCommands;
  }

  @Override
  public GroupUserCreateBuilder create() {
    
    return new GroupUserCreateBuilder() {
      
      private String userId;
      private String groupId;
      
      @Override
      public GroupUserCreateBuilder userId(String userId) {
        this.userId = userId;
        return this;
      }
      @Override
      public GroupUserCreateBuilder groupId(String groupId) {
        this.groupId = groupId;
        return this;
      }
      @Override
      public GroupUser build() throws PmException {
        RepoAssert.notEmpty(userId, () -> "userId not defined!");
        RepoAssert.notEmpty(groupId, () -> "groupId not defined!");
        
        Optional<GroupUser> conflict = query().groupId(groupId).userId(userId).find().stream().findFirst();
        if(conflict.isPresent()) {
          throw new PmException(ImmutableConstraintViolation.builder()
              .id(conflict.get().getId())
              .rev(conflict.get().getRev())
              .constraint(ConstraintType.NOT_UNIQUE)
              .type(ErrorType.GROUP_USER)
              .build(), "entity: 'group user' with group id: '" + groupId + "' and userId id: '" + userId + "' already exists!");
        }
        
        RepoAssert.notNull(userCommands.query().id(userId), () -> "user not found");
        RepoAssert.notNull(groupCommands.query().id(groupId), () -> "group not found");
        
        GroupUser groupUser = ImmutableGroupUser.builder()
            .id(UUID.randomUUID().toString())
            .rev(UUID.randomUUID().toString())
            .groupId(groupId)
            .userId(userId)
            .created(LocalDateTime.now())
            .build();
        return persistentCommand
            .create(visitor -> visitor.visitGroupUser(groupUser)).getGroupUsers()
            .get(groupUser.getId());
      }
    };
  }
  
  @Override
  public GroupUserQueryBuilder query() {
    return new GroupUserQueryBuilder() {
      private String groupId;
      private String userId;
      @Override
      public GroupUserQueryBuilder groupId(String groupId) {
        this.groupId = groupId;
        return this;
      }
      @Override
      public GroupUserQueryBuilder userId(String userId) {
        this.userId = userId;
        return this;
      }
      @Override
      public List<GroupUser> find() {
        BiFunction<MongoClient, MongoDbConfig, List<GroupUser>> mapper = (client, config) -> {
          List<GroupUser> result = new ArrayList<>();
          List<Bson> filters = new ArrayList<>();
          
          if(userId != null) {
            filters.add(Filters.eq(GroupUserCodec.USER_ID, userId));
          }
          if(groupId != null) {
            filters.add(Filters.eq(GroupUserCodec.GROUP_ID, groupId));
          }
          
          MongoCollection<GroupUser> collection = client
            .getDatabase(config.getDb())
            .getCollection(config.getGroupUsers(), GroupUser.class);
          
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
      public GroupUser id(String id) throws PmException {
        RepoAssert.notNull(id, () -> "id not defined!");
        
        BiFunction<MongoClient, MongoDbConfig, Optional<GroupUser>> mapper = (client, config) -> {
          GroupUser value = client
              .getDatabase(config.getDb())
              .getCollection(config.getGroupUsers(), GroupUser.class)
              .find(Filters.eq(GroupUserCodec.ID, id))
              .first();
          return Optional.ofNullable(value);
        };
        Optional<GroupUser> result = persistentCommand.map(mapper);
        if(result.isEmpty()) {
          throw new PmException(ImmutableConstraintViolation.builder()
              .id(id)
              .rev("any")
              .constraint(ConstraintType.NOT_FOUND)
              .type(ErrorType.GROUP_USER)
              .build(), "entity: 'group user' not found with id: '" + id + "'!"); 
        }
        return result.get();
      }
      
      @Override
      public GroupUser rev(String id, String rev) throws PmException {
        RepoAssert.notNull(id, () -> "id not defined!");
        RepoAssert.notNull(rev, () -> "rev not defined!");
        GroupUser group = id(id);
        
        if(!rev.equals(group.getRev())) {
          throw new PmRevException(ImmutableRevisionConflict.builder()
              .id(group.getId())
              .revToUpdate(rev)
              .rev(group.getRev())
              .build(), "revision conflict: 'access' with id: '" + group.getId() + "', revs: " + group.getRev() + " != " + rev + "!");
        }
        return group;
      }

      
      @Override
      public Optional<GroupUser> find(String id) {
        BiFunction<MongoClient, MongoDbConfig, Optional<GroupUser>> mapper = (client, config) -> {
          GroupUser value = client
              .getDatabase(config.getDb())
              .getCollection(config.getGroupUsers(), GroupUser.class)
              .find(Filters.eq(GroupUserCodec.ID, id))
              .first();
          return Optional.ofNullable(value);
        };
        return persistentCommand.map(mapper);
      }
    };
  }

  @Override
  public GroupUserDeleteBuilder delete() {
    return new GroupUserDeleteBuilder() {
      
      private String id;
      private String rev;
      private String groupId;
      private String userId;
      
      @Override
      public GroupUserDeleteBuilder rev(String id, String rev) {
        this.id = id;
        return this;
      }
      @Override
      public GroupUserDeleteBuilder groupId(String groupId) {
        this.groupId = groupId;
        return this;
      }
      @Override
      public GroupUserDeleteBuilder userId(String userId) {
        this.userId = userId;
        return this;
      }
      @Override
      public List<GroupUser> build() throws PmException {
        
        if(id != null && rev != null) {
          RepoAssert.notNull(id, () -> "id not defined!");
          RepoAssert.notNull(rev, () -> "rev not defined!");
          GroupUser group = query().rev(id, rev);
          return new ArrayList<>(
              persistentCommand
              .delete(visitor -> visitor.visitGroupUser(group))
              .getGroupUsers().values());
        }
        RepoAssert.isTrue(groupId != null || userId != null, () -> "userId, groupId or (id and rev) not defined!");
        
        final List<GroupUser> access = query().groupId(groupId).userId(userId).find();
        
        return new ArrayList<>(
            persistentCommand
            .delete(visitor -> access.forEach(a -> visitor.visitGroupUser(a)))
            .getGroupUsers().values());
      }
    };
  }
}
