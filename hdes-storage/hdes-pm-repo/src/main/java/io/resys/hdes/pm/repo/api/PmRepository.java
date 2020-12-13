package io.resys.hdes.pm.repo.api;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;

import org.immutables.value.Value;

import io.resys.hdes.pm.repo.api.commands.AccessCommands;
import io.resys.hdes.pm.repo.api.commands.BatchCommands;
import io.resys.hdes.pm.repo.api.commands.ProjectCommands;
import io.resys.hdes.pm.repo.api.commands.UserCommands;

public interface PmRepository {

  ProjectCommands projects();
  UserCommands users();
  AccessCommands access();
  BatchCommands batch();
  
  @Value.Immutable
  interface Project extends Serializable {
    String getId();
    String getRev();
    String getName();
    LocalDateTime getCreated();
  }
  
  @Value.Immutable
  interface User extends Serializable {
    String getId();
    String getRev();
    LocalDateTime getCreated();
    Optional<String> getExternalId();
    String getValue();
    String getToken();
  }
  
  @Value.Immutable
  interface Access extends Serializable {
    String getId();
    String getRev();
    LocalDateTime getCreated();
    String getUserId();
    String getProjectId();
    String getName();
  }
}
