package io.resys.hdes.projects.spi.mongodb.queries;

import java.util.List;
import java.util.Optional;

import org.bson.conversions.Bson;
import org.immutables.value.Value;

import io.resys.hdes.projects.api.PmRepository.Access;
import io.resys.hdes.projects.api.PmRepository.Group;
import io.resys.hdes.projects.api.PmRepository.GroupUser;
import io.resys.hdes.projects.api.PmRepository.Project;
import io.resys.hdes.projects.api.PmRepository.User;

public interface MongoQuery {

  ProjectQuery project();
  GroupQuery group();
  UserQuery user();
  GroupUserQuery groupUser();
  AccessQuery access();
  
  interface Query<Q, T> {
    // set the query type to OR
    Q or();
    
    Q id(String id);
    Q rev(String rev);
    
    QueryResultWithAccess<T> getWithFilter();
    T get();
    Optional<T> findOne();
    List<T> findAll();
    
    Bson filters();
  }
  
  @Value.Immutable
  interface QueryResultWithAccess<T> {
    T getValue();
    Bson getFilter();
  }
  
  interface ProjectQuery extends Query<ProjectQuery, Project> {
    ProjectQuery name(String name);
  }
  interface GroupQuery extends Query<GroupQuery, Group> {
    GroupQuery name(String name);
  }
  interface UserQuery extends Query<UserQuery, User> {
    UserQuery externalId(String externalId);
    UserQuery name(String name);
    UserQuery token(String token);
  }

  interface GroupUserQuery extends Query<GroupUserQuery, GroupUser> {
    GroupUserQuery user(String userId);
    GroupUserQuery group(String groupId);
  }
  
  interface AccessQuery extends Query<AccessQuery, Access> {
    AccessQuery comment(String comment);
    AccessQuery user(String userId);
    AccessQuery group(String groupId);
    AccessQuery project(String projectId);
  }
}
