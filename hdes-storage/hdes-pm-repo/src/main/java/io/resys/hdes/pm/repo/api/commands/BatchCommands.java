package io.resys.hdes.pm.repo.api.commands;

import java.util.List;
import java.util.Map;

import org.immutables.value.Value;

import io.resys.hdes.pm.repo.api.PmRepository.Access;
import io.resys.hdes.pm.repo.api.PmRepository.Project;
import io.resys.hdes.pm.repo.api.PmRepository.User;

public interface BatchCommands {

  BatchProjectBuilder createProject();
  BatchProjectQuery queryProject();
  BatchUserQuery queryUsers();
  
  interface BatchUserQuery {
    UserResource get(String idOrValueOrExternalIdOrToken);
    List<UserResource> find();
  }
  
  interface BatchProjectBuilder {
    BatchProjectBuilder projectName(String projectName);
    BatchProjectBuilder users(String ... userIdOrExternalIdOrValue);
    BatchProjectBuilder createUser(boolean createUsersIfNotFound);
    ProjectResource build();
  }
  
  interface BatchProjectQuery {
    ProjectResource get(String id);
    List<ProjectResource> find();
  }
  
  @Value.Immutable
  interface ProjectResource {
    Project getProject();
    Map<String, User> getUsers();
    Map<String, Access> getAccess();
  }
  
  
  @Value.Immutable
  interface UserResource {
    User getUser();
    Map<String, Project> getProjects();
    Map<String, Access> getAccess();
  }
}
