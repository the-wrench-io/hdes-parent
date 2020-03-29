package io.resys.hdes.datatype.tests;

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

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.resys.hdes.datatype.api.DataType.ValueType;
import io.resys.hdes.datatype.api.DataTypeExpression;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.TypeNameDependency;
import io.resys.hdes.datatype.spi.antlr.DataTypeExpressionFactory;
import io.resys.hdes.datatype.spi.antlr.dependencies.InMemoryDependency;
import io.resys.hdes.datatype.spi.antlr.dependencies.TypeNameDelegateDependency.SimpleTypeNameDependency;
import io.resys.hdes.datatype.spi.antlr.syntax.InMemorySyntaxVisitor;

public class InMemoryExpressionTest {

  @Test
  public void dates() throws IOException {
    Assertions.assertEquals(LocalDate.of(2000, 12, 31), parse("'2000-12-31'", ValueType.DATE));
    Assertions.assertEquals(LocalDateTime.of(2000, 12, 31, 23, 59, 59), parse("'2000-12-31 23:59:59'", ValueType.DATE_TIME));
  }
  
  @Test
  public void literals() throws IOException {
    Assertions.assertEquals(-10, parse("-10", ValueType.INTEGER));
    Assertions.assertEquals(-10_000, parse("-10_000", ValueType.INTEGER));
    Assertions.assertEquals(new BigDecimal("-10.5"), parse("-10.5", ValueType.DECIMAL));
    Assertions.assertEquals(true, parse("true", ValueType.BOOLEAN));
    Assertions.assertEquals(false, parse("false", ValueType.BOOLEAN));
    Assertions.assertEquals("words", parse("'words'", ValueType.STRING));
  }

  @Test
  public void literalConditions() throws IOException {
    Assertions.assertEquals(false, parse("5 between 10 and 20", ValueType.BOOLEAN));
    Assertions.assertEquals(true, parse("2 between 1 and 10", ValueType.BOOLEAN));
    
    Assertions.assertEquals(true, parse("1 between 1 and 3", ValueType.BOOLEAN));
    Assertions.assertEquals(true, parse("2 between 1 and 3", ValueType.BOOLEAN));
    Assertions.assertEquals(true, parse("3 between 1 and 3", ValueType.BOOLEAN));
    
    Assertions.assertEquals(false, parse("0 between 1 and 3", ValueType.BOOLEAN));
    Assertions.assertEquals(false, parse("4 between 1 and 3", ValueType.BOOLEAN));
    
    
    Assertions.assertEquals(true, parse("0 < 1", ValueType.BOOLEAN));
    Assertions.assertEquals(true, parse("0 <= 1", ValueType.BOOLEAN));
    Assertions.assertEquals(true, parse("1 <= 1", ValueType.BOOLEAN));
    Assertions.assertEquals(false, parse("2 <= 1", ValueType.BOOLEAN));
    Assertions.assertEquals(false, parse("2 < 1", ValueType.BOOLEAN));
    Assertions.assertEquals(true, parse("2 > 1", ValueType.BOOLEAN));
    Assertions.assertEquals(true, parse("2 >= 1", ValueType.BOOLEAN));
    Assertions.assertEquals(true, parse("2 >= 2", ValueType.BOOLEAN));
  }
  
  @Test
  public void conditionalExpression() throws IOException {
    InMemoryDependency dependencies = InMemoryDependency.builder()
    .add("x", ValueType.INTEGER, 5)
    .add("v", ValueType.INTEGER, 100)
    .add("k", ValueType.INTEGER, 1)
    .build();
    
    Assertions.assertEquals(40, parse("x >= 20 ? 30 : 40 \r\n", ValueType.INTEGER, dependencies));
    Assertions.assertEquals(100, parse("x > 20 ? 30 : v", ValueType.INTEGER, dependencies));
    Assertions.assertEquals(40, parse("x <= k ? 30 : 40", ValueType.INTEGER, dependencies));
    Assertions.assertEquals(30, parse("x < 20 ? 30 : x", ValueType.INTEGER, dependencies));
  }

  
  @Test
  public void between() throws IOException {
    InMemoryDependency dependencies = InMemoryDependency.builder()
        .add("x", ValueType.INTEGER, 15)
        .add("v", ValueType.INTEGER, 10)
        .add("k", ValueType.INTEGER, 100)
        .build();
      
    Assertions.assertEquals(true, parse("x between 10 and 20", ValueType.BOOLEAN, dependencies));
    Assertions.assertEquals(true, parse("x between v and k", ValueType.BOOLEAN, dependencies));
  }
  
  @Test
  public void sum() throws IOException {
    InMemoryDependency dependencies = InMemoryDependency.builder().build();
    
    Assertions.assertEquals(70, parse("sum(10, 20, 40)", ValueType.INTEGER, dependencies));
    Assertions.assertEquals(new BigDecimal("72.2"), parse("sum(10.8, 20.5, 40.9)", ValueType.DECIMAL, dependencies));
    Assertions.assertEquals(new BigDecimal("71.3"), parse("sum(10.8, 20.5, 40)", ValueType.DECIMAL, dependencies));
  }
  

  @Test
  public void arithmeticalExpression() throws IOException {
    InMemoryDependency dependencies = InMemoryDependency.builder()
    .add("x", ValueType.DECIMAL, new BigDecimal("40.20"))
    .add("y", ValueType.INTEGER, 42)
    .add("z", ValueType.INTEGER, 100)
    .build();
    
    Assertions.assertEquals(new BigDecimal("1729.7760"), parse("x+y/z*89*(x+5)", ValueType.DECIMAL, dependencies));
  }
  
  @Test
  public void equalityExpression() throws IOException {
    SimpleTypeNameDependency dependencies = SimpleTypeNameDependency.builder()
    .add("x", ValueType.INTEGER)
    .add("w", ValueType.INTEGER)
    .add("z", ValueType.INTEGER)
    .build();
    
    parse("x = 20", ValueType.BOOLEAN, dependencies);
    parse("z != 10", ValueType.BOOLEAN, dependencies);
    parse("z != w", ValueType.BOOLEAN, dependencies);
  }

  @Test
  public void conditionalAndOrExpression() throws IOException {
    SimpleTypeNameDependency dependencies = SimpleTypeNameDependency.builder()
    .add("x", ValueType.INTEGER)
    .add("c", ValueType.INTEGER)
    .add("z", ValueType.INTEGER)
    .add("t", ValueType.INTEGER)
    .add("y", ValueType.INTEGER)
    .build();
    
    parse("x > 6 AND z < t", ValueType.BOOLEAN, dependencies);
    parse("x = 20 and y = 10 aNd z = 100", ValueType.BOOLEAN, dependencies);
    parse("x = 20 anD y = 10 OR c > 10", ValueType.BOOLEAN, dependencies);
  }

  @Test
  public void postfixExpression() throws IOException {
    
    SimpleTypeNameDependency dependencies = SimpleTypeNameDependency.builder()
    .add("x", ValueType.DECIMAL)
    .add("z", ValueType.INTEGER)
    .build();
    
    parse("x++", ValueType.DECIMAL, dependencies);
    parse("z--", ValueType.INTEGER, dependencies);
  }

  @Test
  public void prefixExpression() throws IOException {
    SimpleTypeNameDependency dependencies = SimpleTypeNameDependency.builder()
    .add("x", ValueType.DECIMAL)
    .add("z", ValueType.INTEGER)
    .build();
    
    parse("--x", ValueType.DECIMAL, dependencies);
    parse("++z", ValueType.INTEGER, dependencies);
  }

  @Test
  public void singleSignExpression() throws IOException {
    SimpleTypeNameDependency dependencies = SimpleTypeNameDependency.builder()
    .add("x", ValueType.INTEGER)
    .add("z", ValueType.DECIMAL)
    .build();
    
    parse("-x", ValueType.INTEGER, dependencies);
    parse("+z", ValueType.DECIMAL, dependencies);
  }
  
  public Serializable parse(String value, ValueType returnType) {
    return parse(value, returnType, null);
  }
  
  public void parse(String value, ValueType returnType, TypeNameDependency typeName) {
    DataTypeExpression expression = DataTypeExpressionFactory.builder()
        .value(value)
        .target(DataTypeExpression.Target.JAVA8)
        .returnType(type -> type.name("literal").valueType(returnType).build())
        .dependency(typeName)
        .strict()
        .build();
    InMemorySyntaxVisitor
        .from(expression.getAstTree(), InMemoryDependency.builder().build())
        .visit(expression.getAstTree().getNode()).getValue();
    
  }
  
  public Serializable parse(String value, ValueType returnType, InMemoryDependency typeName) {
    DataTypeExpression expression = DataTypeExpressionFactory.builder()
        .value(value)
        .target(DataTypeExpression.Target.JAVA8)
        .returnType(type -> type.name("literal").valueType(returnType).build())
        .dependency(typeName)
        .strict()
        .build();
    return InMemorySyntaxVisitor
        .from(expression.getAstTree(), typeName)
        .visit(expression.getAstTree().getNode()).getValue();
    
  }
}
