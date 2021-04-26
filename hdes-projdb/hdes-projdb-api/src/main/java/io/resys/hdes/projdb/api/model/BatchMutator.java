package io.resys.hdes.projdb.api.model;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.resys.hdes.projdb.api.model.Resource.GroupType;
import io.resys.hdes.projdb.api.model.Resource.UserStatus;

public interface BatchMutator {
  @Nullable
  String getId();
  @Nullable
  String getRev();
  
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableBatchProject.class)
  @JsonDeserialize(as = ImmutableBatchProject.class)
  interface BatchProject extends BatchMutator {
    @Nullable
    String getName();
    @Nullable
    List<String> getUsers();
    @Nullable
    List<String> getGroups();
  }

  @Value.Immutable
  @JsonSerialize(as = ImmutableBatchGroup.class)
  @JsonDeserialize(as = ImmutableBatchGroup.class)
  interface BatchGroup extends BatchMutator {
    @Nullable
    String getName();
    @Nullable
    String getMatcher();
    @Nullable
    List<String> getUsers();
    @Nullable
    List<String> getProjects();
    @Nullable
    GroupType getType();
  }

  @Value.Immutable
  @JsonSerialize(as = ImmutableBatchUser.class)
  @JsonDeserialize(as = ImmutableBatchUser.class)
  interface BatchUser extends BatchMutator {
    @Nullable
    String getName();
    @Nullable
    String getEmail();
    @Nullable
    String getExternalId();
    @Nullable
    UserStatus getStatus();
    @Nullable
    List<String> getProjects();
    @Nullable
    List<String> getGroups();
  }
}
