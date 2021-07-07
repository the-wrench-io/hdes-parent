package io.resys.wrench.assets.dt.spi.beans;

/*-
 * #%L
 * wrench-assets-dt
 * %%
 * Copyright (C) 2016 - 2021 Copyright 2020 ReSys OÜ
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

import io.resys.wrench.assets.datatype.api.DataTypeRepository.DataType;
import io.resys.wrench.assets.dt.api.model.DecisionTableResult.DecisionContext;

public class ImmutableDecisionContext implements DecisionContext {

  private final DataType key;
  private final Object value;
  public ImmutableDecisionContext(DataType key, Object value) {
    super();
    this.key = key;
    this.value = value;
  }
  @Override
  public DataType getKey() {
    return key;
  }
  @Override
  public Object getValue() {
    return value;
  }
}
