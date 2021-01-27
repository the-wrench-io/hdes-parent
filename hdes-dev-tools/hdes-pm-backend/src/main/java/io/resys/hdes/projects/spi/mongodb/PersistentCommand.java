package io.resys.hdes.projects.spi.mongodb;

/*-
 * #%L
 * hdes-pm-repo
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

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.immutables.value.Value;

import com.mongodb.client.MongoClient;

import io.resys.hdes.projects.api.PmRepository.Access;
import io.resys.hdes.projects.api.PmRepository.Group;
import io.resys.hdes.projects.api.PmRepository.GroupUser;
import io.resys.hdes.projects.api.PmRepository.Project;
import io.resys.hdes.projects.api.PmRepository.User;

public interface PersistentCommand {

  PersistedEntities create(Consumer<EntityVisitor> consumer);
  PersistedEntities update(Consumer<EntityVisitor> consumer);
  PersistedEntities delete(Consumer<EntityVisitor> consumer);
  <T> T map(BiFunction<MongoClient, MongoDbConfig, T> consumer);
  
  

  @Value.Immutable
  public interface MongoDbConfig {
    String getDb();
    String getProjects();
    String getUsers();
    String getGroups();
    String getGroupUsers();
    String getAccess();
  }
  
  interface EntityVisitor {
    Project visitProject(Project project);
    Access visitAccess(Access access);
    User visitUser(User user);
    Group visitGroup(Group group);
    GroupUser visitGroupUser(GroupUser groupUser);
  }

  @Value.Immutable
  interface PersistedEntities {
    Map<String, Project> getProject();
    Map<String, Access> getAccess();
    Map<String, User> getUser();
    Map<String, Group> getGroups();
    Map<String, GroupUser> getGroupUsers();
  }
}
