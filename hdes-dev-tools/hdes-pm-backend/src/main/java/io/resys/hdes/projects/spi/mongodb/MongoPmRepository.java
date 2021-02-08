package io.resys.hdes.projects.spi.mongodb;

import java.util.Optional;
import java.util.function.Consumer;

import io.resys.hdes.projects.api.ImmutableBatchGroup;
import io.resys.hdes.projects.api.ImmutableBatchProject;
import io.resys.hdes.projects.api.ImmutableBatchUser;

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

import io.resys.hdes.projects.api.PmRepository;
import io.resys.hdes.projects.spi.mongodb.batch.BatchQueryDefault;
import io.resys.hdes.projects.spi.mongodb.batch.ResourceMapper;
import io.resys.hdes.projects.spi.mongodb.builders.MongoBuilderCreate;
import io.resys.hdes.projects.spi.mongodb.builders.MongoBuilderDelete;
import io.resys.hdes.projects.spi.mongodb.builders.MongoBuilderUpdate;
import io.resys.hdes.projects.spi.mongodb.queries.MongoQueryDefault;
import io.resys.hdes.projects.spi.mongodb.support.ImmutableMongoDbConfig;
import io.resys.hdes.projects.spi.mongodb.support.ImmutableMongoWrapper;
import io.resys.hdes.projects.spi.mongodb.support.MongoWrapper.MongoDbConfig;
import io.resys.hdes.projects.spi.mongodb.support.MongoWrapper.MongoTransaction;
import io.resys.hdes.projects.spi.support.RepoAssert;

public class MongoPmRepository implements PmRepository {
  
  private final MongoDbConfig config;
  private final MongoTransaction tx;
  
  public MongoPmRepository(MongoDbConfig config, MongoTransaction tx) {
    super();
    this.config = config;
    this.tx = tx;
  }
  
  @Override
  public BatchDelete delete() {
    return new BatchDelete() {
      @Override
      public User user(String userId, String rev) {
        return tx.accept(client -> {
          final var mongo = ImmutableMongoWrapper.builder().db(client.getDatabase(config.getDb())).config(config).client(client).build();
          final var builder = new MongoBuilderDelete(mongo);
          User deleted = builder.visitUser().visitRev(rev).visitId(userId).build(); 
          builder.build();
          return deleted;          
        });
      }      
      @Override
      public Project project(String projectId, String rev) {
        return tx.accept(client -> {
          final var mongo = ImmutableMongoWrapper.builder().db(client.getDatabase(config.getDb())).config(config).client(client).build();
          final var builder = new MongoBuilderDelete(mongo);
          Project deleted = builder.visitProject().visitRev(rev).visitId(projectId).build(); 
          builder.build();
          return deleted;
        });
      }
      @Override
      public Group group(String groupId, String rev) {
        return tx.accept(client -> {
          final var mongo = ImmutableMongoWrapper.builder().db(client.getDatabase(config.getDb())).config(config).client(client).build();
          final var builder = new MongoBuilderDelete(mongo);
          Group deleted = builder.visitGroup().visitRev(rev).visitId(groupId).build(); 
          builder.build();
          return deleted;
        });
      }
    };
  }
  
  
  @Override
  public BatchBuilder update() {
    return new BatchBuilder() {
      @Override
      public UserResource user(Consumer<ImmutableBatchUser.Builder> consumer) {
        ImmutableBatchUser.Builder builder = ImmutableBatchUser.builder();
        consumer.accept(builder);
        return user(builder.build());
      }
      @Override
      public ProjectResource project(Consumer<ImmutableBatchProject.Builder> consumer) {
        ImmutableBatchProject.Builder builder = ImmutableBatchProject.builder();
        consumer.accept(builder);
        return project(builder.build());
      }
      @Override
      public GroupResource group(Consumer<ImmutableBatchGroup.Builder> consumer) {
        ImmutableBatchGroup.Builder builder = ImmutableBatchGroup.builder();
        consumer.accept(builder);
        return group(builder.build());
      }
      @Override
      public UserResource user(BatchUser user) {
        return tx.accept(client -> {
          final var mongo = ImmutableMongoWrapper.builder().db(client.getDatabase(config.getDb())).config(config).client(client).build();
          final var query = new MongoQueryDefault(mongo);
          
          final var builder = new MongoBuilderUpdate(mongo);
          User updated = builder.visitUser()
              .visitId(user.getId())
              .visitRev(user.getRev())
              .visitName(user.getName())
              .visitExternalId(user.getExternalId() == null ? null : Optional.of(user.getExternalId()))
              .visitEmail(user.getEmail())
              .visitStatus(user.getStatus())
              .visitGroups(user.getGroups())
              .visitProjects(user.getProjects())
              .build();
          builder.build();
          return ResourceMapper.map(query, updated);
        });
      }
      @Override
      public ProjectResource project(BatchProject project) {
        return tx.accept(client -> {
          final var mongo = ImmutableMongoWrapper.builder().db(client.getDatabase(config.getDb())).config(config).client(client).build();
          final var query = new MongoQueryDefault(mongo);
          final var builder = new MongoBuilderUpdate(mongo);
          Project updated = builder.visitProject()
              .visitId(project.getId())
              .visitRev(project.getRev())
              .visitName(project.getName())
              .visitGroups(project.getGroups())
              .visitUsers(project.getUsers())
              .build();
          builder.build();
          return ResourceMapper.map(query, updated);
        });
      }
      @Override
      public GroupResource group(BatchGroup group) {
        return tx.accept(client -> {
          final var mongo = ImmutableMongoWrapper.builder().db(client.getDatabase(config.getDb())).config(config).client(client).build();
          final var query = new MongoQueryDefault(mongo);
          final var builder = new MongoBuilderUpdate(mongo);
          Group updated = builder.visitGroup()
              .visitId(group.getId())
              .visitRev(group.getRev())
              .visitName(group.getName())
              .visitProjects(group.getProjects())
              .visitUsers(group.getUsers())
              .visitMatcher(group.getMatcher())
              .build(); 
          builder.build();
          return ResourceMapper.map(query, updated);
        });
      }
    };
  }
    
  @Override
  public BatchBuilder create() {
    return new BatchBuilder() {
      @Override
      public UserResource user(Consumer<ImmutableBatchUser.Builder> consumer) {
        ImmutableBatchUser.Builder builder = ImmutableBatchUser.builder();
        consumer.accept(builder);
        return user(builder.build());
      }
      @Override
      public ProjectResource project(Consumer<ImmutableBatchProject.Builder> consumer) {
        ImmutableBatchProject.Builder builder = ImmutableBatchProject.builder();
        consumer.accept(builder);
        return project(builder.build());
      }
      @Override
      public GroupResource group(Consumer<ImmutableBatchGroup.Builder> consumer) {
        ImmutableBatchGroup.Builder builder = ImmutableBatchGroup.builder();
        consumer.accept(builder);
        return group(builder.build());
      }
      @Override
      public UserResource user(BatchUser user) {
        return tx.accept(client -> {
          final var mongo = ImmutableMongoWrapper.builder().db(client.getDatabase(config.getDb())).config(config).client(client).build();
          final var query = new MongoQueryDefault(mongo);
          final var builder = new MongoBuilderCreate(mongo);
          User created = builder.visitUser()
              .visitName(user.getName())
              .visitExternalId(user.getExternalId() == null ? null : Optional.of(user.getExternalId()))
              .visitEmail(user.getEmail())
              .visitGroups(user.getGroups())
              .visitProjects(user.getProjects())
              .build();
          builder.build();
          return ResourceMapper.map(query, created);
        });
      }
      @Override
      public ProjectResource project(BatchProject project) {
        return tx.accept(client -> {
          final var mongo = ImmutableMongoWrapper.builder().db(client.getDatabase(config.getDb())).config(config).client(client).build();
          final var query = new MongoQueryDefault(mongo);
          final var builder = new MongoBuilderCreate(mongo);
          Project created = builder.visitProject()
              .visitName(project.getName())
              .visitGroups(project.getGroups())
              .visitUsers(project.getUsers())
              .build();
          builder.build();
          return ResourceMapper.map(query, created);
        });
      }
      @Override
      public GroupResource group(BatchGroup group) {
        return tx.accept(client -> {      
          final var mongo = ImmutableMongoWrapper.builder().db(client.getDatabase(config.getDb())).config(config).client(client).build();
          final var query = new MongoQueryDefault(mongo);
          final var builder = new MongoBuilderCreate(mongo);
          Group created = builder.visitGroup()
              .visitName(group.getName())
              .visitProjects(group.getProjects())
              .visitUsers(group.getUsers())
              .visitMatcher(group.getMatcher())
              .build(); 
          builder.build();
          return ResourceMapper.map(query, created);
        });
      }
    };
  }
  
  @Override
  public BatchQuery query() {
    return tx.accept(client -> {       
      final var mongo = ImmutableMongoWrapper.builder().db(client.getDatabase(config.getDb())).config(config).client(client).build();
      MongoQueryDefault query = new MongoQueryDefault(mongo);        
      return new BatchQueryDefault(query);
    });
  } 
  
  public static Config config() {
    return new Config();
  } 
  
  public static class Config {
    private MongoTransaction transaction;
    private MongoDbConfig config;
    private String dbName;
    
    public Config transaction(MongoTransaction transaction) {
      this.transaction = transaction;
      return this;
    }

    public Config config(MongoDbConfig config) {
      this.config = config;
      return this;
    }
    public Config dbName(String dbName) {
      this.dbName = dbName;
      return this;
    }
    public MongoPmRepository build() {
      RepoAssert.notNull(transaction, () -> "transaction not defined!");
      RepoAssert.notEmpty(dbName, () -> "dbName not defined!");
      
      if (config == null) {
        config = ImmutableMongoDbConfig.builder()
            .db(dbName)
            .transactions("transactions")
            .projects("projects")
            .users("users")
            .access("access")
            .groups("groups")
            .groupUsers("groupUsers")
            .build();
      }
      return new MongoPmRepository(config, transaction);
    }
  }
}
