package io.resys.hdes.pm.repo.tests;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;

/*-
 * #%L
 * hdes-object-repo-mongodb
 * %%
 * Copyright (C) 2020 Copyright 2020 ReSys OÜ
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.junit.jupiter.api.Test;

import io.resys.hdes.pm.repo.api.PmRepository;
import io.resys.hdes.pm.repo.api.PmRepository.Access;
import io.resys.hdes.pm.repo.api.PmRepository.Project;
import io.resys.hdes.pm.repo.api.PmRepository.User;
import io.resys.hdes.pm.repo.api.commands.BatchCommands.ProjectResource;
import io.resys.hdes.pm.repo.api.commands.BatchCommands.UserResource;
import io.resys.hdes.pm.repo.spi.mongodb.MongoPmRepository;

public class StorageServiceReadWriteTest {

  @Test
  public void createProjectUserAndAccess() {
    MongoDbFactory.instance(transaction -> {
      PmRepository repo = MongoPmRepository.builder().transaction(transaction).build();
      
      // Project create and find
      Project project = repo.projects().create().name("scoring project 1").build();
      Optional<Project> persistedProject = repo.projects().query().findByName(project.getName());
      Assertions.assertEquals(project.getName(), persistedProject.get().getName());
      
      // User create and find
      User user = repo.users().create().value("admin-user").build();
      Optional<User> persistedUser = repo.users().query().findByValue(user.getName());
      Assertions.assertEquals(user.getName(), persistedUser.get().getName());
      
      // Access create and find
      Access access = repo.access().create().name("admin-user-access-to-project-1").userId(user.getId()).projectId(project.getId()).build();
      Optional<Access> persisteAccess = repo.access().query().findByName(access.getName());
      Assertions.assertEquals(access.getName(), persisteAccess.get().getName());
    });
  }
  
  @Test
  public void createAndQueryUsersAndProjectBatch() {
    MongoDbFactory.instance(transaction -> {
      PmRepository repo = MongoPmRepository.builder().transaction(transaction).build();
      ProjectResource project = repo.batch().createProject()
        .projectName("pricing-project")
        .users("user-1", "user-2", "user-3")
        .createUser(true)
        .build();
   
      UserResource user1 = repo.batch().queryUsers().get("user-1");
      Assertions.assertEquals(1, user1.getAccess().size());
      Assertions.assertTrue(user1.getProjects().containsKey(project.getProject().getId()));
      
    });
  }
  
  
  @Test
  public void createAndQueryUsersGroupsAndProjectBatch() {
    MongoDbFactory.instance(transaction -> {
      PmRepository repo = MongoPmRepository.builder().transaction(transaction).build();
      
      ProjectResource project = repo.batch().createProject()
        .projectName("pricing-project")
        .groups("Group X1", "Group X2")
        .createUser(true)
        .build();
   
      repo.batch().createGroupUsers().createUser(true).users("user-1").groupId("Group X1").build();
      
      UserResource user1 = repo.batch().queryUsers().get("user-1");
      Assertions.assertEquals(1, user1.getAccess().size());
      Assertions.assertEquals(1, user1.getGroupUsers().size());
      Assertions.assertTrue(user1.getProjects().containsKey(project.getProject().getId()));
      
    });
  }
}
