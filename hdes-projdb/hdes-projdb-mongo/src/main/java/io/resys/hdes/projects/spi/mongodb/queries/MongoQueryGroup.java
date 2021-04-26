package io.resys.hdes.projects.spi.mongodb.queries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

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

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import io.resys.hdes.projdb.api.PmException.ErrorType;
import io.resys.hdes.projdb.api.model.Resource.Group;
import io.resys.hdes.projdb.api.model.Resource.GroupType;
import io.resys.hdes.projdb.spi.builders.ObjectsQuery;
import io.resys.hdes.projdb.spi.builders.ObjectsQuery.GroupQuery;
import io.resys.hdes.projects.spi.mongodb.MongoWrapper;
import io.resys.hdes.projects.spi.mongodb.codecs.GroupCodec;

public class MongoQueryGroup extends MongoQueryTemplate<ObjectsQuery.GroupQuery, Group> implements ObjectsQuery.GroupQuery {

  public MongoQueryGroup(MongoWrapper mongo) {
    super(mongo);
  }

  @Override
  protected MongoCollection<Group> getCollection() {
    MongoCollection<Group> collection = mongo.getDb().getCollection(mongo.getConfig().getGroups(), Group.class);
    return collection;
  }
  @Override
  protected ErrorType getErrorType() {
    return ErrorType.GROUP;
  }
  @Override
  public GroupQuery type(GroupType groupType) {
    filters.add(Filters.eq(GroupCodec.GROUP_TYPE, groupType.name()));
    return this;
  }
  @Override
  public GroupQuery name(String name) {
    filters.add(Filters.eq(GroupCodec.NAME, name));
    return this;
  }
  @Override
  public Collection<Group> matches(String ...values) {
    if(values.length == 0) {
      return Collections.emptyList();
    }
    
    List<Group> result = new ArrayList<>();
    getCollection()
      .find(Filters.not(Filters.eq(GroupCodec.MATCHER, null)))
      .forEach((Consumer<Group>) group -> {
        for(String value : values) {
          if(value != null && !value.isBlank() && value.matches(group.getMatcher().get())) {
            result.add(group);
          }
        }
      });
    return Collections.unmodifiableList(result);
  }
}
