package io.resys.hdes.datatype.spi.expressions;

/*-
 * #%L
 * hdes-datatype
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
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.datatype.api.DataType.ValueType;
import io.resys.hdes.datatype.api.DataTypeService;
import io.resys.hdes.datatype.api.DataTypeService.Expression;
import io.resys.hdes.datatype.api.DataTypeService.ExpressionBuilder;
import io.resys.hdes.datatype.api.DataTypeService.ExpressionSourceType;
import io.resys.hdes.datatype.api.DataTypeService.Operation;
import io.resys.hdes.datatype.api.ImmutableExpression;
import io.resys.hdes.datatype.api.exceptions.DataTypeExpressionException;
import io.resys.hdes.datatype.spi.Assert;
import io.resys.hdes.datatype.spi.expressions.operations.BooleanOperation;
import io.resys.hdes.datatype.spi.expressions.operations.DateOperation;
import io.resys.hdes.datatype.spi.expressions.operations.NumberOperation;
import io.resys.hdes.datatype.spi.expressions.operations.StringOperation;

public class DTExpressionBuilder implements DataTypeService.ExpressionBuilder {
  private static final String EXPRESSION_EMPTY = "";
  private final ObjectMapper objectMapper;
  private String src;
  private ValueType valueType;

  public DTExpressionBuilder(ObjectMapper objectMapper) {
    super();
    this.objectMapper = objectMapper;
  }

  @Override
  public ExpressionBuilder src(String src) {
    this.src = src;
    return this;
  }

  @Override
  public ExpressionBuilder valueType(ValueType valueType) {
    this.valueType = valueType;
    return this;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public Expression build() {
    if(StringUtils.isEmpty(src)) {
      return ImmutableExpression.builder()
          .operation(BooleanOperation.builder().build())
          .src(EXPRESSION_EMPTY)
          .type(valueType)
          .srcType(ExpressionSourceType.DT)
          //.constants(constants)
          .build();
    }
    
    Assert.notNull(valueType, () -> "valueType can't be null!");
    try {
      final List<String> constants = new ArrayList<>();
      final Consumer<String> constantsConsumer = (String value) -> {
        if (!StringUtils.isEmpty(value)) {
          constants.add(value);
        }
      };
      Operation operation = null;
      switch (valueType) {
      case STRING:
        operation = StringOperation.builder(objectMapper).build(src, constantsConsumer);
        break;
      case BOOLEAN:
        operation = BooleanOperation.builder().build(src, constantsConsumer);
        break;
      case INTEGER:
      case LONG:
      case DECIMAL:
        operation = NumberOperation.builder().build(src, valueType, constantsConsumer);
        break;
      case DATE:
      case DATE_TIME:
        operation = DateOperation.builder().build(src, valueType, constantsConsumer);
        break;
      default:
        throw DataTypeExpressionException.builder().valueType(valueType).src(src).msg("Unknown value type").build();
      }
      return ImmutableExpression.builder()
          .operation(operation)
          .src(src)
          .type(valueType)
          .srcType(ExpressionSourceType.DT)
          .constants(constants)
          .build();
    } catch(DataTypeExpressionException t) {
      throw t;
    } catch (Exception e) {
      throw DataTypeExpressionException.builder().valueType(valueType).src(src).original(e).build();
    }
  }

  @Override
  public ExpressionBuilder srcType(ExpressionSourceType srcType) {
    return this;
  }
}
