package io.resys.hdes.backend.spi.mongodb;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.immutables.value.Value;

import com.mongodb.client.MongoClient;

import io.resys.hdes.backend.api.PmRepository.Access;
import io.resys.hdes.backend.api.PmRepository.Project;
import io.resys.hdes.backend.api.PmRepository.User;

public interface PersistentCommand {

  PersistedEntities create(Consumer<EntityVisitor> consumer);
  PersistedEntities update(Consumer<EntityVisitor> consumer);
  PersistedEntities delete(Consumer<EntityVisitor> consumer);
  <T> T map(BiFunction<MongoClient, MongoDbConfig, T> consumer);
  
  

  @Value.Immutable
  public interface MongoDbConfig {
    String getDb();
    String getProjects();
    String getUsers();
    String getAccess();
  }
  
  interface EntityVisitor {
    Project visitProject(Project project);
    Access visitAccess(Access access);
    User visitUser(User user);
  }

  @Value.Immutable
  interface PersistedEntities {
    Map<String, Project> getProject();
    Map<String, Access> getAccess();
    Map<String, User> getUser();
  }
}
