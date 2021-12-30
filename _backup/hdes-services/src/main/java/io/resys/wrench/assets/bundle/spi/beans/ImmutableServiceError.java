package io.resys.wrench.assets.bundle.spi.beans;

/*-
 * #%L
 * wrench-component-assets
 * %%
 * Copyright (C) 2016 - 2017 Copyright 2016 ReSys OÜ
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

import org.springframework.util.Assert;

import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceError;

public class ImmutableServiceError implements ServiceError {

  private final String id;
  private final String message;

  public ImmutableServiceError(String id, String message) {
    super();
    Assert.notNull(id, "id can't be null!");
    Assert.notNull(message, "message can't be null!");
    this.id = id;
    this.message = message;
  }
  @Override
  public String getId() {
    return id;
  }
  @Override
  public String getMessage() {
    return message;
  }
}
