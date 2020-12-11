package io.resys.hdes.backend.api;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;

import org.immutables.value.Value;

import io.resys.hdes.backend.api.commands.AccessCommands;
import io.resys.hdes.backend.api.commands.ProjectCommands;
import io.resys.hdes.backend.api.commands.UserCommands;

public interface PmRepository {

  ProjectCommands projects();
  UserCommands users();
  AccessCommands access();
  
  
  
  
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
  }
  
  @Value.Immutable
  interface Access extends Serializable {
    String getId();
    String getRev();
    LocalDateTime getCreated();
    String getUserId();
    String getProjectId();
    String getToken();
    String getName();
  }
}
