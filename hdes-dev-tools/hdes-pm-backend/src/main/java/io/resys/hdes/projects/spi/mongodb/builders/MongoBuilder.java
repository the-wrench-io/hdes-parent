package io.resys.hdes.projects.spi.mongodb.builders;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import io.resys.hdes.projects.api.PmRepository.Access;
import io.resys.hdes.projects.api.PmRepository.Group;
import io.resys.hdes.projects.api.PmRepository.GroupUser;
import io.resys.hdes.projects.api.PmRepository.Project;
import io.resys.hdes.projects.api.PmRepository.User;

public interface MongoBuilder {

  ProjectVisitor visitProject();
  GroupVisitor visitGroup();
  GroupUserVisitor visitGroupUser();
  AccessVisitor visitAccess();
  UserVisitor visitUser();
  MongoBuilderTree build();
  
  @Value.Immutable
  interface MongoBuilderTree {
    Map<String, Project> getProject();
    Map<String, Access> getAccess();
    Map<String, User> getUser();
    Map<String, Group> getGroups();
    Map<String, GroupUser> getGroupUsers();
  }
  
  interface Builder<V, E> {
    V visit(E entity);
    V visitId(String id);
    V visitRev(String rev);
    E build();
  }
  
  interface GroupUserVisitor extends Builder<GroupUserVisitor, GroupUser> {
    GroupUserVisitor visitUser(String userId);
    GroupUserVisitor visitGroup(String groupId);
  }
  
  interface AccessVisitor extends Builder<AccessVisitor, Access> {
    AccessVisitor visitUser(String userId);
    AccessVisitor visitGroup(String groupId);
    AccessVisitor visitProject(String projectId);
    AccessVisitor visitComment(String comment);
  }
  
  interface ProjectVisitor extends Builder<ProjectVisitor, Project> {
    ProjectVisitor visitName(String name);
    ProjectVisitor visitUsers(@Nullable List<String> users);
    ProjectVisitor visitGroups(@Nullable List<String> groups);
  }
  
  interface GroupVisitor extends Builder<GroupVisitor, Group> {
    GroupVisitor visitName(String name);
    GroupVisitor visitUsers(@Nullable List<String> users);
    GroupVisitor visitProjects(@Nullable List<String> projects);
  }

  interface UserVisitor extends Builder<UserVisitor, User> {
    UserVisitor visitName(String name);
    UserVisitor visitExternalId(String externalId);
    UserVisitor visitToken(String token);
    UserVisitor visitEmail(String email);
    UserVisitor visitGroups(@Nullable List<String> groups);
    UserVisitor visitProjects(@Nullable List<String> projects);
  }
}
