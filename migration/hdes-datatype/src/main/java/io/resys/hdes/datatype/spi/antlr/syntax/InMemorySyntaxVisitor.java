package io.resys.hdes.datatype.spi.antlr.syntax;

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

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Range;

import io.resys.hdes.datatype.api.DataTypeExpressionAstNode;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.AmbiguousExpression;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.AndCondition;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.Args;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.ArithmeticalExpression;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.BetweenExpression;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.Condition;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.ConditionalExpression;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.DataTypeConversion;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.DateConversion;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.DateTimeConversion;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.DependencyTree;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.EvalNode;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.Literal;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.LiteralType;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.MethodInvocation;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.MethodName;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.OrCondition;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.Primary;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.Root;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.TypeName;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.Unary;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.UnarySign;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.UnaryType;
import io.resys.hdes.datatype.api.ImmutableEvalNode;
import io.resys.hdes.datatype.spi.Assert;
import io.resys.hdes.datatype.spi.antlr.dependencies.InMemoryDependency;

public class InMemorySyntaxVisitor implements DataTypeExpressionAstNode.Visitor<EvalNode, EvalNode> {

  private final DependencyTree dependencyTree;
  private final InMemoryDependency dependency;
  
  public InMemorySyntaxVisitor(DependencyTree dependencyTree, InMemoryDependency dependency) {
    super();
    this.dependencyTree = dependencyTree;
    this.dependency = dependency;
  }

  public static InMemorySyntaxVisitor from(DependencyTree dependencyTree, InMemoryDependency dependency) {
    Assert.notNull(dependency, () -> "in memory dependency must be defined!");
    return new InMemorySyntaxVisitor(dependencyTree, dependency);
  }
  
  @Override
  public EvalNode visit(Root element) {
    // unwrap
    return visit(element.getValue());
  }
  @Override
  public EvalNode visit(Primary element) {
    // unwrap
    return visit(element.getValue());
  }
  @Override
  public EvalNode visit(AmbiguousExpression element) {
    // unwrap
    return visit(element.getValue());
  }

  @Override
  public EvalNode visit(DataTypeConversion element) {
    if(element.getLiteralType() == LiteralType.DECIMAL) {
      Function<Serializable, EvalNode> result = (value) -> ImmutableEvalNode
          .builder().pos(element.getPos())
          .source(element)
          .literalType(element.getLiteralType())
          .value(value).build();
      
      if(element.getValue().getLiteralType() == LiteralType.STRING) {
        return result.apply(new BigDecimal((String) visit(element.getValue()).getValue()));
      } else if(element.getValue().getLiteralType() == LiteralType.INTEGER) {
        return result.apply(new BigDecimal((Integer) visit(element.getValue()).getValue()));
      }
    }
    
    throw new RuntimeException("AstNode visiting error, unknown data type conversion: " + 
        element.getLiteralType() + " -> " + element.getValue().getLiteralType() + " !");
  }
  
  @Override
  public EvalNode visit(DateTimeConversion element) {
    EvalNode child = visit(element.getValue());
    LocalDateTime value = LocalDateTime.parse((String) child.getValue(), TemplateVisitor.DATE_TIME_FORMATTER);
    return ImmutableEvalNode
        .builder().pos(element.getPos())
        .source(element)
        .literalType(element.getLiteralType()).value(value).build();
  }
  
  @Override
  public EvalNode visit(DateConversion element) {
    EvalNode child = visit(element.getValue());
    LocalDate value = LocalDate.parse((String) child.getValue(), TemplateVisitor.DATE_FORMATTER);
    return ImmutableEvalNode
        .builder().pos(element.getPos())
        .source(element)
        .literalType(element.getLiteralType()).value(value).build();
  }
  
  @Override
  public EvalNode visit(Unary element) {
    
    // Boolean not expression
    if(element.getSign() == UnarySign.NOT) {
      Boolean value = (Boolean) visit(element.getValue()).getValue();
      
      return ImmutableEvalNode
          .builder().pos(element.getPos())
          .source(element)
          .literalType(element.getLiteralType()).value(!value).build();
    }
    
    if(element.getType() == UnaryType.PREFIX && element.getSign() == UnarySign.SUB) {
      Serializable src = visit(element.getValue()).getValue();
      Serializable value = null;
      
      switch (element.getLiteralType()) {
      case INTEGER:
        value = -(Integer) src;
        break;
      case DECIMAL:
        value = ((BigDecimal) src).negate();
        break;
      default: break;
      }
      
      if(value != null) {
        return ImmutableEvalNode
            .builder().pos(element.getPos())
            .source(element)
            .literalType(element.getLiteralType()).value(value).build();
      }
    }
    
    throw new RuntimeException("AstNode visiting error, unary: " + element + " not implemented!");
  }
  
  @Override
  public EvalNode visit(Literal element) {
    Serializable value;
    switch (element.getLiteralType()) {
    case BOOLEAN:
      value = Boolean.valueOf(element.getValue());
      break;
    case INTEGER:
      value = Integer.valueOf(element.getValue());
      break;
    case DECIMAL:
      value = new BigDecimal(element.getValue());
      break;
    case STRING:
      value = element.getValue();
      break;
    default:
      throw new RuntimeException("AstNode visiting error, literal: " + element + " not implemented!");
    }
    return ImmutableEvalNode.builder().source(element).pos(element.getPos()).literalType(element.getLiteralType()).value(value).build();
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public EvalNode visit(BetweenExpression element) {
    Comparable 
      value = (Comparable) visit(element.getValue()).getValue(),
      from = (Comparable) visit(element.getLeft()).getValue(), 
      to = (Comparable) visit(element.getRight()).getValue();
    
    boolean between = Range.between(from, to).contains(value);
    return ImmutableEvalNode.builder()
        .source(element)
        .pos(element.getPos())
        .literalType(element.getLiteralType())
        .value(between).build();
  }
  
  @Override
  public EvalNode visit(ConditionalExpression element) {
    boolean condition = (boolean) visit(element.getCondition()).getValue();
    return condition ? visit(element.getLeft()) : visit(element.getRight());
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public EvalNode visit(Condition element) {
    Comparable 
      left = (Comparable) visit(element.getLeft()).getValue(),
      right = (Comparable) visit(element.getRight()).getValue();

    boolean result = false;
    switch (element.getSign()) {
    case NOTEQUAL: result = left.compareTo(right) != 0; break;
    case EQUAL: result = left.compareTo(right) == 0; break;
    case LESS: result = left.compareTo(right) < 0; break;
    case LESS_THEN: result = left.compareTo(right) <= 0; break;
    case GREATER: result = left.compareTo(right) > 0; break;
    case GREATER_THEN: result = left.compareTo(right) >= 0; break;
    } 
    
    return ImmutableEvalNode.builder()
        .source(element)
        .pos(element.getPos())
        .literalType(element.getLiteralType())
        .value(result).build();
  }
  
  @Override
  public EvalNode visit(TypeName element) {
    return ImmutableEvalNode.builder()
        .source(element)
        .pos(element.getPos())
        .literalType(element.getLiteralType())
        .value(dependency.invoke(element)).build();
  }


  @Override
  public EvalNode visit(MethodInvocation element) {
    List<EvalNode> child = element.getArgs().getValues().stream()
        .map(n -> visit(n)).collect(Collectors.toList());
    return ImmutableEvalNode.builder()
        .source(element)
        .pos(element.getPos())
        .literalType(element.getLiteralType())
        .value(dependency.invoke(element, child)).build();
  }

  @Override
  public EvalNode visit(AndCondition element) {
    Boolean 
    left = (Boolean) visit(element.getLeft()).getValue(),
    right = (Boolean) visit(element.getRight()).getValue();
    
    return ImmutableEvalNode.builder()
        .source(element)
        .pos(element.getPos())
        .literalType(element.getLiteralType())
        .value(left && right).build();
  }

  @Override
  public EvalNode visit(OrCondition element) {
    Boolean 
    left = (Boolean) visit(element.getLeft()).getValue(),
    right = (Boolean) visit(element.getRight()).getValue();
    
    return ImmutableEvalNode.builder()
        .source(element)
        .pos(element.getPos())
        .literalType(element.getLiteralType())
        .value(left || right).build();
  }

  @Override
  public EvalNode visit(ArithmeticalExpression element) {
    
    Serializable value = null;
    if(element.getLiteralType() == LiteralType.INTEGER) {
      Integer 
      left = (Integer) visit(element.getLeft()).getValue(),
      right = (Integer) visit(element.getRight()).getValue();
      
      switch(element.getType()) {
      case ADD: value = left + right; break;
      case MULTIPLY: value = left * right; break;
      case SUBSTRACT: value = left - right; break;
      case DIVIDE: throw new RuntimeException("AstNode visiting error, divide must be decimal operation but is integer!");
      }
      
    } else {
      BigDecimal 
      left = (BigDecimal) visit(element.getLeft()).getValue(),
      right = (BigDecimal) visit(element.getRight()).getValue();
      
      switch(element.getType()) {
      case ADD: value = left.add(right); break;
      case MULTIPLY: value = left.multiply(right); break;
      case SUBSTRACT: value = left.subtract(right); break;
      case DIVIDE: value = left.divide(right); break;
      }
    }
    
    return ImmutableEvalNode.builder()
        .source(element)
        .pos(element.getPos())
        .literalType(element.getLiteralType())
        .value(value).build();
  }

  @Override
  public EvalNode visit(MethodName element) {
    throw new RuntimeException("AstNode visiting error, methodName can't be visited!");
  }
  
  @Override
  public EvalNode visit(Args element) {
    throw new RuntimeException("AstNode visiting error, args can't be visited!");
  }
  
  private EvalNode visit(DataTypeExpressionAstNode element) {
    return TemplateVisitor.visit(this, element);
  }
}
