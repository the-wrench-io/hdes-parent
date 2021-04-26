package io.resys.hdes.projdb.spi;

import java.util.Optional;
import java.util.function.Consumer;

import io.resys.hdes.projdb.api.ProjectDBClient;
import io.resys.hdes.projdb.api.model.BatchMutator.BatchGroup;
import io.resys.hdes.projdb.api.model.BatchMutator.BatchProject;
import io.resys.hdes.projdb.api.model.BatchMutator.BatchUser;
import io.resys.hdes.projdb.api.model.BatchResource.GroupResource;
import io.resys.hdes.projdb.api.model.BatchResource.ProjectResource;
import io.resys.hdes.projdb.api.model.BatchResource.UserResource;
import io.resys.hdes.projdb.api.model.ImmutableBatchGroup;
import io.resys.hdes.projdb.api.model.ImmutableBatchProject;
import io.resys.hdes.projdb.api.model.ImmutableBatchUser;
import io.resys.hdes.projdb.api.model.Resource.Group;
import io.resys.hdes.projdb.api.model.Resource.Project;
import io.resys.hdes.projdb.api.model.Resource.User;
import io.resys.hdes.projdb.spi.builders.BatchQueryDefault;
import io.resys.hdes.projdb.spi.builders.MongoBuilderCreate;
import io.resys.hdes.projdb.spi.builders.MongoBuilderDelete;
import io.resys.hdes.projdb.spi.builders.MongoBuilderUpdate;
import io.resys.hdes.projdb.spi.builders.ResourceBuilder;
import io.resys.hdes.projdb.spi.context.DBCommand;

public class ProjectDBClientDefault implements ProjectDBClient {
  
  private final DBCommand dbCommand;
  
  public ProjectDBClientDefault(DBCommand dbCommand) {
    super();
    this.dbCommand = dbCommand;
  }
  
  @Override
  public BatchDelete delete() {
    return new BatchDelete() {
      @Override
      public User user(String userId, String rev) {
        return dbCommand.accept(client -> {
          final var builder = new MongoBuilderDelete(client);
          User deleted = builder.visitUser().visitRev(rev).visitId(userId).build(); 
          builder.build();
          return deleted;          
        });
      }      
      @Override
      public Project project(String projectId, String rev) {
        return dbCommand.accept(client -> {
          final var builder = new MongoBuilderDelete(client);
          Project deleted = builder.visitProject().visitRev(rev).visitId(projectId).build(); 
          builder.build();
          return deleted;
        });
      }
      @Override
      public Group group(String groupId, String rev) {
        return dbCommand.accept(client -> {
          final var builder = new MongoBuilderDelete(client);
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
        return dbCommand.accept(client -> {
          final var builder = new MongoBuilderUpdate(client);
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
          return ResourceBuilder.map(client.query(), updated);
        });
      }
      @Override
      public ProjectResource project(BatchProject project) {
        return dbCommand.accept(client -> {
          final var builder = new MongoBuilderUpdate(client);
          Project updated = builder.visitProject()
              .visitId(project.getId())
              .visitRev(project.getRev())
              .visitName(project.getName())
              .visitGroups(project.getGroups())
              .visitUsers(project.getUsers())
              .build();
          builder.build();
          return ResourceBuilder.map(client.query(), updated);
        });
      }
      @Override
      public GroupResource group(BatchGroup group) {
        return dbCommand.accept(client -> {
          final var builder = new MongoBuilderUpdate(client);
          Group updated = builder.visitGroup()
              .visitId(group.getId())
              .visitRev(group.getRev())
              .visitName(group.getName())
              .visitProjects(group.getProjects())
              .visitUsers(group.getUsers())
              .visitMatcher(group.getMatcher())
              .build(); 
          builder.build();
          return ResourceBuilder.map(client.query(), updated);
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
        return dbCommand.accept(client -> {
          final var builder = new MongoBuilderCreate(client);
          User created = builder.visitUser()
              .visitName(user.getName())
              .visitExternalId(user.getExternalId() == null ? null : Optional.of(user.getExternalId()))
              .visitEmail(user.getEmail())
              .visitGroups(user.getGroups())
              .visitProjects(user.getProjects())
              .build();
          builder.build();
          return ResourceBuilder.map(client.query(), created);
        });
      }
      @Override
      public ProjectResource project(BatchProject project) {
        return dbCommand.accept(client -> {
          final var builder = new MongoBuilderCreate(client);
          Project created = builder.visitProject()
              .visitName(project.getName())
              .visitGroups(project.getGroups())
              .visitUsers(project.getUsers())
              .build();
          builder.build();
          return ResourceBuilder.map(client.query(), created);
        });
      }
      @Override
      public GroupResource group(BatchGroup group) {
        return dbCommand.accept(client -> {      
          final var builder = new MongoBuilderCreate(client);
          Group created = builder.visitGroup()
              .visitName(group.getName())
              .visitProjects(group.getProjects())
              .visitUsers(group.getUsers())
              .visitMatcher(group.getMatcher())
              .visitType(group.getType())
              .build(); 
          builder.build();
          return ResourceBuilder.map(client.query(), created);
        });
      }
    };
  }
  
  @Override
  public BatchQuery query() {
    return dbCommand.accept(client -> {               
      return new BatchQueryDefault(client.query());
    });
  } 
}
