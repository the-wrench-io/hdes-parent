package io.resys.hdes.projdb.api.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.resys.hdes.projdb.api.model.Resource.Access;
import io.resys.hdes.projdb.api.model.Resource.Group;
import io.resys.hdes.projdb.api.model.Resource.GroupUser;
import io.resys.hdes.projdb.api.model.Resource.Project;
import io.resys.hdes.projdb.api.model.Resource.User;

public interface BatchResource {
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableProjectResource.class)
  @JsonDeserialize(as = ImmutableProjectResource.class)
  interface ProjectResource extends BatchResource {
    Project getProject();
    Map<String, User> getUsers();
    Map<String, Access> getAccess();
    Map<String, Group> getGroups();
    Map<String, GroupUser> getGroupUsers();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableUserResource.class)
  @JsonDeserialize(as = ImmutableUserResource.class)
  interface UserResource extends BatchResource {
    User getUser();
    Map<String, Project> getProjects();
    Map<String, Access> getAccess();
    Map<String, Group> getGroups();
    Map<String, GroupUser> getGroupUsers();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableGroupResource.class)
  @JsonDeserialize(as = ImmutableGroupResource.class)
  interface GroupResource extends BatchResource {
    Group getGroup();
    Map<String, User> getUsers();
    Map<String, Project> getProjects();
    Map<String, Access> getAccess();
    Map<String, GroupUser> getGroupUser();
  }
  
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableTokenResource.class)
  @JsonDeserialize(as = ImmutableTokenResource.class)
  interface TokenResource extends BatchResource {
    String getName();
    Optional<String> getEmail();
    String getId();
    List<TokenAccessResource> getAccess();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableTokenAccessResource.class)
  @JsonDeserialize(as = ImmutableTokenAccessResource.class)
  interface TokenAccessResource extends BatchResource {
    String getId();
    String getName();
  }
}
