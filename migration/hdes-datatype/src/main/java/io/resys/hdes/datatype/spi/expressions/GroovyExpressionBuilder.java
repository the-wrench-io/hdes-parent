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

import org.apache.commons.lang3.StringUtils;

import io.resys.hdes.datatype.api.DataType.ValueType;
import io.resys.hdes.datatype.api.DataTypeService;
import io.resys.hdes.datatype.api.DataTypeService.Expression;
import io.resys.hdes.datatype.api.DataTypeService.ExpressionBuilder;
import io.resys.hdes.datatype.api.DataTypeService.ExpressionSourceType;
import io.resys.hdes.datatype.api.ImmutableExpression;
import io.resys.hdes.datatype.api.exceptions.DataTypeExpressionException;
import io.resys.hdes.datatype.spi.expressions.operations.BooleanOperation;
import io.resys.hdes.datatype.spi.expressions.operations.GroovyOperation;

public class GroovyExpressionBuilder implements DataTypeService.ExpressionBuilder {
  private static final String EXPRESSION_EMPTY = "true";
  private String src;
  private ValueType valueType;

  public GroovyExpressionBuilder() {
    super();
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
    
    try {
      return ImmutableExpression.builder()
          .operation(new GroovyOperation(src))
          .src(src)
          .type(valueType)
          .srcType(ExpressionSourceType.GROOVY)
          //.constants(constants)
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
