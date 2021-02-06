package io.resys.hdes.projects.spi.mongodb.queries;

/*-
 * #%L
 * hdes-pm-backend
 * %%
 * Copyright (C) 2020 - 2021 Copyright 2020 ReSys OÜ
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

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.bson.conversions.Bson;
import org.immutables.value.Value;

import io.resys.hdes.projects.api.PmRepository.Access;
import io.resys.hdes.projects.api.PmRepository.Group;
import io.resys.hdes.projects.api.PmRepository.GroupType;
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
    
    Q id(Collection<String> id);
    Q id(String id);
    Q rev(String rev);
    
    QueryResultWithAccess<T> getWithFilter();
    T get();
    Optional<T> findOne();
    Optional<T> findFirst();
    List<T> findAll();
    void delete();
    
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
    GroupQuery type(GroupType groupType);
    GroupQuery name(String name);
    Collection<Group> matches(String ...values);
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
    AccessQuery user(Collection<String> userId);
    AccessQuery group(String groupId);
    AccessQuery group(Collection<String> groupId);
    AccessQuery project(String projectId);
    AccessQuery project(Collection<String> projectId);
  }
}
