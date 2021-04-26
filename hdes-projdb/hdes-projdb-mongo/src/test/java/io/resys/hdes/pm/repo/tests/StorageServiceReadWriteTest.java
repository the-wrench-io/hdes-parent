package io.resys.hdes.pm.repo.tests;

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

import io.resys.hdes.projdb.api.ProjectDBClient;
import io.resys.hdes.projdb.api.model.BatchResource.GroupResource;
import io.resys.hdes.projdb.api.model.BatchResource.ProjectResource;
import io.resys.hdes.projdb.api.model.BatchResource.UserResource;
import io.resys.hdes.projdb.api.model.ImmutableBatchGroup;
import io.resys.hdes.projdb.api.model.ImmutableBatchProject;
import io.resys.hdes.projdb.api.model.ImmutableBatchUser;
import io.resys.hdes.projdb.api.model.Resource.Group;
import io.resys.hdes.projdb.api.model.Resource.Project;
import io.resys.hdes.projdb.api.model.Resource.User;
import io.resys.hdes.projects.spi.mongodb.MongoProjectDBClient;

public class StorageServiceReadWriteTest {

  @Test
  public void createProjectUserAndAccess() {
    MongoDbFactory.instance(transaction -> {

      ProjectDBClient repo = MongoProjectDBClient.config().dbName("PM").transaction(transaction).build();
      
      // Project create and find
      Project project = repo.create().project(ImmutableBatchProject.builder().name("scoring project 1").build()).getProject();
      Project persistedProject = repo.query().project().get(project.getName()).getProject();
      Assertions.assertEquals(project.getName(), persistedProject.getName());
      
      // User create and find
      User user = repo.create().user(ImmutableBatchUser.builder().addProjects(persistedProject.getId()).name("admin-user").email("admin@trolls.com").build()).getUser();
      UserResource persistedUser = repo.query().users().get(user.getName());
      Assertions.assertEquals(user.getName(), persistedUser.getUser().getName());
      Assertions.assertEquals(1, persistedUser.getAccess().size());
      
    });
  }

  @Test
  public void createAndQueryUsersAndProjectBatch() {
    MongoDbFactory.instance(transaction -> {
      ProjectDBClient repo = MongoProjectDBClient.config().dbName("PM").transaction(transaction).build();
      
      User user1 = repo.create().user(ImmutableBatchUser.builder().name("admin-user1").email("admin@trolls.com").build()).getUser();
      User user2 = repo.create().user(ImmutableBatchUser.builder().name("admin-user2").email("admin@trolls.com").build()).getUser();
      User user3 = repo.create().user(ImmutableBatchUser.builder().name("admin-user3").email("admin@trolls.com").build()).getUser();
      
      repo.create().project(ImmutableBatchProject.builder()
          .name("pricing-project")
          .addUsers(user1.getId(), user2.getId(), user3.getId())
          .build());
   
      ProjectResource project = repo.query().project().get("pricing-project");
      Assertions.assertEquals(3, project.getAccess().size());
    });
  }
  
  @Test
  public void createAndQueryUsersGroupsAndProjectBatch() {
    MongoDbFactory.instance(transaction -> {
      ProjectDBClient repo = MongoProjectDBClient.config().dbName("PM").transaction(transaction).build();
      
      Group group1 = repo.create().group(ImmutableBatchGroup.builder().name("admins-1").build()).getGroup();
      Group group2 = repo.create().group(ImmutableBatchGroup.builder().name("admins-2").build()).getGroup();
      Group group3 = repo.create().group(ImmutableBatchGroup.builder().name("admins-3").build()).getGroup();
      
      ProjectResource project = repo.create().project(ImmutableBatchProject.builder()
          .name("pricing-project")
          .addGroups(group1.getId(), group2.getId(), group3.getId())
          .build());
      
      Assertions.assertEquals(3, project.getAccess().size());
      
    });
  }
  
  @Test
  public void groupMatcherTest() {
    MongoDbFactory.instance(transaction -> {
      ProjectDBClient repo = MongoProjectDBClient.config().dbName("PM").transaction(transaction).build();
      
      Group group1 = repo.create().group(ImmutableBatchGroup.builder().name("admins-1").matcher(".*@trolls\\.com$").build()).getGroup();
      Group group2 = repo.create().group(ImmutableBatchGroup.builder().name("admins-2").matcher(".*@humans\\.com$").build()).getGroup();
      Group group3 = repo.create().group(ImmutableBatchGroup.builder().name("admins-3").build()).getGroup();
      
      User user1 = repo.create().user(ImmutableBatchUser.builder().name("rockbreaker").email("rockbreaker@trolls.com").build()).getUser();
      User user2 = repo.create().user(ImmutableBatchUser.builder().name("bridgeguard").email("bridgeguard@trolls.com").build()).getUser();
      User user3 = repo.create().user(ImmutableBatchUser.builder().name("walker").email("walker@humans.com").build()).getUser();
      
      // trolls
      GroupResource groupResource = repo.query().groups().get(group1.getId());
      Assertions.assertEquals(2, groupResource.getUsers().size());
      Assertions.assertTrue(groupResource.getUsers().containsKey(user1.getId()));
      Assertions.assertTrue(groupResource.getUsers().containsKey(user2.getId()));

      // humans
      groupResource = repo.query().groups().get(group2.getId());
      Assertions.assertEquals(1, groupResource.getUsers().size());
      Assertions.assertTrue(groupResource.getUsers().containsKey(user3.getId()));
      
      // empty group
      groupResource = repo.query().groups().get(group3.getId());
      Assertions.assertEquals(0, groupResource.getUsers().size());
      
    });
  }
}
