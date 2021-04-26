package io.resys.hdes.projects.spi.mongodb;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Updates;

import io.resys.hdes.projdb.api.model.ImmutableAccess;
import io.resys.hdes.projdb.api.model.ImmutableGroup;
import io.resys.hdes.projdb.api.model.ImmutableGroupUser;
import io.resys.hdes.projdb.api.model.ImmutableProject;
import io.resys.hdes.projdb.api.model.Resource.Access;
import io.resys.hdes.projdb.api.model.Resource.Group;
import io.resys.hdes.projdb.api.model.Resource.GroupUser;
import io.resys.hdes.projdb.api.model.Resource.Project;
import io.resys.hdes.projdb.api.model.Resource.User;
import io.resys.hdes.projdb.spi.builders.ObjectsQuery;
import io.resys.hdes.projdb.spi.context.DBContext;
import io.resys.hdes.projects.spi.mongodb.codecs.AccessCodec;
import io.resys.hdes.projects.spi.mongodb.codecs.CodecUtil;
import io.resys.hdes.projects.spi.mongodb.codecs.GroupCodec;
import io.resys.hdes.projects.spi.mongodb.codecs.GroupUserCodec;
import io.resys.hdes.projects.spi.mongodb.codecs.ProjectCodec;
import io.resys.hdes.projects.spi.mongodb.codecs.UserCodec;
import io.resys.hdes.projects.spi.mongodb.queries.MongoQueryDefault;
import io.resys.hdes.projects.spi.mongodb.queries.MongoQueryTemplate;
import io.resys.hdes.projects.spi.mongodb.queries.MongoQueryTemplate.QueryResultWithAccess;

public class MongoDBContext implements DBContext {
  private final MongoWrapper mongo;
  private final MongoQueryDefault query;
  public MongoDBContext(MongoWrapper mongo, MongoQueryDefault query) {
    super();
    this.mongo = mongo;
    this.query = query;
  }

  @Override
  public Project insert(Project project) {
    mongo.getDb().getCollection(mongo.getConfig().getProjects(), Project.class).insertOne(project);
    return project;
  }
  @Override
  public Group insert(Group group) {
    mongo.getDb().getCollection(mongo.getConfig().getGroups(), Group.class).insertOne(group);
    return group;
  }
  @Override
  public User insert(User user) {
    mongo.getDb().getCollection(mongo.getConfig().getUsers(), User.class).insertOne(user);
    return user;
  }
  @Override
  public GroupUser insert(GroupUser groupUser) {
    mongo.getDb().getCollection(mongo.getConfig().getGroupUsers(), GroupUser.class).insertOne(groupUser);
    return groupUser;
  }
  @Override
  public Access insert(Access access) {
    mongo.getDb().getCollection(mongo.getConfig().getAccess(), Access.class).insertOne(access);
    return access;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Project update(Project newStateWithOldRev) {
    final String id = newStateWithOldRev.getId();
    final String rev = newStateWithOldRev.getRev();
    final String name = newStateWithOldRev.getName();
  
    final QueryResultWithAccess<Project> queryResult = query.project().id(id).rev(rev)
        .unwrap(MongoQueryTemplate.class)
        .getWithFilter();
    
    final Project project;
    if(name.equals(queryResult.getValue().getName())) {
      project = queryResult.getValue();
    } else {
      final var newRev = UUID.randomUUID().toString();
      mongo.getDb().getCollection(mongo.getConfig().getProjects(), Project.class)
        .updateOne(queryResult.getFilter(), Updates.combine(
          Updates.set(ProjectCodec.NAME, name), 
          Updates.set(CodecUtil.REV, newRev)));
      project = ImmutableProject.builder()
          .from(queryResult.getValue())
          .name(name).rev(newRev)
          .build();
    }
    return project;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Group update(Group newStateWithOldRev) {
    final QueryResultWithAccess<Group> queryResult = query.group()
        .id(newStateWithOldRev.getId())
        .rev(newStateWithOldRev.getRev())
        .unwrap(MongoQueryTemplate.class)
        .getWithFilter();
    
    final String name = newStateWithOldRev.getName();
    final String matcher = newStateWithOldRev.getMatcher().orElse(null);
    
    if( name.equals(queryResult.getValue().getName()) &&
        (matcher == null && queryResult.getValue().getMatcher().isEmpty() ||
         queryResult.getValue().getMatcher().orElse("").equals(matcher)) ) {
      
      return queryResult.getValue();
    }
    
    final var newRev = UUID.randomUUID().toString();
    mongo.getDb().getCollection(mongo.getConfig().getGroups(), Group.class)
      .updateOne(queryResult.getFilter(), Updates.combine(
        Updates.set(GroupCodec.NAME, name), 
        Updates.set(GroupCodec.MATCHER, matcher), 
        Updates.set(CodecUtil.REV, newRev)));
    
    return ImmutableGroup.builder()
        .from(queryResult.getValue())
        .rev(newRev)
        .matcher(Optional.ofNullable(matcher))
        .name(name)
        .build();
  }

  @Override
  public User update(User newStateWithOldRev) {
    final var newRev = UUID.randomUUID().toString();
    final List<Bson> updates = new ArrayList<>();
    
    updates.add(Updates.set(CodecUtil.REV, newRev));          
    updates.add(Updates.set(UserCodec.NAME, newStateWithOldRev.getName()));
    updates.add(Updates.set(UserCodec.EMAIL, newStateWithOldRev.getEmail()));
    updates.add(Updates.set(UserCodec.EXTERNAL_ID, newStateWithOldRev.getExternalId().orElse(null)));
    updates.add(Updates.set(UserCodec.TOKEN, newStateWithOldRev.getToken()));
    updates.add(Updates.set(UserCodec.STATUS, newStateWithOldRev.getStatus().name()));
    
    final var filter = query.user()
        .id(newStateWithOldRev.getId()).rev(newStateWithOldRev.getRev())
        .unwrap(MongoQueryTemplate.class).filters();
    
    mongo.getDb().getCollection(mongo.getConfig().getUsers(), User.class)
    .updateOne(filter, Updates.combine(updates));
  
    return query.user().id(newStateWithOldRev.getId()).get();
  }

  @SuppressWarnings("unchecked")
  @Override
  public GroupUser update(GroupUser newStateWithOldRev) {

    QueryResultWithAccess<GroupUser> queryResult = query.groupUser().id(newStateWithOldRev.getId()).rev(newStateWithOldRev.getRev())
        .unwrap(MongoQueryTemplate.class)
        .getWithFilter();
    final var newRev = UUID.randomUUID().toString();
    
    mongo.getDb().getCollection(mongo.getConfig().getGroupUsers(), GroupUser.class)
      .updateOne(queryResult.getFilter(), Updates.combine(
        Updates.set(GroupUserCodec.USER_ID, newStateWithOldRev.getUserId()), 
        Updates.set(GroupUserCodec.GROUP_ID, newStateWithOldRev.getGroupId()), 
        Updates.set(CodecUtil.REV, newRev)));
    
    return ImmutableGroupUser.builder().from(newStateWithOldRev).rev(newRev).build();
  }

  @SuppressWarnings("unchecked")
  @Override
  public Access update(Access newStateWithOldRev) {
    
    QueryResultWithAccess<Access> queryResult = query.access()
        .id(newStateWithOldRev.getId()).rev(newStateWithOldRev.getRev())
        .unwrap(MongoQueryTemplate.class)
        .getWithFilter();
    final var newRev = UUID.randomUUID().toString();
    
    mongo.getDb().getCollection(mongo.getConfig().getAccess(), Access.class)
      .updateOne(queryResult.getFilter(), Updates.combine(
        Updates.set(AccessCodec.GROUP_ID, newStateWithOldRev.getGroupId()), 
        Updates.set(AccessCodec.USER_ID, newStateWithOldRev.getUserId()),
        Updates.set(AccessCodec.PROJECT_ID, newStateWithOldRev.getProjectId()),
        Updates.set(AccessCodec.COMMENT, newStateWithOldRev.getComment().orElse(null)),
        Updates.set(CodecUtil.REV, newRev)));
    
    return ImmutableAccess.builder().from(newStateWithOldRev).rev(newRev).build();
  }

  @SuppressWarnings("unchecked")
  @Override
  public Project deleteProject(String id, String rev) {
    final QueryResultWithAccess<Project> queryResult = query.project().id(id).rev(rev)
        .unwrap(MongoQueryTemplate.class)
        .getWithFilter();
    final var access = query.access()
        .project(queryResult.getValue().getId())
        .unwrap(MongoQueryTemplate.class)
        .filters();
    mongo.getDb().getCollection(mongo.getConfig().getProjects(), Project.class).deleteOne(queryResult.getFilter());
    mongo.getDb().getCollection(mongo.getConfig().getAccess(), Access.class).deleteMany(access);
    return queryResult.getValue();
  }

  @SuppressWarnings("unchecked")
  @Override
  public Group deleteGroup(String id, String rev) {
    QueryResultWithAccess<Group> queryResult = query.group().id(id).rev(rev)
        .unwrap(MongoQueryTemplate.class)
        .getWithFilter();
    
    mongo.getDb().getCollection(mongo.getConfig().getGroups(), Group.class)
      .deleteOne(queryResult.getFilter());
    
    mongo.getDb().getCollection(mongo.getConfig().getGroupUsers(), GroupUser.class)
      .deleteMany(query.groupUser().group(queryResult.getValue().getId()).unwrap(MongoQueryTemplate.class).filters());
    
    mongo.getDb().getCollection(mongo.getConfig().getAccess(), Access.class)
      .deleteMany(query.access().group(queryResult.getValue().getId()).unwrap(MongoQueryTemplate.class).filters());
    
    return queryResult.getValue();
  }

  @SuppressWarnings("unchecked")
  @Override
  public User deleteUser(String id, String rev) {

    QueryResultWithAccess<User> queryResult = query.user().id(id).rev(rev)
        .unwrap(MongoQueryTemplate.class)
        .getWithFilter();
    mongo.getDb().getCollection(mongo.getConfig().getUsers(), User.class)
      .deleteOne(queryResult.getFilter());
    
    mongo.getDb().getCollection(mongo.getConfig().getGroupUsers(), GroupUser.class)
      .deleteMany(query.groupUser().user(queryResult.getValue().getId()).unwrap(MongoQueryTemplate.class).filters());
    
    mongo.getDb().getCollection(mongo.getConfig().getAccess(), Access.class)
      .deleteMany(query.access().user(queryResult.getValue().getId()).unwrap(MongoQueryTemplate.class).filters());
    
    return queryResult.getValue();
  }

  @SuppressWarnings("unchecked")
  @Override
  public GroupUser deleteGroupUser(String id, String rev) {
    
    QueryResultWithAccess<GroupUser> queryResult = query.groupUser().id(id).rev(rev)
        .unwrap(MongoQueryTemplate.class)
        .getWithFilter();
    mongo.getDb().getCollection(mongo.getConfig().getGroupUsers(), GroupUser.class).deleteOne(queryResult.getFilter());

    return queryResult.getValue();
  }

  @SuppressWarnings("unchecked")
  @Override
  public Access deleteAccess(String id, String rev) {
    QueryResultWithAccess<Access> queryResult = query.access().id(id).rev(rev)
        .unwrap(MongoQueryTemplate.class)
        .getWithFilter();
    mongo.getDb().getCollection(mongo.getConfig().getAccess(), Access.class).deleteOne(queryResult.getFilter());
    return queryResult.getValue();
  }

  @Override
  public ObjectsQuery query() {
    return query;
  }
}
