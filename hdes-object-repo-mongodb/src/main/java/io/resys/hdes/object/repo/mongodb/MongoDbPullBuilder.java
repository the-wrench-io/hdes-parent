package io.resys.hdes.object.repo.mongodb;

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

import io.resys.hdes.object.repo.api.ObjectRepository.Objects;
import io.resys.hdes.object.repo.api.ObjectRepository.PullBuilder;
import io.resys.hdes.object.repo.mongodb.MongoCommand.MongoDbConfig;

public class MongoDbPullBuilder implements PullBuilder {
  private final MongoCommand<Objects> command;
  private final MongoDbConfig mongoDbConfig;
  
  @Override
  public Objects build() {

    return null;
  }
}
