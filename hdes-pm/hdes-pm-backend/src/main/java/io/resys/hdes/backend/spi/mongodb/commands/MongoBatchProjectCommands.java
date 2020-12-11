package io.resys.hdes.backend.spi.mongodb.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import io.resys.hdes.backend.api.ImmutableConstraintViolation;
import io.resys.hdes.backend.api.PmException;
import io.resys.hdes.backend.api.PmException.ConstraintType;
import io.resys.hdes.backend.api.PmException.ErrorType;
import io.resys.hdes.backend.api.PmRepository.Access;
import io.resys.hdes.backend.api.PmRepository.Project;
import io.resys.hdes.backend.api.PmRepository.User;
import io.resys.hdes.backend.api.commands.AccessCommands;
import io.resys.hdes.backend.api.commands.BatchProjectCommands;
import io.resys.hdes.backend.api.commands.ImmutableProjectResource;
import io.resys.hdes.backend.api.commands.ProjectCommands;
import io.resys.hdes.backend.api.commands.UserCommands;

public class MongoBatchProjectCommands implements BatchProjectCommands {

  private final ProjectCommands projectCommands;
  private final UserCommands userCommands;
  private final AccessCommands accessCommands;
  
  public MongoBatchProjectCommands(
      ProjectCommands projectCommands, 
      UserCommands userCommands,
      AccessCommands accessCommands) {
    super();
    this.projectCommands = projectCommands;
    this.userCommands = userCommands;
    this.accessCommands = accessCommands;
  }

  @Override
  public BatchProjectBuilder createProject() {
    return new BatchProjectBuilder() {
      private String projectName;
      private boolean createUsersIfNotFound;
      private List<String> users = Collections.emptyList();
      
      @Override
      public BatchProjectBuilder projectName(String projectName) {
        this.projectName = projectName;
        return this;
      }
      @Override
      public BatchProjectBuilder createUser(boolean createUsersIfNotFound) {
        this.createUsersIfNotFound = createUsersIfNotFound;
        return this;
      }
      @Override
      public BatchProjectBuilder users(String... userIdOrExternalIdOrValue) {
        users = new ArrayList<>(Arrays.asList(userIdOrExternalIdOrValue));
        return this;
      }
      @Override
      public ProjectResource build() {
        final var project = projectCommands.create().name(projectName).build();
        final var builder = ImmutableProjectResource.builder().project(project);
        
        for(String userFilter : users) {
          // get user by: 
          //   * id/value/externalId 
          //   * create new user
          final var user = userCommands.query().find(userFilter)
            .orElseGet(() -> userCommands.query().findByValue(userFilter)
                .orElseGet(() -> userCommands.query().findByExternalId(userFilter)
                    .orElseGet(() -> {
                      if(createUsersIfNotFound) {
                        return userCommands.create().value(userFilter).build();  
                      }
                      throw new PmException(ImmutableConstraintViolation.builder()
                          .id(userFilter)
                          .rev("")
                          .constraint(ConstraintType.NOT_FOUND)
                          .type(ErrorType.USER)
                          .build(), "entity not found: 'user'by one of the keys: 'value/externalId/id' = '" + userFilter + "' already exists!");
                    })));
          final var access = accessCommands.create()
              .projectId(project.getId())
              .userId(user.getId())
              .name(projectName + "-" + userFilter)
              .build();
          
          builder
            .putUsers(user.getId(), user)
            .putAccess(access.getId(), access);
        }
        return builder.build();
      }
    };
  }

  @Override
  public BatchProjectQuery queryProject() {
    return new BatchProjectQuery() {
      private ProjectResource map(Project project) {
        List<Access> access = accessCommands.query().projectId(project.getId()).find();
        List<User> users = access
            .stream().map(e -> e.getUserId()).collect(Collectors.toSet())
            .stream().map(e -> userCommands.query().id(e))
            .collect(Collectors.toList());
        
        return ImmutableProjectResource.builder()
          .project(project)
          .access(access.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
          .users(users.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)))
          .build();
      }
      @Override
      public ProjectResource get(String id) {
        return map(projectCommands.query().id(id));
      }
      @Override
      public List<ProjectResource> find() {
        return projectCommands.query().find().stream()
            .map(this::map)
            .collect(Collectors.toList());
      }
    };
  }
}
