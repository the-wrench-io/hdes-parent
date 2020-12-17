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

import com.mongodb.client.MongoClient;
import com.mongodb.client.model.Filters;

import io.resys.hdes.pm.repo.api.ImmutableConstraintViolation;
import io.resys.hdes.pm.repo.api.ImmutableRevisionConflict;
import io.resys.hdes.pm.repo.api.ImmutableUser;
import io.resys.hdes.pm.repo.api.PmException;
import io.resys.hdes.pm.repo.api.PmException.ConstraintType;
import io.resys.hdes.pm.repo.api.PmException.ErrorType;
import io.resys.hdes.pm.repo.api.PmRepository.User;
import io.resys.hdes.pm.repo.api.PmRevException;
import io.resys.hdes.pm.repo.api.commands.UserCommands;
import io.resys.hdes.pm.repo.spi.mongodb.PersistentCommand;
import io.resys.hdes.pm.repo.spi.mongodb.PersistentCommand.MongoDbConfig;
import io.resys.hdes.pm.repo.spi.mongodb.codecs.UserCodec;
import io.resys.hdes.pm.repo.spi.support.RepoAssert;

public class MongoUserCommands implements UserCommands {

  private final PersistentCommand persistentCommand;

  public MongoUserCommands(PersistentCommand persistentCommand) {
    super();
    this.persistentCommand = persistentCommand;
  }

  @Override
  public UserCreateBuilder create() {
    return new UserCreateBuilder() {
      private String value;
      private String externalId;
      @Override
      public UserCreateBuilder value(String value) {
        this.value = value;
        return this;
      }
      @Override
      public UserCreateBuilder externalId(String externalId) {
        this.externalId = externalId;
        return this;
      }
      @Override
      public User build() throws PmException {
        RepoAssert.notEmpty(value, () -> "name not defined!");
        
        Optional<User> conflict = query().findByValue(value);
        if(conflict.isPresent()) {
          throw new PmException(ImmutableConstraintViolation.builder()
              .id(conflict.get().getId())
              .rev(conflict.get().getRev())
              .constraint(ConstraintType.NOT_UNIQUE)
              .type(ErrorType.USER)
              .build(), "entity: 'user' with name: '" + value + "' already exists!");
        }
        
        User project = ImmutableUser.builder()
            .id(UUID.randomUUID().toString())
            .rev(UUID.randomUUID().toString())
            .token(UUID.randomUUID().toString())
            .externalId(Optional.ofNullable(externalId))
            .name(value)
            .created(LocalDateTime.now())
            .build();
        return persistentCommand
            .create(visitor -> visitor.visitUser(project)).getUser()
            .get(project.getId());
      }
    };
  }
  
  @Override
  public UserQueryBuilder query() {
    return new UserQueryBuilder() {
      @Override
      public List<User> find() {
        BiFunction<MongoClient, MongoDbConfig, List<User>> mapper = (client, config) -> {
          List<User> result = new ArrayList<>();
          client
            .getDatabase(config.getDb())
            .getCollection(config.getUsers(), User.class)
            .find()
            .forEach(result::add);
          
          return Collections.unmodifiableList(result);
        };
        return persistentCommand.map(mapper);
      }
      
      @Override
      public User id(String id) throws PmException {
        RepoAssert.notNull(id, () -> "id not defined!");
        
        BiFunction<MongoClient, MongoDbConfig, Optional<User>> mapper = (client, config) -> {
          User value = client
              .getDatabase(config.getDb())
              .getCollection(config.getUsers(), User.class)
              .find(Filters.eq(UserCodec.ID, id))
              .first();
          return Optional.ofNullable(value);
        };
        Optional<User> result = persistentCommand.map(mapper);
        if(result.isEmpty()) {
          throw new PmException(ImmutableConstraintViolation.builder()
              .id(id)
              .rev("any")
              .constraint(ConstraintType.NOT_FOUND)
              .type(ErrorType.PROJECT)
              .build(), "entity: 'user' not found with id: '" + id + "'!"); 
        }
        return result.get();
      }
      
      @Override
      public User rev(String id, String rev) throws PmException {
        RepoAssert.notNull(id, () -> "id not defined!");
        RepoAssert.notNull(rev, () -> "rev not defined!");
        User project = id(id);
        
        if(!rev.equals(project.getRev())) {
          throw new PmRevException(ImmutableRevisionConflict.builder()
              .id(project.getId())
              .revToUpdate(rev)
              .rev(project.getRev())
              .build(), "revision conflict: 'user' with id: '" + project.getId() + "', revs: " + project.getRev() + " != " + rev + "!");
        }
        return project;
      }
      
      @Override
      public Optional<User> findByValue(String value) {
        RepoAssert.notNull(value, () -> "value not defined!");
        BiFunction<MongoClient, MongoDbConfig, Optional<User>> mapper = (client, config) -> {
          User user = client
              .getDatabase(config.getDb())
              .getCollection(config.getUsers(), User.class)
              .find(Filters.eq(UserCodec.NAME, value))
              .first();
          return Optional.ofNullable(user);
        };
        return persistentCommand.map(mapper);
      }
      @Override
      public Optional<User> findByToken(String token) {
        RepoAssert.notNull(token, () -> "token not defined!");
        BiFunction<MongoClient, MongoDbConfig, Optional<User>> mapper = (client, config) -> {
          User user = client
              .getDatabase(config.getDb())
              .getCollection(config.getUsers(), User.class)
              .find(Filters.eq(UserCodec.TOKEN, token))
              .first();
          return Optional.ofNullable(user);
        };
        return persistentCommand.map(mapper);
      }      
      @Override
      public Optional<User> findByExternalId(String externalId) {
        RepoAssert.notNull(externalId, () -> "externalId not defined!");
        
        BiFunction<MongoClient, MongoDbConfig, Optional<User>> mapper = (client, config) -> {
          User user = client
              .getDatabase(config.getDb())
              .getCollection(config.getUsers(), User.class)
              .find(Filters.eq(UserCodec.EXTERNAL_ID, externalId))
              .first();
          return Optional.ofNullable(user);
        };
        return persistentCommand.map(mapper);
      }
      @Override
      public Optional<User> find(String id) {
        BiFunction<MongoClient, MongoDbConfig, Optional<User>> mapper = (client, config) -> {
          User value = client
              .getDatabase(config.getDb())
              .getCollection(config.getUsers(), User.class)
              .find(Filters.eq(UserCodec.ID, id))
              .first();
          return Optional.ofNullable(value);
        };
        return persistentCommand.map(mapper);
      }

      @Override
      public User any(String userFilter) throws PmException {
        return find(userFilter)
          .orElseGet(() -> findByValue(userFilter)
              .orElseGet(() -> findByExternalId(userFilter)
                  .orElseGet(() -> findByToken(userFilter)
                    .orElseThrow(() -> new PmException(ImmutableConstraintViolation.builder()
                          .id(userFilter)
                          .rev("")
                          .constraint(ConstraintType.NOT_FOUND)
                          .type(ErrorType.USER)
                          .build(), "entity: 'user' not found by one of the keys: 'value/externalId/id/token' = '" + userFilter + "'!")
                    )
                  )
                )
              );
      }
    };
  }

  @Override
  public UserUpdateBuilder update() {
    return new UserUpdateBuilder() {
      private String id;
      private String rev;
      private String value;
      private String externalId;
      private String token;
      
      @Override
      public UserUpdateBuilder rev(String id, String rev) {
        this.rev = rev;
        return this;
      }
      @Override
      public UserUpdateBuilder value(String value) {
        this.value = value;
        return this;
      }
      @Override
      public UserUpdateBuilder externalId(String externalId) {
        this.externalId = externalId;
        return this;
      }
      @Override
      public UserUpdateBuilder token(String token) {
        this.token = token;
        return this;
      }
      @Override
      public User build() throws PmException {
        RepoAssert.notNull(id, () -> "id not defined!");
        RepoAssert.notNull(rev, () -> "rev not defined!");
        
        final User old = query().rev(id, rev);
        final String value = this.value == null ? old.getName() : this.value;
        final String externalId = this.externalId == null ? old.getExternalId().orElse(null) : this.externalId;
        final String token = this.token == null ? old.getToken() : this.token;
        
        User access = ImmutableUser.builder()
            .from(old)
            .name(value)
            .externalId(externalId)
            .token(token)
            .build();
        return persistentCommand
            .update(visitor -> visitor.visitUser(access))
            .getUser().get(access.getId());
      }
    };
  }

  @Override
  public UserDeleteBuilder delete() {
    return new UserDeleteBuilder() {
      private String id;
      private String rev;
      @Override
      public UserDeleteBuilder rev(String id, String rev) {
        this.id = id;
        return this;
      }
      @Override
      public User build() throws PmException {
        RepoAssert.notNull(id, () -> "id not defined!");
        RepoAssert.notNull(rev, () -> "rev not defined!");
        User user = query().rev(id, rev);
        
        return persistentCommand
            .delete(visitor -> visitor.visitUser(user))
            .getUser().get(user.getId());
      }
    };
  }
}
