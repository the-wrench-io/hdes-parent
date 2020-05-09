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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import io.resys.hdes.app.service.api.ApplicationService.SaveBuilder;
import io.resys.hdes.app.service.api.ApplicationService.SaveRequest;
import io.resys.hdes.app.service.api.ApplicationService.SaveResponse;
import io.resys.hdes.app.service.api.ApplicationService.State;
import io.resys.hdes.app.service.api.ApplicationService.StateCopy;

public class GenericApplicationSaveBuilder implements SaveBuilder {
  private final State state;
  private final List<SaveRequest> entries = new ArrayList<>();

  public GenericApplicationSaveBuilder(State state) {
    super();
    this.state = state;
  }

  @Override
  public SaveBuilder add(SaveRequest... entry) {
    entries.addAll(Arrays.asList(entry));
    return this;
  }

  @Override
  public Collection<SaveResponse> build() {
    StateCopy stateCopy = state.copy();
    for (SaveRequest saveRequest : entries) {
      stateCopy.add(saveRequest);
    }
    return state.save(stateCopy.build());
  }
}
