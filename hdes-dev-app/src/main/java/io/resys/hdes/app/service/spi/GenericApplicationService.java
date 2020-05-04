package io.resys.hdes.app.service.spi;

/*-
 * #%L
 * hdes-dev-app
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

import io.resys.hdes.app.service.api.ApplicationService;
import io.resys.hdes.app.service.spi.builders.GenericApplicationExceptionBuilder;
import io.resys.hdes.app.service.spi.builders.GenericApplicationHealthQuery;
import io.resys.hdes.app.service.spi.builders.GenericApplicationModelQuery;
import io.resys.hdes.app.service.spi.builders.GenericApplicationSaveBuilder;

public class GenericApplicationService implements ApplicationService {
  private final State state;
  
  public GenericApplicationService(State state) {
    super();
    this.state = state;
  }

  @Override
  public ModelQuery query() {
    return new GenericApplicationModelQuery(state);
  }

  @Override
  public SaveBuilder save() {
    return new GenericApplicationSaveBuilder(state);
  }

  @Override
  public State state() {
    return state;
  }

  @Override
  public HealthQuery health() {
    return new GenericApplicationHealthQuery(state);
  }

  @Override
  public ExceptionBuilder exception() {
    return new GenericApplicationExceptionBuilder();
  }
  
}
