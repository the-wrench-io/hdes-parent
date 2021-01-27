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
import com.mongodb.client.model.Filters;

import io.resys.hdes.projects.api.ImmutableConstraintViolation;
import io.resys.hdes.projects.api.ImmutableProject;
import io.resys.hdes.projects.api.ImmutableRevisionConflict;
import io.resys.hdes.projects.api.PmException;
import io.resys.hdes.projects.api.PmException.ConstraintType;
import io.resys.hdes.projects.api.PmException.ErrorType;
import io.resys.hdes.projects.api.PmRepository.Project;
import io.resys.hdes.projects.api.PmRevException;
import io.resys.hdes.projects.api.commands.ProjectCommands;
import io.resys.hdes.projects.spi.mongodb.PersistentCommand;
import io.resys.hdes.projects.spi.mongodb.PersistentCommand.MongoDbConfig;
import io.resys.hdes.projects.spi.mongodb.codecs.ProjectCodec;
import io.resys.hdes.projects.spi.support.RepoAssert;

public class MongoProjectCommands implements ProjectCommands {

  private final PersistentCommand persistentCommand;

  public MongoProjectCommands(PersistentCommand persistentCommand) {
    super();
    this.persistentCommand = persistentCommand;
  }

  @Override
  public ProjectCreateBuilder create() {
    
    return new ProjectCreateBuilder() {
      private String name;
      
      @Override
      public ProjectCreateBuilder name(String name) {
        this.name = name;
        return this;
      }
      
      @Override
      public Project build() throws PmException {
        RepoAssert.notEmpty(name, () -> "name not defined!");
        Optional<Project> conflict = query().findByName(name);
        if(conflict.isPresent()) {
          throw new PmException(ImmutableConstraintViolation.builder()
              .id(conflict.get().getId())
              .rev(conflict.get().getRev())
              .constraint(ConstraintType.NOT_UNIQUE)
              .type(ErrorType.ACCESS)
              .build(), "entity: 'project' with name: '" + name + "' already exists!");
        }
        
        Project project = ImmutableProject.builder()
            .id(UUID.randomUUID().toString())
            .rev(UUID.randomUUID().toString())
            .name(name)
            .created(LocalDateTime.now())
            .build();
        return persistentCommand
            .create(visitor -> visitor.visitProject(project)).getProject()
            .get(project.getId());
      }
    };
  }
  
  @Override
  public ProjectQueryBuilder query() {
    return new ProjectQueryBuilder() {
      @Override
      public List<Project> find() {
        BiFunction<MongoClient, MongoDbConfig, List<Project>> mapper = (client, config) -> {
          List<Project> result = new ArrayList<>();          
          client
            .getDatabase(config.getDb())
            .getCollection(config.getProjects(), Project.class)
            .find().forEach((Consumer<Project>) result::add);
          
          return Collections.unmodifiableList(result);
        };
        return persistentCommand.map(mapper);
      }
      
      @Override
      public Project id(String id) throws PmException {
        RepoAssert.notNull(id, () -> "id not defined!");
        
        BiFunction<MongoClient, MongoDbConfig, Optional<Project>> mapper = (client, config) -> {
          Project value = client
              .getDatabase(config.getDb())
              .getCollection(config.getProjects(), Project.class)
              .find(Filters.eq(ProjectCodec.ID, id))
              .first();
          return Optional.ofNullable(value);
        };
        Optional<Project> result = persistentCommand.map(mapper);
        if(result.isEmpty()) {
          throw new PmException(ImmutableConstraintViolation.builder()
              .id(id)
              .rev("any")
              .constraint(ConstraintType.NOT_FOUND)
              .type(ErrorType.PROJECT)
              .build(), "entity: 'project' not found with id: '" + id + "'!"); 
        }
        return result.get();
      }
      
      @Override
      public Project rev(String id, String rev) throws PmException {
        RepoAssert.notNull(id, () -> "id not defined!");
        RepoAssert.notNull(rev, () -> "rev not defined!");
        Project project = id(id);
        
        if(!rev.equals(project.getRev())) {
          throw new PmRevException(ImmutableRevisionConflict.builder()
              .id(project.getId())
              .revToUpdate(rev)
              .rev(project.getRev())
              .build(), "revision conflict: 'project' with id: '" + project.getId() + "', revs: " + project.getRev() + " != " + rev + "!");
        }
        return project;
      }
      
      @Override
      public Optional<Project> findByName(String name) {
        RepoAssert.notNull(name, () -> "name not defined!");
        BiFunction<MongoClient, MongoDbConfig, Optional<Project>> mapper = (client, config) -> {
          Project value = client
              .getDatabase(config.getDb())
              .getCollection(config.getProjects(), Project.class)
              .find(Filters.eq(ProjectCodec.NAME, name))
              .first();
          return Optional.ofNullable(value);
        };
        return persistentCommand.map(mapper);
      }
      
      @Override
      public Optional<Project> find(String id) {
        BiFunction<MongoClient, MongoDbConfig, Optional<Project>> mapper = (client, config) -> {
          Project value = client
              .getDatabase(config.getDb())
              .getCollection(config.getProjects(), Project.class)
              .find(Filters.eq(ProjectCodec.ID, id))
              .first();
          return Optional.ofNullable(value);
        };
        return persistentCommand.map(mapper);
      }
    };
  }

  @Override
  public ProjectUpdateBuilder update() {
    return new ProjectUpdateBuilder() {
      private String id;
      private String rev;
      private String name;
      
      @Override
      public ProjectUpdateBuilder rev(String rev) {
        this.rev = rev;
        return this;
      }
      @Override
      public ProjectUpdateBuilder name(String name) {
        this.name = name;
        return this;
      }
      @Override
      public ProjectUpdateBuilder id(String id) {
        this.id = id;
        return this;
      }
      @Override
      public Project build() throws PmException {
        RepoAssert.notNull(id, () -> "id not defined!");
        RepoAssert.notNull(rev, () -> "rev not defined!");
        RepoAssert.notNull(name, () -> "name not defined!");

        Project project = ImmutableProject.builder()
            .from(query().rev(id, rev))
            .name(name)
            .build();        
        
        return persistentCommand
            .update(visitor -> visitor.visitProject(project))
            .getProject().get(project.getId());
      }
    };
  }

  @Override
  public ProjectDeleteBuilder delete() {
    return new ProjectDeleteBuilder() {
      private String id;
      private String rev;
      @Override
      public ProjectDeleteBuilder rev(String rev) {
        this.rev = rev;
        return this;
      }
      @Override
      public ProjectDeleteBuilder id(String id) {
        this.id = id;
        return this;
      }
      @Override
      public Project build() throws PmException {
        RepoAssert.notNull(id, () -> "id not defined!");
        RepoAssert.notNull(rev, () -> "rev not defined!");
        
        Project project = query().rev(id, rev);
        return persistentCommand
            .delete(visitor -> visitor.visitProject(project))
            .getProject().get(project.getId());
      }
    };
  }
}
