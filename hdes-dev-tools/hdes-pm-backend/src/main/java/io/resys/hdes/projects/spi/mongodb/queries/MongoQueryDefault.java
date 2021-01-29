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

import io.resys.hdes.projects.spi.mongodb.support.MongoWrapper;

public class MongoQueryDefault implements MongoQuery {
  private final MongoWrapper mongo;
  
  public MongoQueryDefault(MongoWrapper mongo) {
    super();
    this.mongo = mongo;
  }
  @Override
  public GroupQuery group() {
    return new MongoQueryGroup(mongo);
  }
  @Override
  public UserQuery user() {
    return new MongoQueryUser(mongo);
  }
  @Override
  public GroupUserQuery groupUser() {
    return new MongoQueryGroupUser(mongo);
  }
  @Override
  public AccessQuery access() {
    return new MongoQueryAccess(mongo);
  }
  @Override
  public ProjectQuery project() {
    return new MongoQueryProject(mongo);
  }
}
