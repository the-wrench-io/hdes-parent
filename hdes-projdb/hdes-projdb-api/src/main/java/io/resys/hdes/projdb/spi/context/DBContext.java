package io.resys.hdes.projdb.spi.context;

import io.resys.hdes.projdb.api.model.Resource.Access;
import io.resys.hdes.projdb.api.model.Resource.Group;
import io.resys.hdes.projdb.api.model.Resource.GroupUser;
import io.resys.hdes.projdb.api.model.Resource.Project;
import io.resys.hdes.projdb.api.model.Resource.User;
import io.resys.hdes.projdb.spi.builders.ObjectsQuery;

public interface DBContext {
  ObjectsQuery query();
  
  Project insert(Project project);
  Group insert(Group group);
  User insert(User user);
  GroupUser insert(GroupUser groupUser);
  Access insert(Access access);
  
  Project update(Project newStateWithOldRev);
  Group update(Group newStateWithOldRev);
  User update(User newStateWithOldRev);
  GroupUser update(GroupUser newStateWithOldRev);
  Access update(Access newStateWithOldRev);
  
  Project deleteProject(String id, String rev);
  Group deleteGroup(String id, String rev);
  User deleteUser(String id, String rev);
  GroupUser deleteGroupUser(String id, String rev);
  Access deleteAccess(String id, String rev);
}
