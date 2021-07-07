package io.resys.wrench.assets.dt.spi.expression;

import java.math.BigDecimal;
import java.util.Arrays;

/*-
 * #%L
 * wrench-component-dt
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;

import io.resys.wrench.assets.datatype.api.DataTypeRepository.ValueType;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.DynamicValueExpressionExecutor;

public class SpringDynamicValueExpressionExecutor implements DynamicValueExpressionExecutor {

  private final static ExpressionParser PARSER = new SpelExpressionParser();
  private final Map<String, Expression> cache = new HashMap<>();
  private final List<String> validExpressions = Arrays.asList("<=", "<",">=", ">", "=");

  @Override
  public Object parseVariable(String expression, ValueType type) {
    Optional<String> comparison = validExpressions.stream().filter(v -> expression.startsWith(v)).findFirst();
    if(!comparison.isPresent()) {
      switch(type) {
      case DECIMAL:
        return BigDecimal.ZERO;
      case LONG:
        return 0;
      case INTEGER:
        return 0;
      default: return null;
      }
    }
    String value = expression.substring(comparison.get().length()).trim();
    switch(type) {
    case DECIMAL:
      return new BigDecimal(value);
    case LONG:
      return Long.parseLong(value);
    case INTEGER:
      return Integer.parseInt(value);
    default: return null;
    }
  }

  @Override
  public String execute(String expression, Map<String, Object> contextExpression) {
    Assert.notNull(expression, "expression can't be null!");
    Assert.notNull(contextExpression, "context can't be null!");

    StandardEvaluationContext context = createContext(contextExpression);
    Expression exp = getExpression(expression);
    return exp.getValue(context) + "";
  }

  protected StandardEvaluationContext createContext(Map<String, Object> contextExpression) {
    StandardEvaluationContext context = new StandardEvaluationContext();
    context.setVariables(contextExpression);
    context.addPropertyAccessor(new PropertyAccessor() {

      @Override
      public void write(EvaluationContext context, Object target, String name, Object newValue) throws AccessException {
        // TODO Auto-generated method stub

      }

      @Override
      public TypedValue read(EvaluationContext context, Object target, String name) throws AccessException {
        return new TypedValue(contextExpression.get(name));
      }

      @Override
      public Class<?>[] getSpecificTargetClasses() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public boolean canWrite(EvaluationContext context, Object target, String name) throws AccessException {
        // TODO Auto-generated method stub
        return false;
      }

      @Override
      public boolean canRead(EvaluationContext context, Object target, String name) throws AccessException {
        return contextExpression.containsKey(name);
      }
    });

    //contextExpression.forEach((key, value) -> context.setVariable(key, value));
    return context;
  }

  protected Expression getExpression(String value) {
    if(cache.containsKey(value)) {
      return cache.get(value);
    }
    Expression exp = PARSER.parseExpression(value);
    cache.put(value, exp);
    return exp;
  }
}
