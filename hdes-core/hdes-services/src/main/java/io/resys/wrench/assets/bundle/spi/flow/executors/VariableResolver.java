package io.resys.wrench.assets.bundle.spi.flow.executors;

/*-
 * #%L
 * wrench-component-assets
 * %%
 * Copyright (C) 2016 - 2018 Copyright 2016 ReSys OÜ
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
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class VariableResolver {
  
  public VariableResolver(ObjectMapper objectMapper) {
    super();
    this.objectMapper = objectMapper;
  }

  private final ObjectMapper objectMapper;

  public Serializable getVariableOnPath(String name, Map<String, Serializable> tasks) {
    String[] path = name.split("\\.");
    if(path.length > 0) {
      return getVariableOnPath(path, tasks);
    }
    return null;
  }

  private Serializable getVariableOnPath(String[] paths, Object src) {
    Object target = src;
    StringBuilder fullName = new StringBuilder();
    for(String path : paths) {
      if(fullName.length() > 0) {
        fullName.append(".");
      }
      fullName.append(path);

      target = getVariable(fullName.toString(), path, target);
      if(target == null) {
        return (Serializable) target;
      }
    }
    return (Serializable) target;
  }

  @SuppressWarnings("rawtypes")
  private Object getVariable(String fullName, String name, Object src) {
    if(src instanceof Map) {
      return ((Map) src).get(name);
    }
    try {
      return objectMapper.convertValue(src, Map.class).get(name);
    } catch(Exception e) {
      throw new IllegalArgumentException("Don't know how to convert variable: " + fullName + " of type: " + src.getClass().getCanonicalName() + "!");
    }
  }
}
