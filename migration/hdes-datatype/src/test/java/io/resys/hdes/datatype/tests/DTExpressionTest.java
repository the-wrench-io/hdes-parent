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
import java.util.List;

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
public class DTExpressionTest {
  private final ExpressionFactory expressionFactory = new GenericExpressionFactory(new ObjectMapper());
  
  @Test
  public void stringIn() throws IOException {
    DataTypeService.Expression expression = build(ValueType.STRING, "in[\"aaa\", \"bbb\"]");
    Assertions.assertEquals(true, expression.getOperation().apply("aaa"));
    Assertions.assertEquals(true, expression.getOperation().apply("bbb"));
    Assertions.assertEquals(false, expression.getOperation().apply("c"));
  }

  @Test
  public void stringNotIn() throws IOException {
    DataTypeService.Expression expression = build(ValueType.STRING, "not in[\"sdsd\", \"dsdx\"]");

    Assertions.assertEquals(true, expression.getOperation().apply("a"));
    Assertions.assertEquals(true, expression.getOperation().apply("c"));
    Assertions.assertEquals(false, expression.getOperation().apply("dsdx"));
  }

  @Test
  public void toNumberComparison() throws IOException {
    DataTypeService.Expression expression = build(ValueType.INTEGER, "<= 10");

    Assertions.assertEquals(true, expression.getOperation().apply(1));
    Assertions.assertEquals(false, expression.getOperation().apply(11));
  }

  @Test
  public void toNumberRange() throws IOException {
    DataTypeService.Expression expression = build(ValueType.INTEGER, "[1..3]");

    // include start and end
    Assertions.assertEquals(true, expression.getOperation().apply(1));
    Assertions.assertEquals(true, expression.getOperation().apply(2));
    Assertions.assertEquals(true, expression.getOperation().apply(3));
    Assertions.assertEquals(false, expression.getOperation().apply(4));

    // exclude start and end
    expression = build(ValueType.INTEGER, "(1..3)");
    Assertions.assertEquals(false, expression.getOperation().apply(1));
    Assertions.assertEquals(true, expression.getOperation().apply(2));
    Assertions.assertEquals(false, expression.getOperation().apply(3));
    Assertions.assertEquals(false, expression.getOperation().apply(4));

    // exclude start and include end
    expression = build(ValueType.INTEGER, "(1..3]");
    Assertions.assertEquals(false, expression.getOperation().apply(1));
    Assertions.assertEquals(true, expression.getOperation().apply(2));
    Assertions.assertEquals(true, expression.getOperation().apply(3));
    Assertions.assertEquals(false, expression.getOperation().apply(4));

    // include start and exclude end
    expression = build(ValueType.INTEGER, "[1..3)");
    Assertions.assertEquals(true, expression.getOperation().apply(1));
    Assertions.assertEquals(true, expression.getOperation().apply(2));
    Assertions.assertEquals(false, expression.getOperation().apply(3));
    Assertions.assertEquals(false, expression.getOperation().apply(4));
  }

  @Test
  public void toBoolean() throws IOException {
    DataTypeService.Expression expression = build(ValueType.BOOLEAN, "true");

    Assertions.assertEquals(true, expression.getOperation().apply(true));
    Assertions.assertEquals(false, expression.getOperation().apply(false));

    expression = build(ValueType.BOOLEAN, "false");
    Assertions.assertEquals(true, expression.getOperation().apply(false));

  }

  @Test
  public void toDate() throws IOException {
    // equals
    DataTypeService.Expression expression = build(ValueType.DATE_TIME, "equals 2017-07-03T00:00:00Z");
    Assertions.assertEquals(true, expression.getOperation().apply(ValueBuilder.parseLocalDateTime("2017-07-03T00:00:00Z")));

    // after
    expression = build(ValueType.DATE_TIME, "after 2017-07-03T00:00:00Z");
    Assertions.assertEquals(false, expression.getOperation().apply(ValueBuilder.parseLocalDateTime("2017-07-03T00:00:00Z")));

    Assertions.assertEquals(true, expression.getOperation().apply(ValueBuilder.parseLocalDateTime("2017-07-03T00:00:01Z")));

    // before
    expression = build(ValueType.DATE_TIME, "before 2017-07-03T00:00:00Z");
    Assertions.assertEquals(false, expression.getOperation().apply(ValueBuilder.parseLocalDateTime("2017-07-03T00:00:00Z")));
    Assertions.assertEquals(true, expression.getOperation().apply(ValueBuilder.parseLocalDateTime("2017-07-02T23:59:59Z")));


    // before between
    expression = build(ValueType.DATE_TIME, "between 2017-07-03T00:00:01Z and 2017-07-03T00:00:03Z");
    Assertions.assertEquals(false, expression.getOperation().apply(ValueBuilder.parseLocalDateTime("2017-07-03T00:00:00Z")));
    Assertions.assertEquals(true, expression.getOperation().apply(ValueBuilder.parseLocalDateTime("2017-07-03T00:00:01Z")));
    Assertions.assertEquals(true, expression.getOperation().apply(ValueBuilder.parseLocalDateTime("2017-07-03T00:00:02Z")));
    Assertions.assertEquals(true, expression.getOperation().apply(ValueBuilder.parseLocalDateTime("2017-07-03T00:00:03Z")));
    Assertions.assertEquals(false, expression.getOperation().apply(ValueBuilder.parseLocalDateTime("2017-07-03T00:00:04Z")));
  }

  public DataTypeService.Expression build(ValueType type, String src) {
    return new LogginOperation(expressionFactory.builder().src(src).valueType(type).build());
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
