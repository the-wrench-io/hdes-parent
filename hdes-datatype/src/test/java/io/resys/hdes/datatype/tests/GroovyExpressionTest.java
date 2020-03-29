package io.resys.hdes.datatype.tests;

/*-
 * #%L
 * wrench-component-assets-Dt
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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.datatype.api.DataType.ValueType;
import io.resys.hdes.datatype.api.DataTypeService;
import io.resys.hdes.datatype.api.DataTypeService.ExpressionSourceType;
import io.resys.hdes.datatype.api.DataTypeService.Operation;
import io.resys.hdes.datatype.spi.expressions.ExpressionFactory;
import io.resys.hdes.datatype.spi.expressions.GenericExpressionFactory;


@SuppressWarnings("unchecked")
public class GroovyExpressionTest {
  private final ExpressionFactory expressionFactory = new GenericExpressionFactory(new ObjectMapper());
  
  @Test
  public void stringIn() throws IOException {
    Map<String, Object> tasks = new HashMap<>();
    tasks.put("task1", 0);
    tasks.put("task2", 3);
    
    Assertions.assertEquals(true, build("tasks.task1 == 0").getOperation().apply(tasks));
    Assertions.assertEquals(false, build("tasks.task1 == 1").getOperation().apply(tasks));
    Assertions.assertEquals(true, build("tasks.task2 == 3").getOperation().apply(tasks));
  }

  public DataTypeService.Expression build(String src) {
    return new LogginOperation(expressionFactory.builder().src(src).srcType(ExpressionSourceType.GROOVY).valueType(ValueType.OBJECT).build());
  }

  @SuppressWarnings("rawtypes")
  public static class LogginOperation implements Operation, DataTypeService.Expression {
    private final DataTypeService.Expression delegate;
    public LogginOperation(DataTypeService.Expression delegate) {
      this.delegate = delegate;
    }

    @Override
    public Object apply(Object entity) {
      long start = System.nanoTime();
      Object result = delegate.getOperation().apply(entity);
      System.out.println(delegate.getSrc() + " execution in nano: " + (System.nanoTime() - start));
      return result;
    }

    @Override
    public String getSrc() {
      return delegate.getSrc();
    }

    @Override
    public ValueType getType() {
      return delegate.getType();
    }

    @Override
    public List<String> getConstants() {
      return delegate.getConstants();
    }

    @Override
    public Operation getOperation() {
      return this;
    }

    @Override
    public ExpressionSourceType getSrcType() {
      return delegate.getSrcType();
    }
  }
}
