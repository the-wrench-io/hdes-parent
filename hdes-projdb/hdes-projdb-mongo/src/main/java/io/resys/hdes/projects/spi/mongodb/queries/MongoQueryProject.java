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

import io.resys.hdes.projdb.api.PmException.ErrorType;
import io.resys.hdes.projdb.api.model.Resource.Project;
import io.resys.hdes.projdb.spi.builders.ObjectsQuery;
import io.resys.hdes.projdb.spi.builders.ObjectsQuery.ProjectQuery;
import io.resys.hdes.projects.spi.mongodb.MongoWrapper;
import io.resys.hdes.projects.spi.mongodb.codecs.ProjectCodec;

public class MongoQueryProject extends MongoQueryTemplate<ObjectsQuery.ProjectQuery, Project> implements ObjectsQuery.ProjectQuery {

  public MongoQueryProject(MongoWrapper mongo) {
    super(mongo);
  }

  @Override
  public ProjectQuery name(String name) {
    filters.add(Filters.eq(ProjectCodec.NAME, name));
    return this;
  }
  @Override
  protected MongoCollection<Project> getCollection() {
    MongoCollection<Project> collection = mongo.getDb().getCollection(mongo.getConfig().getProjects(), Project.class);
    return collection;
  }
  @Override
  protected ErrorType getErrorType() {
    return ErrorType.PROJECT;
  }

}
