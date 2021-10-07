package io.resys.wrench.assets.dt.spi;

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
import java.util.function.Supplier;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import io.resys.hdes.client.api.ast.AstDataType.ValueType;
import io.resys.hdes.client.api.execution.DecisionTableResult.Expression;
import io.resys.hdes.client.spi.decision.execution.OperationFactory;
import io.resys.wrench.assets.dt.spi.config.TestDtConfig;

@RunWith(BlockJUnit4ClassRunner.class)
public class ExpressionTest {

  private Supplier<OperationFactory.Builder> expressionBuilder = () -> (OperationFactory.builder().objectMapper(TestDtConfig.objectMapper()));

  @Test
  public void stringIn() throws IOException {
    Expression expression = build(ValueType.STRING, "in[\"aaa\", \"bbb\"]");
    Assert.assertEquals(true, expression.getValue("aaa"));
    Assert.assertEquals(true, expression.getValue("bbb"));
    Assert.assertEquals(false, expression.getValue("c"));
  }

  @Test
  public void stringNotIn() throws IOException {
    Expression expression = build(ValueType.STRING, "not in[\"sdsd\", \"dsdx\"]");

    Assert.assertEquals(true, expression.getValue("a"));
    Assert.assertEquals(true, expression.getValue("c"));
    Assert.assertEquals(false, expression.getValue("dsdx"));
  }

  @Test
  public void toNumberComparison() throws IOException {
    Expression expression = build(ValueType.INTEGER, "<= 10");

    Assert.assertEquals(true, expression.getValue(1));
    Assert.assertEquals(false, expression.getValue(11));
  }

  @Test
  public void toNumberRange() throws IOException {
    Expression expression = build(ValueType.INTEGER, "[1..3]");

    // include start and end
    Assert.assertEquals(true, expression.getValue(1));
    Assert.assertEquals(true, expression.getValue(2));
    Assert.assertEquals(true, expression.getValue(3));
    Assert.assertEquals(false, expression.getValue(4));

    // exclude start and end
    expression = build(ValueType.INTEGER, "(1..3)");
    Assert.assertEquals(false, expression.getValue(1));
    Assert.assertEquals(true, expression.getValue(2));
    Assert.assertEquals(false, expression.getValue(3));
    Assert.assertEquals(false, expression.getValue(4));

    // exclude start and include end
    expression = build(ValueType.INTEGER, "(1..3]");
    Assert.assertEquals(false, expression.getValue(1));
    Assert.assertEquals(true, expression.getValue(2));
    Assert.assertEquals(true, expression.getValue(3));
    Assert.assertEquals(false, expression.getValue(4));

    // include start and exclude end
    expression = build(ValueType.INTEGER, "[1..3)");
    Assert.assertEquals(true, expression.getValue(1));
    Assert.assertEquals(true, expression.getValue(2));
    Assert.assertEquals(false, expression.getValue(3));
    Assert.assertEquals(false, expression.getValue(4));
  }

  @Test
  public void toBoolean() throws IOException {
    Expression expression = build(ValueType.BOOLEAN, "true");

    Assert.assertEquals(true, expression.getValue(true));
    Assert.assertEquals(false, expression.getValue(false));

    expression = build(ValueType.BOOLEAN, "false");
    Assert.assertEquals(true, expression.getValue(false));

  }

  @Test
  public void toDate() throws IOException {
    // equals
    Expression expression = build(ValueType.DATE_TIME, "equals 2017-07-03T00:00:00Z");
    Assert.assertEquals(true, expression.getValue(ValueBuilder.parseLocalDateTime("2017-07-03T00:00:00Z")));

    // after
    expression = build(ValueType.DATE_TIME, "after 2017-07-03T00:00:00Z");
    Assert.assertEquals(false, expression.getValue(ValueBuilder.parseLocalDateTime("2017-07-03T00:00:00Z")));

    Assert.assertEquals(true, expression.getValue(ValueBuilder.parseLocalDateTime("2017-07-03T00:00:01Z")));

    // before
    expression = build(ValueType.DATE_TIME, "before 2017-07-03T00:00:00Z");
    Assert.assertEquals(false, expression.getValue(ValueBuilder.parseLocalDateTime("2017-07-03T00:00:00Z")));
    Assert.assertEquals(true, expression.getValue(ValueBuilder.parseLocalDateTime("2017-07-02T23:59:59Z")));


    // before between
    expression = build(ValueType.DATE_TIME, "between 2017-07-03T00:00:01Z and 2017-07-03T00:00:03Z");
    Assert.assertEquals(false, expression.getValue(ValueBuilder.parseLocalDateTime("2017-07-03T00:00:00Z")));
    Assert.assertEquals(true, expression.getValue(ValueBuilder.parseLocalDateTime("2017-07-03T00:00:01Z")));
    Assert.assertEquals(true, expression.getValue(ValueBuilder.parseLocalDateTime("2017-07-03T00:00:02Z")));
    Assert.assertEquals(true, expression.getValue(ValueBuilder.parseLocalDateTime("2017-07-03T00:00:03Z")));
    Assert.assertEquals(false, expression.getValue(ValueBuilder.parseLocalDateTime("2017-07-03T00:00:04Z")));
  }

  public Expression build(ValueType type, String src) {
    
    return new LoggingDecisionTableExpression(expressionBuilder.get().src(src).valueType(type).build());
  }
  
  public static class LoggingDecisionTableExpression implements Expression {
    private final Expression delegate;
    public LoggingDecisionTableExpression(Expression delegate) {
      this.delegate = delegate;
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
    public Object getValue(Object entity) {
      long start = System.nanoTime();
      Object result = delegate.getValue(entity);
      System.out.println(delegate.getSrc() + " execution in nano: " + (System.nanoTime() - start));
      
      return result;
    }
  }
}
