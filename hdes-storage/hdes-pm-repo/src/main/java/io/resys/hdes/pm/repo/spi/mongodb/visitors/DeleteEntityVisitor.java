package io.resys.hdes.pm.repo.spi.mongodb.visitors;

import org.bson.conversions.Bson;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import io.resys.hdes.pm.repo.api.ImmutableConstraintViolation;
import io.resys.hdes.pm.repo.api.ImmutableRevisionConflict;
import io.resys.hdes.pm.repo.api.PmException;
import io.resys.hdes.pm.repo.api.PmException.ConstraintType;
import io.resys.hdes.pm.repo.api.PmException.ErrorType;
import io.resys.hdes.pm.repo.api.PmRepository.Access;
import io.resys.hdes.pm.repo.api.PmRepository.Project;
import io.resys.hdes.pm.repo.api.PmRepository.User;
import io.resys.hdes.pm.repo.api.PmRevException;
import io.resys.hdes.pm.repo.spi.mongodb.ImmutablePersistedEntities;
import io.resys.hdes.pm.repo.spi.mongodb.PersistentCommand.EntityVisitor;
import io.resys.hdes.pm.repo.spi.mongodb.PersistentCommand.MongoDbConfig;
import io.resys.hdes.pm.repo.spi.mongodb.codecs.AccessCodec;
import io.resys.hdes.pm.repo.spi.mongodb.codecs.ProjectCodec;
import io.resys.hdes.pm.repo.spi.mongodb.codecs.UserCodec;
import io.resys.hdes.pm.repo.spi.support.RepoAssert;

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
    client
      .getDatabase(config.getDb())
      .getCollection(config.getAccess(), Access.class)
      .find(Filters.eq(AccessCodec.PROJECT_ID, project.getId()))
      .forEach(access -> visitAccess(access));
    
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
    client
      .getDatabase(config.getDb())
      .getCollection(config.getAccess(), Access.class)
      .find(Filters.eq(AccessCodec.USER_ID, user.getId()))
      .forEach(access -> visitAccess(access));

    collection.deleteOne(filter);
    collect.putUser(user.getId(), user);
    return user;
  }
}
