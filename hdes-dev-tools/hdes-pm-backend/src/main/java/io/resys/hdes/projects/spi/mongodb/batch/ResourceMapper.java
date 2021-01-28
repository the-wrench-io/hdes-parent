package io.resys.hdes.projects.spi.mongodb.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.resys.hdes.projects.api.ImmutableGroupResource;
import io.resys.hdes.projects.api.ImmutableProjectResource;
import io.resys.hdes.projects.api.ImmutableUserResource;
import io.resys.hdes.projects.api.PmRepository.Access;
import io.resys.hdes.projects.api.PmRepository.Group;
import io.resys.hdes.projects.api.PmRepository.GroupResource;
import io.resys.hdes.projects.api.PmRepository.GroupUser;
import io.resys.hdes.projects.api.PmRepository.Project;
import io.resys.hdes.projects.api.PmRepository.ProjectResource;
import io.resys.hdes.projects.api.PmRepository.User;
import io.resys.hdes.projects.api.PmRepository.UserResource;
import io.resys.hdes.projects.spi.mongodb.builders.MongoBuilder;
import io.resys.hdes.projects.spi.mongodb.builders.MongoBuilderCreate;
import io.resys.hdes.projects.spi.mongodb.builders.MongoBuilderDelete;
import io.resys.hdes.projects.spi.mongodb.builders.MongoBuilderUpdate;
import io.resys.hdes.projects.spi.mongodb.queries.MongoQuery;
import io.resys.hdes.projects.spi.mongodb.queries.MongoQueryDefault;
import io.resys.hdes.projects.spi.mongodb.support.MongoWrapper;

public class ResourceMapper {

  public static MongoQuery query(MongoWrapper wrapper) {
    return new MongoQueryDefault(wrapper); 
  }

  public static MongoBuilder create(MongoWrapper wrapper) {
    return new MongoBuilderCreate(wrapper); 
  }
  
  public static MongoBuilder update(MongoWrapper wrapper) {
    return new MongoBuilderUpdate(wrapper); 
  }
  
  public static MongoBuilder delete(MongoWrapper wrapper) {
    return new MongoBuilderDelete(wrapper); 
  }
  
  public static GroupResource map(MongoQuery query, Group group) {
    
    List<Access> access = query.access().group(group.getId()).findAll();
    List<Project> projects = access
        .stream().map(e -> e.getProjectId()).collect(Collectors.toSet())
        .stream().map(e -> query.project().id(e).get())
        .collect(Collectors.toList());
    List<GroupUser> groupUsers = query.groupUser().group(group.getId()).findAll();
    List<User> users = groupUsers.stream().map(user -> query.user().id(user.getUserId()).get()).collect(Collectors.toList());
    
    return ImmutableGroupResource.builder()
      .group(group)
      .users(users.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
      .access(access.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
      .projects(projects.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
      .groupUser(groupUsers.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
      .build();
  }

  public static UserResource map(MongoQuery query, User user) {
    List<GroupUser> groupUsers = query.groupUser().user(user.getId()).findAll();
    List<Group> groups = groupUsers
        .stream().map(e -> e.getGroupId()).collect(Collectors.toSet())
        .stream().map(e -> query.group().id(e).get())
        .collect(Collectors.toList());
    
    List<Access> access = new ArrayList<>(query.access().user(user.getId()).findAll());
    groups.stream().map(g -> g.getId())
      .forEach(groupId -> query.access().group(groupId).findAll().forEach(a -> access.add(a)));        

    List<Project> projects = access
        .stream().map(e -> e.getProjectId()).collect(Collectors.toSet())
        .stream().map(e -> query.project().id(e).get())
        .collect(Collectors.toList());

    return ImmutableUserResource.builder()
      .user(user)
      .access(access.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
      .projects(projects.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
      .groups(groups.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
      .groupUsers(groupUsers.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
      .build();
  }
  
  public static ProjectResource map(MongoQuery query, Project project) {
    List<Access> access = query.access().project(project.getId()).findAll();
    List<User> users = new ArrayList<>(access
        .stream().filter(e -> e.getUserId().isPresent())
        .map(e -> e.getUserId().get()).collect(Collectors.toSet())
        .stream().map(e -> query.user().id(e).get())
        .collect(Collectors.toList()));
    
    Set<String> groupIds = access.stream()
        .filter(e -> e.getGroupId().isPresent())
        .map(e -> e.getGroupId().get())
        .collect(Collectors.toSet());
    
    List<GroupUser> groupUsers = new ArrayList<>();
    groupIds.stream().forEach(e -> query.groupUser().group(e).findAll().forEach(g -> groupUsers.add(g)));
    
    List<Group> groups = groupIds
        .stream().map(e -> query.group().id(e).get())
        .collect(Collectors.toList());

    // Add users from group
    groupUsers.forEach(user -> users.add(query.user().id(user.getUserId()).get()));
    
    return ImmutableProjectResource.builder()
      .project(project)
      .access(access.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
      .users(users.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
      .groups(groups.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
      .groupUsers(groupUsers.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
      .build();
  }
}
