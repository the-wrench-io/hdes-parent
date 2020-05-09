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

import java.util.Arrays;

import io.resys.hdes.app.service.api.ApplicationService.Health;
import io.resys.hdes.app.service.api.ApplicationService.HealthQuery;
import io.resys.hdes.app.service.api.ApplicationService.State;
import io.resys.hdes.app.service.api.ImmutableHealth;
import io.resys.hdes.app.service.api.ImmutableHealthValue;

public class GenericApplicationHealthQuery implements HealthQuery {
  private final State state;

  public GenericApplicationHealthQuery(State state) {
    super();
    this.state = state;
  }

  @Override
  public Health get() {
    return ImmutableHealth.builder().status("OK").values(Arrays.asList(
        ImmutableHealthValue.builder()
          .id("models")
          .value(String.valueOf(state.getModels().size()))
          .build())
        ).build();
  }
}
