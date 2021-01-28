package io.resys.hdes.projects.spi.mongodb.builders;

import java.util.List;
import java.util.UUID;

import com.mongodb.client.model.Updates;

import io.resys.hdes.projects.api.ImmutableAccess;
import io.resys.hdes.projects.api.ImmutableConstraintViolation;
import io.resys.hdes.projects.api.ImmutableGroup;
import io.resys.hdes.projects.api.ImmutableGroupUser;
import io.resys.hdes.projects.api.ImmutableProject;
import io.resys.hdes.projects.api.ImmutableUser;
import io.resys.hdes.projects.api.PmException;
import io.resys.hdes.projects.api.PmException.ConstraintType;
import io.resys.hdes.projects.api.PmException.ErrorType;
import io.resys.hdes.projects.api.PmRepository.Access;
import io.resys.hdes.projects.api.PmRepository.Group;
import io.resys.hdes.projects.api.PmRepository.GroupUser;
import io.resys.hdes.projects.api.PmRepository.Project;
import io.resys.hdes.projects.api.PmRepository.User;
import io.resys.hdes.projects.spi.mongodb.codecs.AccessCodec;
import io.resys.hdes.projects.spi.mongodb.codecs.CodecUtil;
import io.resys.hdes.projects.spi.mongodb.codecs.GroupCodec;
import io.resys.hdes.projects.spi.mongodb.codecs.GroupUserCodec;
import io.resys.hdes.projects.spi.mongodb.codecs.ProjectCodec;
import io.resys.hdes.projects.spi.mongodb.codecs.UserCodec;
import io.resys.hdes.projects.spi.mongodb.queries.MongoQuery;
import io.resys.hdes.projects.spi.mongodb.queries.MongoQuery.QueryResultWithAccess;
import io.resys.hdes.projects.spi.mongodb.queries.MongoQueryDefault;
import io.resys.hdes.projects.spi.mongodb.support.MongoWrapper;
import io.resys.hdes.projects.spi.support.RepoAssert;

public class MongoBuilderUpdate implements MongoBuilder {

  private final MongoWrapper mongo;
  private final MongoQuery query;
  private final ImmutableMongoBuilderTree.Builder collect;
  
  public MongoBuilderUpdate(MongoWrapper mongo) {
    this.mongo = mongo;
    this.query = new MongoQueryDefault(mongo);
    this.collect = ImmutableMongoBuilderTree.builder();
  }

  @Override
  public MongoBuilderTree build() {
    return collect.build();
  }
  
  @Override
  public ProjectVisitor visitProject() {
    return new ProjectVisitor() {
      private String id;
      private String rev;
      private String name;
      private List<String> users;
      private List<String> groups;
      
      @Override
      public Project build() throws PmException {
        RepoAssert.notEmptyAll(() -> "define id and rev!", id, rev);        
        RepoAssert.notEmpty(name, () -> "name not defined!");
        
        QueryResultWithAccess<Project> queryResult = query.project().id(id).rev(rev).getWithFilter();

        final var newRev = UUID.randomUUID().toString();
        mongo.getDb().getCollection(mongo.getConfig().getProjects(), Project.class)
          .updateOne(queryResult.getFilter(), Updates.combine(
            Updates.set(ProjectCodec.NAME, name), 
            Updates.set(CodecUtil.REV, newRev)));
        
        final var project = ImmutableProject.builder()
            .from(queryResult.getValue())
            .name(name).rev(newRev)
            .build();
        collect.putProject(project.getId(), project);
        
        if(users != null) {
          this.users.stream()
            .map(id -> query.user().id(id).get())
            .forEach(user -> visitAccess().visitProject(project.getId()).visitUser(user.getId()).build());
        }
        if(groups != null) {
          this.groups.stream()
            .map(id -> query.group().id(id).get())
            .forEach(group -> visitAccess().visitProject(project.getId()).visitGroup(group.getId()).build());
        }
        return project;
      }
      @Override
      public ProjectVisitor visit(Project project) {
        return visitId(project.getId())
            .visitRev(project.getRev())
            .visitName(project.getName());
      }
      @Override
      public ProjectVisitor visitUsers(List<String> users) {
        this.users = users;
        return this;
      }
      @Override
      public ProjectVisitor visitGroups(List<String> groups) {
        this.groups = groups;
        return this;
      }
      @Override
      public ProjectVisitor visitName(String name) {
        this.name = name;
        return this;
      }
      @Override
      public ProjectVisitor visitRev(String rev) {
        this.rev = rev;
        return this;
      }
      @Override
      public ProjectVisitor visitId(String id) {
        this.id = id;
        return this;
      }
    };
  }
  @Override
  public GroupVisitor visitGroup() {
    return new GroupVisitor() {
      private String id;
      private String rev;
      private String name;
      private List<String> users;
      private List<String> projects;
      
      @Override
      public Group build() {
        RepoAssert.notEmptyAll(() -> "define id and rev!", id, rev);      
        RepoAssert.notEmpty(name, () -> "name not defined!");
        
        QueryResultWithAccess<Group> queryResult = query.group().id(id).rev(rev).getWithFilter();
        final var newRev = UUID.randomUUID().toString();
        
        mongo.getDb().getCollection(mongo.getConfig().getGroups(), Group.class)
          .updateOne(queryResult.getFilter(), Updates.combine(
            Updates.set(GroupCodec.NAME, name), 
            Updates.set(CodecUtil.REV, newRev)));
        
        Group group = ImmutableGroup.builder()
            .from(queryResult.getValue())
            .rev(newRev)
            .name(name)
            .build();
        
        collect.putGroups(group.getId(), group);
        
        if(users != null) {
          this.users.stream()
            .map(id -> query.user().id(id).get())
            .forEach(user -> visitGroupUser().visitGroup(group.getId()).visitUser(user.getId()).build());
        }
        if(projects != null) {
          this.projects.stream()
            .map(id -> query.project().id(id).get())
            .forEach(project -> visitAccess().visitProject(project.getId()).visitGroup(group.getId()).build());
        }
        return group;
      }
      @Override
      public GroupVisitor visit(Group entity) {
        return visitId(entity.getId())
            .visitRev(entity.getRev())
            .visitName(entity.getName());
      }
      @Override
      public GroupVisitor visitUsers(List<String> users) {
        this.users = users;
        return this;
      }
      @Override
      public GroupVisitor visitProjects(List<String> projects) {
        this.projects = projects;
        return this;
      }
      @Override
      public GroupVisitor visitName(String name) {
        this.name = name;
        return this;
      }
      @Override
      public GroupVisitor visitRev(String rev) {
        this.rev = rev;
        return this;
      }
      @Override
      public GroupVisitor visitId(String id) {
        this.id = id;
        return this;
      }
    };
  }
  @Override
  public UserVisitor visitUser() {
    return new UserVisitor() {
      private String id;
      private String rev;
      private String name;
      private String email;
      private String externalId;
      private String token;
      private List<String> groups;
      private List<String> projects;
      
      @Override
      public User build() {
        RepoAssert.notEmptyAll(() -> "define id and rev!", id, rev);    
        RepoAssert.notEmpty(name, () -> "define name!");
        RepoAssert.notEmpty(email, () -> "define email!");
        RepoAssert.notEmpty(externalId, () -> "define externalId!");
        RepoAssert.notEmpty(token, () -> "define token!");
        
        QueryResultWithAccess<User> queryResult = query.user().id(id).rev(rev).getWithFilter();
        
        if(token.equals(queryResult.getValue().getToken()) && query.user().token(token).findOne().isPresent()) {
          throw new PmException(ImmutableConstraintViolation.builder()
              .id(queryResult.getValue().getId())
              .rev(queryResult.getValue().getRev())
              .constraint(ConstraintType.NOT_UNIQUE)
              .type(ErrorType.USER)
              .build(), () -> "entity: 'user' with token: '" + token + "' already exists!");
        }
        
        final var newRev = UUID.randomUUID().toString();
        mongo.getDb().getCollection(mongo.getConfig().getUsers(), User.class)
        .updateOne(queryResult.getFilter(), Updates.combine(
          Updates.set(UserCodec.NAME, name), 
          Updates.set(UserCodec.EMAIL, email), 
          Updates.set(UserCodec.EXTERNAL_ID, externalId),
          Updates.set(UserCodec.TOKEN, token),
          Updates.set(CodecUtil.REV, newRev)));
      
        User user = ImmutableUser.builder()
            .from(queryResult.getValue())
            .rev(newRev)
            .name(name)
            .email(email)
            .externalId(externalId)
            .token(token)
            .build();
        
        
        if(groups != null) {
          this.groups.stream()
            .map(id -> query.group().id(id).get())
            .forEach(group -> visitGroupUser().visitGroup(group.getId()).visitUser(user.getId()).build());
        }
        if(projects != null) {
          this.projects.stream()
            .map(id -> query.project().id(id).get())
            .forEach(project -> visitAccess().visitProject(project.getId()).visitUser(user.getId()).build());
        }
        return user;
      }
      @Override
      public UserVisitor visitProjects(List<String> projects) {
        this.projects = projects;
        return this;
      }
      @Override
      public UserVisitor visitName(String name) {
        this.name = name;
        return this;
      }
      @Override
      public UserVisitor visitGroups(List<String> groups) {
        this.groups = groups;
        return this;
      }
      @Override
      public UserVisitor visitEmail(String email) {
        this.email = email;
        return this;
      }
      @Override
      public UserVisitor visitToken(String token) {
        this.token = token;
        return this;
      }
      @Override
      public UserVisitor visitExternalId(String externalId) {
        this.externalId = externalId;
        return this;
      }
      @Override
      public UserVisitor visit(User entity) {
        return visitId(entity.getId())
            .visitRev(entity.getRev())
            .visitName(entity.getName())
            .visitToken(entity.getToken())
            .visitExternalId(entity.getExternalId().orElse(null))
            .visitEmail(entity.getEmail());
      }
      @Override
      public UserVisitor visitRev(String rev) {
        this.rev = rev;
        return this;
      }
      @Override
      public UserVisitor visitId(String id) {
        this.id = id;
        return this;
      }
    };
  }

  @Override
  public GroupUserVisitor visitGroupUser() {
    return new GroupUserVisitor() {
      private String id;
      private String rev;
      private String userId;
      private String groupId;
      @Override
      public GroupUser build() {
        RepoAssert.notEmptyAll(() -> "define id and rev!", id, rev);        
        RepoAssert.notEmpty(userId, () -> "userId not defined!");
        RepoAssert.notEmpty(groupId, () -> "groupId not defined!");
        
        QueryResultWithAccess<GroupUser> queryResult = query.groupUser().id(id).rev(rev).getWithFilter();
        final var newRev = UUID.randomUUID().toString();
        
        mongo.getDb().getCollection(mongo.getConfig().getGroupUsers(), GroupUser.class)
          .updateOne(queryResult.getFilter(), Updates.combine(
            Updates.set(GroupUserCodec.USER_ID, userId), 
            Updates.set(GroupUserCodec.GROUP_ID, groupId), 
            Updates.set(CodecUtil.REV, newRev)));
        
        GroupUser groupUser = ImmutableGroupUser.builder()
            .from(queryResult.getValue())
            .rev(newRev)
            .userId(userId)
            .groupId(groupId)
            .build();
        collect.putGroupUsers(groupUser.getId(), groupUser);
        return groupUser;
      }
      @Override
      public GroupUserVisitor visitRev(String rev) {
        this.rev = rev;
        return this;
      }
      @Override
      public GroupUserVisitor visitId(String id) {
        this.id = id;
        return this;
      }
      @Override
      public GroupUserVisitor visit(GroupUser entity) {
        return visitId(entity.getId())
            .visitRev(entity.getRev())
            .visitUser(entity.getUserId())
            .visitGroup(entity.getGroupId());
      } 
      @Override
      public GroupUserVisitor visitUser(String userId) {
        this.userId = userId;
        return this;
      }      
      @Override
      public GroupUserVisitor visitGroup(String groupId) {
        this.groupId = groupId;
        return this;
      }
    };
  }

  @Override
  public AccessVisitor visitAccess() {
    return new AccessVisitor() {
      private String id;
      private String rev;
      private String userId;
      private String groupId;
      private String projectId;
      private String comment;
      
      @Override
      public Access build() {
        RepoAssert.notEmptyAll(() -> "define id and rev!", id, rev);        
        RepoAssert.notEmptyAtLeastOne(() -> "userId or groupId not defined!", groupId, userId);
        RepoAssert.notEmpty(projectId, () -> "projectId not defined!");
        
        QueryResultWithAccess<Access> queryResult = query.access().id(id).rev(rev).getWithFilter();
        final var newRev = UUID.randomUUID().toString();
        
        mongo.getDb().getCollection(mongo.getConfig().getAccess(), Access.class)
          .updateOne(queryResult.getFilter(), Updates.combine(
            Updates.set(AccessCodec.GROUP_ID, groupId), 
            Updates.set(AccessCodec.USER_ID, userId),
            Updates.set(AccessCodec.PROJECT_ID, projectId),
            Updates.set(AccessCodec.COMMENT, comment),
            Updates.set(CodecUtil.REV, newRev)));
        
        Access access = ImmutableAccess.builder()
            .from(queryResult.getValue())
            .rev(newRev)
            .userId(userId)
            .groupId(groupId)
            .projectId(projectId)
            .comment(comment)
            .build();
        
        collect.putAccess(access.getId(), access);
        return access;
      }
      @Override
      public AccessVisitor visitUser(String userId) {
        this.userId = userId;
        return this;
      }
      @Override
      public AccessVisitor visitProject(String projectId) {
        this.projectId = projectId;
        return this;
      }
      @Override
      public AccessVisitor visitGroup(String groupId) {
        this.groupId = groupId;
        return this;
      }
      @Override
      public AccessVisitor visitComment(String comment) {
        this.comment = comment;
        return this;
      }
      @Override
      public AccessVisitor visit(Access entity) {
        return visitId(entity.getId())
            .visitRev(entity.getRev())
            .visitComment(entity.getComment().orElse(null))
            .visitGroup(entity.getGroupId().orElse(null))
            .visitUser(entity.getUserId().orElse(null))
            .visitProject(entity.getProjectId());
      }
      @Override
      public AccessVisitor visitRev(String rev) {
        this.rev = rev;
        return this;
      }
      @Override
      public AccessVisitor visitId(String id) {
        this.id = id;
        return this;
      }
    };
  }
}
