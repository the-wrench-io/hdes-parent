package io.resys.wrench.assets.dt.spi.expression;

/*-
 * #%L
 * wrench-assets-dt
 * %%
 * Copyright (C) 2016 - 2019 Copyright 2016 ReSys OÜ
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
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.client.api.ast.AstType.ValueType;
import io.resys.hdes.client.api.execution.DecisionTableResult.DecisionTableExpression;
import io.resys.hdes.client.spi.util.Assert;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.DecisionTableExpressionBuilder;
import io.resys.wrench.assets.dt.spi.beans.ImmutableDecisionTableExpression;
import io.resys.wrench.assets.dt.spi.exceptions.DecisionTableException;

public class GenericDecisionTableExpressionBuilder implements DecisionTableExpressionBuilder {

  private final ObjectMapper objectMapper;
  private String src;
  private ValueType valueType;

  public GenericDecisionTableExpressionBuilder(ObjectMapper objectMapper) {
    super();
    this.objectMapper = objectMapper;
  }
  @Override
  public GenericDecisionTableExpressionBuilder src(String src) {
    this.src = src;
    return this;
  }
  @Override
  public GenericDecisionTableExpressionBuilder valueType(ValueType valueType) {
    this.valueType = valueType;
    return this;
  }
  @Override
  public DecisionTableExpression build() {
    Assert.notNull(src, () -> "src can't be null!");
    Assert.notNull(valueType, () -> "valueType can't be null!");

    try {
      final List<String> constants = new ArrayList<>();
      final Consumer<String> constantsConsumer = (String value) -> {
        if(!StringUtils.isEmpty(value)) {
          constants.add(value);
        }
      };
      
      Operation operation = null;
      switch (valueType) {
      case STRING:
        operation = StringOperation.builder(objectMapper).build(src, constantsConsumer);
        break;
      case BOOLEAN:
        operation = BooleanOperation.builder(objectMapper).build(src, constantsConsumer);
        break;
      case INTEGER:
      case LONG:
      case DECIMAL:
        operation = NumberOperation.builder(objectMapper).build(src, valueType, constantsConsumer);
        break;
      case DATE:
      case DATE_TIME:
        operation = DateOperation.builder(objectMapper).build(src, valueType, constantsConsumer);
        break;
      default:
        throw new DecisionTableException("Unknown type: " + valueType + "!");
      }

      return new ImmutableDecisionTableExpression(
          operation,
          src, valueType, Collections.unmodifiableList(constants));
    } catch(Exception e) {
      throw new DecisionTableException(e.getMessage(), e);
    }
  }
}
