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

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import io.resys.hdes.projdb.api.PmException.ErrorType;
import io.resys.hdes.projdb.api.model.Resource.Access;
import io.resys.hdes.projdb.spi.builders.ObjectsQuery;
import io.resys.hdes.projdb.spi.builders.ObjectsQuery.AccessQuery;
import io.resys.hdes.projects.spi.mongodb.MongoWrapper;
import io.resys.hdes.projects.spi.mongodb.codecs.AccessCodec;

public class MongoQueryAccess extends MongoQueryTemplate<ObjectsQuery.AccessQuery, Access> implements ObjectsQuery.AccessQuery {

  public MongoQueryAccess(MongoWrapper mongo) {
    super(mongo);
  }
  public AccessQuery user(Collection<String> id) {
    filters.add(Filters.in(AccessCodec.USER_ID, id));
    return this;
  }
  public AccessQuery group(Collection<String> id) {
    filters.add(Filters.in(AccessCodec.GROUP_ID, id));
    return this;
  }
  public AccessQuery project(Collection<String> id) {
    filters.add(Filters.in(AccessCodec.PROJECT_ID, id));
    return this;
  }   
  @Override
  public AccessQuery comment(String comment) {
    filters.add(Filters.eq(AccessCodec.COMMENT, comment));
    return this;
  }
  @Override
  public AccessQuery user(String userId) {
    filters.add(Filters.eq(AccessCodec.USER_ID, userId));
    return this;
  }
  @Override
  public AccessQuery group(String groupId) {
    filters.add(Filters.eq(AccessCodec.GROUP_ID, groupId));
    return this;
  }
  @Override
  public AccessQuery project(String projectId) {
    filters.add(Filters.eq(AccessCodec.PROJECT_ID, projectId));
    return this;
  }
  @Override
  protected MongoCollection<Access> getCollection() {
    MongoCollection<Access> collection = mongo.getDb().getCollection(mongo.getConfig().getAccess(), Access.class);
    return collection;
  }
  @Override
  protected ErrorType getErrorType() {
    return ErrorType.ACCESS;
  }
}
