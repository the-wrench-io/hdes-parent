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

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.datatype.api.DataType.ValueType;
import io.resys.hdes.datatype.api.DataTypeService;
import io.resys.hdes.datatype.api.DataTypeService.Expression;
import io.resys.hdes.datatype.api.DataTypeService.ExpressionBuilder;
import io.resys.hdes.datatype.api.DataTypeService.ExpressionSourceType;

public class DelegateExpressionBuilder implements DataTypeService.ExpressionBuilder {
  private final ObjectMapper objectMapper;
  private String src;
  private ValueType valueType;
  private ExpressionSourceType srcType;

  public DelegateExpressionBuilder(ObjectMapper objectMapper) {
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
  
  @Override
  public ExpressionBuilder srcType(ExpressionSourceType srcType) {
    this.srcType = srcType;
    return this;
  }

  @Override
  public Expression build() {
    final ExpressionSourceType srcType = this.srcType == null ? ExpressionSourceType.DT : this.srcType;
    final DataTypeService.ExpressionBuilder result;

    switch (srcType) {
    case GROOVY:
      result = new GroovyExpressionBuilder();      
      break;
    case JEXL:
      result = new JexlExpressionBuilder();
      break;
    default:
      result = new DTExpressionBuilder(objectMapper);
      break;
    }
    
    return result.src(src).srcType(srcType).valueType(valueType).build();
  }
}
