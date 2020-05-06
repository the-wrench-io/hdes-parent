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

import static io.resys.hdes.datatype.spi.antlr.syntax.TemplateVisitor.combine;
import static io.resys.hdes.datatype.spi.antlr.syntax.TemplateVisitor.convert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import io.resys.hdes.datatype.api.DataType;
import io.resys.hdes.datatype.api.DataType.Direction;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.AmbiguousExpression;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.AndCondition;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.Args;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.ArithmeticalExpression;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.ArithmeticalType;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.BetweenExpression;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.Condition;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.ConditionalExpression;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.DataTypeConversion;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.DateConversion;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.DateTimeConversion;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.DependencyTree;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.Literal;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.LiteralType;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.MethodInvocation;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.MethodInvocationDependency;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.MethodName;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.OrCondition;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.Primary;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.Root;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.TypeName;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.TypeNameDependency;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.Unary;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.UnarySign;
import io.resys.hdes.datatype.api.ImmutableAmbiguousExpression;
import io.resys.hdes.datatype.api.ImmutableAndCondition;
import io.resys.hdes.datatype.api.ImmutableArgs;
import io.resys.hdes.datatype.api.ImmutableArithmeticalExpression;
import io.resys.hdes.datatype.api.ImmutableBetweenExpression;
import io.resys.hdes.datatype.api.ImmutableCondition;
import io.resys.hdes.datatype.api.ImmutableConditionalExpression;
import io.resys.hdes.datatype.api.ImmutableDataTypeConversion;
import io.resys.hdes.datatype.api.ImmutableDateConversion;
import io.resys.hdes.datatype.api.ImmutableDateTimeConversion;
import io.resys.hdes.datatype.api.ImmutableMethodInvocation;
import io.resys.hdes.datatype.api.ImmutableOrCondition;
import io.resys.hdes.datatype.api.ImmutableRoot;
import io.resys.hdes.datatype.api.ImmutableTypeName;
import io.resys.hdes.datatype.api.ImmutableUnary;
import io.resys.hdes.datatype.spi.antlr.dependencies.GenericDependencyTree;


public class DependencyTreeVisitor implements DataTypeExpressionAstNode.Visitor<DataTypeExpressionAstNode, DependencyTree> {
  private final MethodInvocationDependency method;
  private final TypeNameDependency typeName;
  private final DataType returnType;
  private final List<Literal> literals = new ArrayList<>();
  private final List<DataType> dependencies = new ArrayList<>();

  public DependencyTreeVisitor(DataType returnType, MethodInvocationDependency method, TypeNameDependency typeName) {
    super();
    this.method = method;
    this.typeName = typeName;
    this.returnType = returnType;
  }

  public static DependencyTreeVisitor from(DataType returnType, MethodInvocationDependency method, TypeNameDependency typeName) {
    return new DependencyTreeVisitor(returnType, method, typeName);
  }

  @Override
  public DataTypeExpressionAstNode visit(Primary element) {
    // unwrap
    return visit(element.getValue());
  }

  @Override
  public Literal visit(Literal element) {
    literals.add(element);
    // nothing to do
    return element;
  }

  public DataTypeExpressionAstNode visit(LiteralType type, DataTypeExpressionAstNode from) {
    // no type conversion
    if (type == from.getLiteralType()) {
      return from;
    }
    if (type == LiteralType.DATE && from.getLiteralType() == LiteralType.STRING) {
      return ImmutableDateConversion.builder().from(from).literalType(type).value(from).build();
    } else if (type == LiteralType.DATE_TIME && from.getLiteralType() == LiteralType.STRING) {
      return ImmutableDateTimeConversion.builder().from(from).literalType(type).value(from).build();
    }
    
    if(type == LiteralType.DECIMAL && from.getLiteralType() == LiteralType.INTEGER) {
      return ImmutableDataTypeConversion.builder().from(from).literalType(type).value(from).build();
    }
    
    return from;
  }
  
  @Override
  public BetweenExpression visit(BetweenExpression element) {
    
    DataTypeExpressionAstNode value = visit(element.getValue());
    DataTypeExpressionAstNode left = visit(element.getRight());
    DataTypeExpressionAstNode right = visit(element.getLeft());
    
    left = visit(value.getLiteralType(), left);
    right = visit(value.getLiteralType(), right);
    
    return ImmutableBetweenExpression.builder()
        .literalType(LiteralType.BOOLEAN)
        .from(element)
        .value(value)
        .left(left)
        .right(right)
        .build();
  }

  @Override
  public AmbiguousExpression visit(AmbiguousExpression element) {
    DataTypeExpressionAstNode value = visit(element.getValue());
    return ImmutableAmbiguousExpression.builder()
        .from(element)
        .literalType(value.getLiteralType())
        .value(value).build();
  }

  @Override
  public DependencyTree visit(Root element) {
    DataTypeExpressionAstNode child = visit(element.getValue());
    LiteralType literalType = convert(returnType.getValueType());
    Root result = ImmutableRoot.builder().from(element)
        .value(visit(literalType, child)).literalType(literalType).build();
    return new GenericDependencyTree(result, dependencies, literals);
  }

  @Override
  public ConditionalExpression visit(ConditionalExpression element) {
    DataTypeExpressionAstNode left = visit(element.getLeft());
    return ImmutableConditionalExpression.builder()
        .from(element)
        .left(left)
        .right(visit(element.getRight()))
        .condition(visit(element.getCondition()))
        .literalType(left.getLiteralType())
        .build();
  }

  @Override
  public MethodInvocation visit(MethodInvocation element) {
    DataType dataType = this.method.apply(element);
    dependencies.add(dataType);
    List<DataType> output = dataType == null ? Collections.emptyList() : dataType.getProperties().stream().filter(d -> d.getDirection() == Direction.OUT).collect(Collectors.toList());
    if (output.isEmpty() || output.size() > 1) {
      return ImmutableMethodInvocation.builder().from(element)
          .args(visit(element.getArgs()))
          .dependency(dataType)
          .literalType(LiteralType.UNKNOWN).build();
    }
    LiteralType literalType = convert(output.get(0).getValueType());
    Args args = visit(element.getArgs());
    if (literalType == LiteralType.NUMERIC) {
      
      for (DataTypeExpressionAstNode arg : args.getValues()) {
        if (arg.getLiteralType() == LiteralType.INTEGER) {
          literalType = LiteralType.INTEGER;
        } else if (arg.getLiteralType() == LiteralType.DECIMAL) {
          literalType = LiteralType.DECIMAL;
          break;
        }
      }
      
      final LiteralType conversionType = literalType;
      args = ImmutableArgs.builder().from(args)
          .values(args.getValues().stream()
              .map(a -> visit(conversionType, a))
              .collect(Collectors.toList())).build();
      
      
    }
    return ImmutableMethodInvocation.builder()
        .from(element)
        .args(args)
        .dependency(dataType)
        .literalType(literalType).build();
  }

  @Override
  public Condition visit(Condition element) {
    DataTypeExpressionAstNode left = visit(element.getLeft());
    DataTypeExpressionAstNode right = visit(element.getRight());
    LiteralType literalType = combine(left.getLiteralType(), right.getLiteralType());
    return ImmutableCondition.builder().from(element)
        .literalType(LiteralType.BOOLEAN)
        .left(visit(literalType, left))
        .right(visit(literalType, right))
        .build();
  }

  @Override
  public TypeName visit(TypeName element) {
    DataType dataType = this.typeName.apply(element);
    LiteralType literal = dataType == null ? LiteralType.UNKNOWN : convert(dataType.getValueType());
    return ImmutableTypeName.builder()
        .from(element)
        .literalType(literal)
        .dependency(dataType)
        .build();
  }

  @Override
  public Unary visit(Unary element) {
    DataTypeExpressionAstNode child = visit(element.getValue());
    return ImmutableUnary.builder()
        .from(element)
        .literalType(element.getSign() == UnarySign.NOT ? LiteralType.BOOLEAN : child.getLiteralType())
        .value(child)
        .build();
  }

  @Override
  public AndCondition visit(AndCondition element) {
    DataTypeExpressionAstNode left = visit(element.getLeft());
    DataTypeExpressionAstNode right = visit(element.getRight());
    return ImmutableAndCondition.builder()
        .from(element)
        .left(left)
        .right(right)
        .literalType(LiteralType.BOOLEAN)
        .build();
  }

  @Override
  public OrCondition visit(OrCondition element) {
    DataTypeExpressionAstNode left = visit(element.getLeft());
    DataTypeExpressionAstNode right = visit(element.getRight());
    return ImmutableOrCondition.builder()
        .from(element)
        .left(left)
        .right(right)
        .literalType(LiteralType.BOOLEAN)
        .build();
  }

  @Override
  public ArithmeticalExpression visit(ArithmeticalExpression element) {
    DataTypeExpressionAstNode left = visit(element.getLeft());
    DataTypeExpressionAstNode right = visit(element.getRight());
    
    LiteralType resultLiteralType = element.getType() == ArithmeticalType.DIVIDE ? 
        LiteralType.DECIMAL :
        combine(left.getLiteralType(), right.getLiteralType());
    
    
    return ImmutableArithmeticalExpression.builder()
        .from(element)
        .left(visit(resultLiteralType, left))
        .right(visit(resultLiteralType, right))
        .literalType(resultLiteralType)
        .build();
  }

  @Override
  public Args visit(Args element) {
    return ImmutableArgs.builder()
        .from(element)
        .values(element.getValues() == null ? Collections.emptyList() : element.getValues().stream().map(a -> visit(a)).collect(Collectors.toList()))
        .literalType(LiteralType.UNKNOWN)
        .build();
  }

  @Override
  public DataTypeConversion visit(DataTypeConversion element) {
    return element;
  }
  
  @Override
  public DataTypeExpressionAstNode visit(DateConversion element) {
    return element;
  }

  @Override
  public DataTypeExpressionAstNode visit(DateTimeConversion element) {
    return element;
  }

  @Override
  public DataTypeExpressionAstNode visit(MethodName element) {
    throw new RuntimeException("AstNode visiting error, methodName can't be visited directly, every arg should be validated on parent level!");
  }

  private DataTypeExpressionAstNode visit(DataTypeExpressionAstNode element) {
    return TemplateVisitor.visit(this, element);
  }
}
