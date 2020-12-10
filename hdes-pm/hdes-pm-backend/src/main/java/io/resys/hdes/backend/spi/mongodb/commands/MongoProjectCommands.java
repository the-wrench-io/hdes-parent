package io.resys.hdes.backend.spi.mongodb.commands;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;

import org.bson.conversions.Bson;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import io.resys.hdes.backend.api.ImmutableProject;
import io.resys.hdes.backend.api.PmException;
import io.resys.hdes.backend.api.PmException.ExceptionCode;
import io.resys.hdes.backend.api.PmRepository.Project;
import io.resys.hdes.backend.api.commands.ProjectCommands;
import io.resys.hdes.backend.spi.mongodb.PersistentCommand;
import io.resys.hdes.backend.spi.mongodb.PersistentCommand.MongoDbConfig;
import io.resys.hdes.backend.spi.mongodb.codecs.ProjectCodec;
import io.resys.hdes.backend.spi.support.RepoAssert;

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
          throw new PmException(ExceptionCode.DUPLICATE_PROJECT, "Project with the same name already exists!");
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
      public List<Project> list() {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public Project id(String id) throws PmException {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public Optional<Project> findByName(String name) {
        BiFunction<MongoClient, MongoDbConfig, Optional<Project>> mapper = (client, config) -> {
          
          MongoCollection<Project> collection = client
              .getDatabase(config.getDb())
              .getCollection(config.getProjects(), Project.class);
          collection.find().forEach(p -> {
            
            System.out.println(p);
          });;
          
          Bson filter = Filters.eq(ProjectCodec.NAME, name);
          Project value = collection.find(filter).first();
          return Optional.ofNullable(value);
        };
        return persistentCommand.map(mapper);
      }
      
      @Override
      public Optional<Project> find(String id) {
        // TODO Auto-generated method stub
        return null;
      }
    };
  }

  @Override
  public ProjectUpdateBuilder update() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ProjectDeleteBuilder delete() {
    return new ProjectDeleteBuilder() {
      @Override
      public ProjectDeleteBuilder rev(String rev) {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public ProjectDeleteBuilder id(String id) {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public Project build() throws PmException {
        // TODO Auto-generated method stub
        return null;
      }
    };
  }
}
