package io.resys.hdes.backend.api.commands;

import java.util.List;
import java.util.Map;

import org.immutables.value.Value;

import io.resys.hdes.backend.api.PmRepository.Access;
import io.resys.hdes.backend.api.PmRepository.Project;
import io.resys.hdes.backend.api.PmRepository.User;

public interface BatchProjectCommands {

  BatchProjectBuilder createProject();
  BatchProjectQuery queryProject();
  
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
  
}
