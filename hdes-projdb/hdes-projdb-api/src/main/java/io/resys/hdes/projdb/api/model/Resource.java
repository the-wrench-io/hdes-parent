package io.resys.hdes.projdb.api.model;

import java.time.LocalDateTime;
import java.util.Optional;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public interface Resource {
  
  enum UserStatus { PENDING, ENABLED, DISABLED }
  enum GroupType { USER, ADMIN }
    
  String getId();
  String getRev();
  LocalDateTime getCreated();
  

  @JsonSerialize(as = ImmutableProject.class)
  @JsonDeserialize(as = ImmutableProject.class)
  @Value.Immutable
  interface Project extends Resource {
    String getName();
  }

  @JsonSerialize(as = ImmutableUser.class)
  @JsonDeserialize(as = ImmutableUser.class)
  @Value.Immutable
  interface User extends Resource {
    Optional<String> getExternalId();
    Optional<String> getEmail();
    String getName();
    String getToken();
    UserStatus getStatus();
  }
  
  @JsonSerialize(as = ImmutableGroup.class)
  @JsonDeserialize(as = ImmutableGroup.class)
  @Value.Immutable
  interface Group extends Resource {
    GroupType getType();
    Optional<String> getMatcher(); 
    String getName();
  }
  
  @JsonSerialize(as = ImmutableGroupUser.class)
  @JsonDeserialize(as = ImmutableGroupUser.class)
  @Value.Immutable
  interface GroupUser extends Resource {
    String getUserId();
    String getGroupId();
  }
  
  @JsonSerialize(as = ImmutableAccess.class)
  @JsonDeserialize(as = ImmutableAccess.class)
  @Value.Immutable
  interface Access extends Resource {
    String getProjectId();
    Optional<String> getComment();
    Optional<String> getUserId();
    Optional<String> getGroupId();
  }
}
