package io.resys.hdes.app.service.spi.builders;

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

import java.util.Collection;

import io.resys.hdes.app.service.api.ApplicationService;
import io.resys.hdes.app.service.api.ApplicationService.Model;
import io.resys.hdes.app.service.api.ApplicationService.State;

public class GenericApplicationModelQuery implements ApplicationService.ModelQuery {

  private final State state;
  
  public GenericApplicationModelQuery(State state) {
    super();
    this.state = state;
  }

  @Override
  public Collection<Model> get() {
    return state.getModels();
  }
}
