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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import io.resys.hdes.projects.api.ImmutableConstraintViolation;
import io.resys.hdes.projects.api.ImmutableGroup;
import io.resys.hdes.projects.api.ImmutableRevisionConflict;
import io.resys.hdes.projects.api.PmException;
import io.resys.hdes.projects.api.PmException.ConstraintType;
import io.resys.hdes.projects.api.PmException.ErrorType;
import io.resys.hdes.projects.api.PmRepository.Group;
import io.resys.hdes.projects.api.PmRevException;
import io.resys.hdes.projects.api.commands.GroupCommands;
import io.resys.hdes.projects.spi.mongodb.PersistentCommand;
import io.resys.hdes.projects.spi.mongodb.PersistentCommand.MongoDbConfig;
import io.resys.hdes.projects.spi.mongodb.codecs.GroupCodec;
import io.resys.hdes.projects.spi.support.RepoAssert;

public class MongoGroupCommands implements GroupCommands {

  private final PersistentCommand persistentCommand;

  public MongoGroupCommands(PersistentCommand persistentCommand) {
    super();
    this.persistentCommand = persistentCommand;
  }

  @Override
  public GroupCreateBuilder create() {
    return new GroupCreateBuilder() {
      private String name;
      @Override
      public GroupCreateBuilder name(String name) {
        this.name = name;
        return this;
      }
      @Override
      public Group build() throws PmException {
        RepoAssert.notEmpty(name, () -> "name not defined!");
        
        Optional<Group> conflict = query().findByName(name);
        if(conflict.isPresent()) {
          throw new PmException(ImmutableConstraintViolation.builder()
              .id(conflict.get().getId())
              .rev(conflict.get().getRev())
              .constraint(ConstraintType.NOT_UNIQUE)
              .type(ErrorType.GROUP)
              .build(), "entity: 'group' with name: '" + name + "' already exists!");
        }

        Group group = ImmutableGroup.builder()
            .id(UUID.randomUUID().toString())
            .rev(UUID.randomUUID().toString())
            .name(name)
            .created(LocalDateTime.now())
            .build();
        return persistentCommand
            .create(visitor -> visitor.visitGroup(group)).getGroups()
            .get(group.getId());
      }
    };
  }
  
  @Override
  public GroupQueryBuilder query() {
    return new GroupQueryBuilder() {
      @Override
      public List<Group> find() {
        BiFunction<MongoClient, MongoDbConfig, List<Group>> mapper = (client, config) -> {
          List<Group> result = new ArrayList<>();

          MongoCollection<Group> collection = client
            .getDatabase(config.getDb())
            .getCollection(config.getGroups(), Group.class);
          
          collection.find()
            .forEach((Consumer<Group>) result::add);
          return Collections.unmodifiableList(result);
          
        };
        return persistentCommand.map(mapper);
      }
      
      @Override
      public Group id(String id) throws PmException {
        RepoAssert.notNull(id, () -> "id not defined!");
        
        BiFunction<MongoClient, MongoDbConfig, Optional<Group>> mapper = (client, config) -> {
          Group value = client
              .getDatabase(config.getDb())
              .getCollection(config.getGroups(), Group.class)
              .find(Filters.eq(GroupCodec.ID, id))
              .first();
          return Optional.ofNullable(value);
        };
        Optional<Group> result = persistentCommand.map(mapper);
        if(result.isEmpty()) {
          throw new PmException(ImmutableConstraintViolation.builder()
              .id(id)
              .rev("any")
              .constraint(ConstraintType.NOT_FOUND)
              .type(ErrorType.GROUP)
              .build(), "entity: 'group' not found with id: '" + id + "'!");
        }
        return result.get();
      }
      
      @Override
      public Group rev(String id, String rev) throws PmException {
        RepoAssert.notNull(id, () -> "id not defined!");
        RepoAssert.notNull(rev, () -> "rev not defined!");
        Group group = id(id);
        
        if(!rev.equals(group.getRev())) {
          throw new PmRevException(ImmutableRevisionConflict.builder()
              .id(group.getId())
              .revToUpdate(rev)
              .rev(group.getRev())
              .build(), "revision conflict: 'group' with id: '" + group.getId() + "', revs: " + group.getRev() + " != " + rev + "!");
        }
        return group;
      }
      
      @Override
      public Optional<Group> findByName(String name) {
        RepoAssert.notNull(name, () -> "name not defined!");
        BiFunction<MongoClient, MongoDbConfig, Optional<Group>> mapper = (client, config) -> {
          Group value = client
              .getDatabase(config.getDb())
              .getCollection(config.getGroups(), Group.class)
              .find(Filters.eq(GroupCodec.NAME, name))
              .first();
          return Optional.ofNullable(value);
        };
        return persistentCommand.map(mapper);
      }
      
      @Override
      public Optional<Group> find(String id) {
        BiFunction<MongoClient, MongoDbConfig, Optional<Group>> mapper = (client, config) -> {
          Group value = client
              .getDatabase(config.getDb())
              .getCollection(config.getGroups(), Group.class)
              .find(Filters.eq(GroupCodec.ID, id))
              .first();
          return Optional.ofNullable(value);
        };
        return persistentCommand.map(mapper);
      }

      @Override
      public Group any(String idOrName) {
        return find(idOrName).orElseGet(() -> findByName(idOrName)
            .orElseThrow(() -> new PmException(ImmutableConstraintViolation.builder()
                  .id(idOrName)
                  .rev("")
                  .constraint(ConstraintType.NOT_FOUND)
                  .type(ErrorType.GROUP)
                  .build(), "entity: 'group' not found by one of the keys: 'id/name' = '" + idOrName + "'!")
            ));
      }
    };
  }

  @Override
  public GroupUpdateBuilder update() {
    return new GroupUpdateBuilder() {
      private String id;
      private String rev;
      private String name;
      
      @Override
      public GroupUpdateBuilder rev(String rev) {
        this.rev = rev;
        return this;
      }
      @Override
      public GroupUpdateBuilder name(String name) {
        this.name = name;
        return this;
      }
      @Override
      public GroupUpdateBuilder id(String id) {
        this.id = id;
        return this;
      }
      @Override
      public Group build() throws PmException {
        RepoAssert.notNull(id, () -> "id not defined!");
        RepoAssert.notNull(rev, () -> "rev not defined!");
        RepoAssert.notNull(name, () -> "name not defined!");
        
        Group group = ImmutableGroup.builder()
            .from(query().rev(id, rev))
            .name(name)
            .build();
        return persistentCommand
            .update(visitor -> visitor.visitGroup(group))
            .getGroups().get(group.getId());
      }
    };
  }

  @Override
  public GroupDeleteBuilder delete() {
    return new GroupDeleteBuilder() {
      private String id;
      private String rev;
      @Override
      public GroupDeleteBuilder id(String id) {
        this.id = id;
        return this;
      }
      @Override
      public GroupDeleteBuilder rev(String rev) {
        this.rev = rev;
        return this;
      }
      @Override
      public Group build() throws PmException {
        RepoAssert.notNull(id, () -> "id not defined!");
        RepoAssert.notNull(rev, () -> "rev not defined!");
        
        Group group = query().rev(id, rev);
        return persistentCommand
            .delete(visitor -> visitor.visitGroup(group))
            .getGroups().get(group.getId());
      }
    };
  }
}
