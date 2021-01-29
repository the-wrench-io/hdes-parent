package io.resys.hdes.projects.spi.mongodb.batch;

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
import java.util.stream.Collectors;

import io.resys.hdes.projects.api.PmRepository.BatchGroupQuery;
import io.resys.hdes.projects.api.PmRepository.BatchProjectQuery;
import io.resys.hdes.projects.api.PmRepository.BatchQuery;
import io.resys.hdes.projects.api.PmRepository.BatchUserQuery;
import io.resys.hdes.projects.api.PmRepository.GroupResource;
import io.resys.hdes.projects.api.PmRepository.ProjectResource;
import io.resys.hdes.projects.api.PmRepository.UserResource;
import io.resys.hdes.projects.spi.mongodb.queries.MongoQuery;

public class BatchQueryDefault implements BatchQuery {

  private final MongoQuery query;
  
  public BatchQueryDefault(MongoQuery query) {
    super();
    this.query = query;
  }
  
  @Override
  public BatchUserQuery users() {
    return new BatchUserQuery() {
      @Override
      public UserResource get(String idOrValueOrExternalIdOrToken) {
        final var any = idOrValueOrExternalIdOrToken;
        final var user = query.user()
            .id(any)
            .externalId(any)
            .token(any)
            .name(any)
            .or().get();
        return ResourceMapper.map(query, user);
      }
      @Override
      public List<UserResource> find() {
        return query.user().findAll().stream()
            .map(u -> ResourceMapper.map(query, u))
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
        
        return ResourceMapper.map(query, group);
      }
      
      @Override
      public List<GroupResource> find() {
        return query.group().findAll().stream()
            .map(u -> ResourceMapper.map(query, u))
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
        return ResourceMapper.map(query, project);
      }
      
      @Override
      public ProjectResource get(String idOrName) {
        final var any = idOrName;
        final var project = query.project()
            .id(any)
            .name(any)
            .or().get();
        return ResourceMapper.map(query, project);
      }
      
      @Override
      public List<ProjectResource> find() {
        return query.project().findAll().stream()
            .map(u -> ResourceMapper.map(query, u))
            .collect(Collectors.toList());
      }
    };
  }
  
}
