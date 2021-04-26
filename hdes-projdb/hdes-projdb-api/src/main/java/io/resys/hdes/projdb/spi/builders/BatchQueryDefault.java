package io.resys.hdes.projdb.spi.builders;

import java.util.Collection;

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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.resys.hdes.projdb.api.ProjectDBClient.BatchAdminsQuery;
import io.resys.hdes.projdb.api.ProjectDBClient.BatchGroupQuery;
import io.resys.hdes.projdb.api.ProjectDBClient.BatchProjectQuery;
import io.resys.hdes.projdb.api.ProjectDBClient.BatchQuery;
import io.resys.hdes.projdb.api.ProjectDBClient.BatchTokensQuery;
import io.resys.hdes.projdb.api.ProjectDBClient.BatchUserQuery;
import io.resys.hdes.projdb.api.model.BatchResource.GroupResource;
import io.resys.hdes.projdb.api.model.BatchResource.ProjectResource;
import io.resys.hdes.projdb.api.model.BatchResource.TokenResource;
import io.resys.hdes.projdb.api.model.BatchResource.UserResource;
import io.resys.hdes.projdb.api.model.ImmutableTokenAccessResource;
import io.resys.hdes.projdb.api.model.ImmutableTokenResource;
import io.resys.hdes.projdb.api.model.Resource.GroupType;
import io.resys.hdes.projdb.api.model.Resource.UserStatus;

public class BatchQueryDefault implements BatchQuery {

  private final ObjectsQuery query;
  
  public BatchQueryDefault(ObjectsQuery query) {
    super();
    this.query = query;
  }
  
  @Override
  public BatchTokensQuery tokens() {
    return new BatchTokensQuery() {
      
      @Override
      public Optional<TokenResource> findOne(String token) {
        final var user = query.user().token(token).findOne();
        if(user.isEmpty()) {
          return Optional.empty();
        }
        final UserResource resource = ResourceBuilder.map(query, user.get());
        return Optional.of(ImmutableTokenResource.builder()
            .name(resource.getUser().getName())
            .email(resource.getUser().getEmail())
            .id(resource.getUser().getToken())
            .addAllAccess(resource.getProjects().values().stream()
                .map(p -> ImmutableTokenAccessResource.builder()
                    .name(p.getName())
                    .id(p.getId())
                    .build())
                .collect(Collectors.toList()))
            .build());
      }
    };
  }
  
  @Override
  public BatchUserQuery users() {
    return new BatchUserQuery() {
      
      public boolean isUser(String userName) {
        return query.user().name(userName).findFirst().isPresent();
      }
      
      @Override
      public UserResource get(String idOrValueOrExternalIdOrToken) {
        final var any = idOrValueOrExternalIdOrToken;
        final var user = query.user()
            .id(any)
            .externalId(any)
            .token(any)
            .name(any)
            .or().get();
        return ResourceBuilder.map(query, user);
      }
      @Override
      public List<UserResource> find() {
        return query.user().findAll().stream()
            .map(u -> ResourceBuilder.map(query, u))
            .collect(Collectors.toList());
      }
    };
  }
  @Override
  public BatchGroupQuery groups() {
    return new BatchGroupQuery() {
      @Override
      public GroupResource get(String idOrName) {
        final var any = idOrName;
        final var group = query.group()
            .id(any)
            .name(any)
            .or().get();
        
        return ResourceBuilder.map(query, group);
      }
      
      @Override
      public List<GroupResource> find() {
        return query.group().findAll().stream()
            .map(u -> ResourceBuilder.map(query, u))
            .collect(Collectors.toList());
      }
    };
  }
  @Override
  public BatchProjectQuery project() {
    return new BatchProjectQuery() {
      @Override
      public ProjectResource get(String id, String rev) {
        final var project = query.project()
            .id(id)
            .rev(rev)
            .get();
        return ResourceBuilder.map(query, project);
      }
      
      @Override
      public ProjectResource get(String idOrName) {
        final var any = idOrName;
        final var project = query.project()
            .id(any)
            .name(any)
            .or().get();
        return ResourceBuilder.map(query, project);
      }
      
      @Override
      public List<ProjectResource> find() {
        return query.project().findAll().stream()
            .map(u -> ResourceBuilder.map(query, u))
            .collect(Collectors.toList());
      }
    };
  }

  @Override
  public BatchAdminsQuery admins() {
    return new BatchAdminsQuery() {
      @Override
      public boolean isAdmin(String userName) {
        final var user = query.user().name(userName).status(UserStatus.ENABLED).name(userName).findOne();
        if(user.isEmpty()) {
          return false;
        }
        Collection<String> groups = query.groupUser().user(user.get().getId())
          .findAll().stream()
          .map(groupUser -> groupUser.getGroupId())
          .collect(Collectors.toSet());
        
        return query.group().id(groups).type(GroupType.ADMIN).findFirst().isPresent();
      }
    };
  }
}
