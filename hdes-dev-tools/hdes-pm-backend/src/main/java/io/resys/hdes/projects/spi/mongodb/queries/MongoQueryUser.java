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

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import io.resys.hdes.projects.api.PmException.ErrorType;
import io.resys.hdes.projects.api.PmRepository.User;
import io.resys.hdes.projects.spi.mongodb.codecs.UserCodec;
import io.resys.hdes.projects.spi.mongodb.queries.MongoQuery.UserQuery;
import io.resys.hdes.projects.spi.mongodb.support.MongoWrapper;

public class MongoQueryUser extends MongoQueryTemplate<MongoQuery.UserQuery, User> implements MongoQuery.UserQuery {

  public MongoQueryUser(MongoWrapper mongo) {
    super(mongo);
  }

  @Override
  public UserQuery name(String name) {
    filters.add(Filters.eq(UserCodec.NAME, name));
    return this;
  }
  @Override
  public UserQuery token(String token) {
    filters.add(Filters.eq(UserCodec.TOKEN, token));
    return this;
  }
  @Override
  public UserQuery externalId(String externalId) {
    filters.add(Filters.eq(UserCodec.EXTERNAL_ID, externalId));
    return this;
  }
  @Override
  protected MongoCollection<User> getCollection() {
    MongoCollection<User> collection = mongo.getDb().getCollection(mongo.getConfig().getUsers(), User.class);
    return collection;
  }
  @Override
  protected ErrorType getErrorType() {
    return ErrorType.USER;
  }

}
