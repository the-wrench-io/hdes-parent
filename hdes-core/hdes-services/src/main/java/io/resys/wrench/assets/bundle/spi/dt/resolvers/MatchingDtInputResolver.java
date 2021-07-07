package io.resys.wrench.assets.bundle.spi.dt.resolvers;

/*-
 * #%L
 * wrench-assets-services
 * %%
 * Copyright (C) 2016 - 2020 Copyright 2016 ReSys OÜ
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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import io.resys.wrench.assets.bundle.spi.dt.DtInputResolver;
import io.resys.wrench.assets.datatype.api.DataTypeRepository.DataType;
import io.resys.wrench.assets.dt.api.DecisionTableRepository;

public class MatchingDtInputResolver implements Serializable, DtInputResolver {

  private static final long serialVersionUID = 8818379343078413837L;

  private final Map<String, Object> variables;

  public MatchingDtInputResolver(Map<String, Object> variables) {
    this.variables = variables;
  }

  @Override
  public Object apply(DataType t) {
    String name = t.getName();

    // Flat mapping
    if(variables.containsKey(name)) {
      return variables.get(name);
    }
    return DecisionTableRepository.DecisionTableFixedValue.ALWAYS_TRUE;
  }
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static class Builder {
    private final Map<String, Object> variables = new HashMap<>();
    public Builder add(String id, Object value) {
      variables.put(id, value);
      return this;
    }
    public Builder addAll(Map<String, Object> variables) {
      this.variables.putAll(variables);
      return this;
    }
    public MatchingDtInputResolver build() {
      return new MatchingDtInputResolver(variables);
    }
  }
}
