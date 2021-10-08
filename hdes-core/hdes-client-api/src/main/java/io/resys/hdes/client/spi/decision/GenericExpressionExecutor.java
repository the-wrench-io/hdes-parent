package io.resys.hdes.client.spi.decision;

/*-
 * #%L
 * wrench-assets-dt
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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.client.api.ast.TypeDef.ValueType;
import io.resys.hdes.client.api.execution.DecisionResult.NodeExpressionExecutor;
import io.resys.hdes.client.api.execution.ExpressionProgram;
import io.resys.hdes.client.spi.expression.ExpressionProgramFactory;
import io.resys.hdes.client.spi.util.HdesAssert;

public class GenericExpressionExecutor implements NodeExpressionExecutor {


  private final ObjectMapper objectMapper;
  private final Map<String, ExpressionProgram> cache = new ConcurrentHashMap<>();

  public GenericExpressionExecutor(ObjectMapper objectMapper) {
    super();
    this.objectMapper = objectMapper;
  }

  @Override
  public boolean execute(String value, ValueType type, Object entity) {
    if(entity == null && (value == null || value.isEmpty())) {
      return true;
    } else if(entity == null) {
      return false;
    }
    HdesAssert.notNull(entity, () -> "Type: \"" + type + "\" expression: \"" + value + "\" entity can't be null!");
    if(value == null || value.isEmpty()) {
      return true;
    }
    ExpressionProgram expression = getExpression(value, type);
    return (boolean) expression.run(entity).getValue();
  }

  @Override
  public ExpressionProgram getExpression(String src, ValueType type) {
    if(src == null) {
      return null;
    }
    String cacheKey = src + ":" + type;
    if(cache.containsKey(cacheKey)) {
      return cache.get(cacheKey);
    }
    ExpressionProgram exp = ExpressionProgramFactory.builder().objectMapper(objectMapper).src(src).valueType(type).build();
    cache.put(cacheKey, exp);
    return exp;
  }
}
