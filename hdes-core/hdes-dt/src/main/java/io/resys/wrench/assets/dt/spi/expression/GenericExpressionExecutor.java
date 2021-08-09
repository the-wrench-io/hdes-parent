package io.resys.wrench.assets.dt.spi.expression;

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
import java.util.function.Supplier;

import io.resys.wrench.assets.datatype.api.DataTypeRepository.ValueType;
import io.resys.wrench.assets.datatype.spi.util.Assert;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.DecisionTableExpression;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.DecisionTableExpressionBuilder;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.NodeExpressionExecutor;

public class GenericExpressionExecutor implements NodeExpressionExecutor {

  private final Supplier<DecisionTableExpressionBuilder> expressionBuilder;
  private final Map<String, DecisionTableExpression> cache = new ConcurrentHashMap<>();

  public GenericExpressionExecutor(Supplier<DecisionTableExpressionBuilder> expressionBuilder) {
    super();
    this.expressionBuilder = expressionBuilder;
  }

  @Override
  public boolean execute(String value, ValueType type, Object entity) {
    if(entity == null && (value == null || value.isEmpty())) {
      return true;
    } else if(entity == null) {
      return false;
    }
    Assert.notNull(entity, () -> "Type: \"" + type + "\" expression: \"" + value + "\" entity can't be null!");
    if(value == null || value.isEmpty()) {
      return true;
    }
    DecisionTableExpression expression = getExpression(value, type);
    return (boolean) expression.getValue(entity);
  }

  @Override
  public DecisionTableExpression getExpression(String src, ValueType type) {
    if(src == null) {
      return null;
    }
    String cacheKey = src + ":" + type;
    if(cache.containsKey(cacheKey)) {
      return cache.get(cacheKey);
    }
    DecisionTableExpression exp = expressionBuilder.get().src(src).valueType(type).build();
    cache.put(cacheKey, exp);
    return exp;
  }
}
