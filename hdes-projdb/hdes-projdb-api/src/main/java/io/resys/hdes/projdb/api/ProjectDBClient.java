package io.resys.hdes.projdb.api;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import io.resys.hdes.projdb.api.model.BatchMutator.BatchGroup;
import io.resys.hdes.projdb.api.model.BatchMutator.BatchProject;
import io.resys.hdes.projdb.api.model.BatchMutator.BatchUser;
import io.resys.hdes.projdb.api.model.BatchResource.GroupResource;
import io.resys.hdes.projdb.api.model.BatchResource.ProjectResource;
import io.resys.hdes.projdb.api.model.BatchResource.TokenResource;
import io.resys.hdes.projdb.api.model.BatchResource.UserResource;
import io.resys.hdes.projdb.api.model.ImmutableBatchGroup;
import io.resys.hdes.projdb.api.model.ImmutableBatchProject;
import io.resys.hdes.projdb.api.model.ImmutableBatchUser;
import io.resys.hdes.projdb.api.model.Resource.Group;
import io.resys.hdes.projdb.api.model.Resource.Project;
import io.resys.hdes.projdb.api.model.Resource.User;

public interface ProjectDBClient {

  BatchBuilder update();
  BatchBuilder create();
  BatchDelete delete();
  BatchQuery query();
  
  interface BatchDelete {
    Project project(String projectId, String rev);
    Group group(String groupId, String rev);
    User user(String userId, String rev);
  }
  
  interface BatchBuilder {
    ProjectResource project(Consumer<ImmutableBatchProject.Builder> builder);
    ProjectResource project(BatchProject project);
    
    GroupResource group(Consumer<ImmutableBatchGroup.Builder> builder);
    GroupResource group(BatchGroup group);
    
    UserResource user(Consumer<ImmutableBatchUser.Builder> builder);
    UserResource user(BatchUser user);
  }
  
  interface BatchQuery {
    BatchAdminsQuery admins();
    BatchTokensQuery tokens();
    BatchUserQuery users();
    BatchGroupQuery groups();
    BatchProjectQuery project();
  }

  interface BatchAdminsQuery {
    boolean isAdmin(String userName);
  }
  
  interface BatchTokensQuery {
    Optional<TokenResource> findOne(String token);
  }
  
  interface BatchProjectQuery {
    ProjectResource get(String idOrName);
    ProjectResource get(String id, String rev);
    List<ProjectResource> find();
  }
  
  interface BatchUserQuery {
    UserResource get(String idOrValueOrExternalIdOrToken);
    boolean isUser(String userName);
    List<UserResource> find();
  }

  interface BatchGroupQuery {
    GroupResource get(String idOrName);
    List<GroupResource> find();
  }
}
