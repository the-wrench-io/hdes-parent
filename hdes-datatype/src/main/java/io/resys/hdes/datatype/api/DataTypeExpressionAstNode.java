package io.resys.hdes.datatype.api;

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
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.immutables.value.Value;

public interface DataTypeExpressionAstNode {

  Position getPos();
 
  @Nullable
  LiteralType getLiteralType();
  
  @Value.Immutable
  interface ErrorNode {
    DataTypeExpressionAstNode getTarget();
    String getMessage();
  }
  
  @Value.Immutable
  interface Position {
    String getText();
    PositionToken getStart();
    
    @Nullable
    PositionToken getStop();
  }

  @Value.Immutable
  interface PositionToken {
    int getLine();
    int getCol();
  }
  
  @Value.Immutable
  interface CompilationUnit {
    List<MethodInvocation> getMethods();
    String getValue();
  } 
  
  @Value.Immutable  
  interface EvalNode extends DataTypeExpressionAstNode {
    DataTypeExpressionAstNode getSource();
    Serializable getValue();
  }

  @Value.Immutable
  interface EmptyNode extends DataTypeExpressionAstNode {
    
  }
  
  @Value.Immutable
  interface BetweenExpression extends DataTypeExpressionAstNode {
    DataTypeExpressionAstNode getValue();
    DataTypeExpressionAstNode getLeft();
    DataTypeExpressionAstNode getRight();
  }
  
  @Value.Immutable
  interface DataTypeConversion extends DataTypeExpressionAstNode {
    DataTypeExpressionAstNode getValue();
  }
  
  @Value.Immutable
  interface DateConversion extends DataTypeExpressionAstNode {
    DataTypeExpressionAstNode getValue();
  }

  @Value.Immutable
  interface DateTimeConversion extends DataTypeExpressionAstNode {
    DataTypeExpressionAstNode getValue();
  }
  
  @Value.Immutable
  interface Root extends DataTypeExpressionAstNode {
    @Nullable
    DataTypeExpressionAstNode getValue();
  }  

  @Value.Immutable
  interface AmbiguousExpression extends DataTypeExpressionAstNode {
    DataTypeExpressionAstNode getValue();
  }
  
  @Value.Immutable
  interface Primary extends DataTypeExpressionAstNode {
    DataTypeExpressionAstNode getValue();
  }
  
  @Value.Immutable
  interface ConditionalExpression extends DataTypeExpressionAstNode {
    Condition getCondition();
    DataTypeExpressionAstNode getLeft();
    DataTypeExpressionAstNode getRight();
  }

  @Value.Immutable
  interface Condition extends DataTypeExpressionAstNode {
    ConditionType getSign();
    DataTypeExpressionAstNode getLeft();
    DataTypeExpressionAstNode getRight();
  }
  
  @Value.Immutable
  interface AndCondition extends DataTypeExpressionAstNode {
    DataTypeExpressionAstNode getLeft();
    DataTypeExpressionAstNode getRight();
  }
  
  @Value.Immutable
  interface OrCondition extends DataTypeExpressionAstNode {
    DataTypeExpressionAstNode getLeft();
    DataTypeExpressionAstNode getRight();
  }

  @Value.Immutable
  interface ArithmeticalExpression extends DataTypeExpressionAstNode {
    DataTypeExpressionAstNode getLeft();
    DataTypeExpressionAstNode getRight();
    ArithmeticalType getType();
  }

  @Value.Immutable
  interface MethodName extends DataTypeExpressionAstNode {
    String getValue();
  }
  
  @Value.Immutable
  interface MethodInvocation extends DataTypeExpressionAstNode {
    MethodName getName();
    @Nullable
    TypeName getTypeName();
    @Nullable
    Args getArgs();
    @Nullable
    DataType getDependency();
  }

  @Value.Immutable
  interface Args extends DataTypeExpressionAstNode {
    List<DataTypeExpressionAstNode> getValues();
  }
  
  @Value.Immutable
  interface TypeName extends DataTypeExpressionAstNode {
    String getValue();
    @Nullable
    DataType getDependency();
  }
  
  @Value.Immutable
  interface Literal extends DataTypeExpressionAstNode {
    String getValue();
  }
  
  @Value.Immutable
  interface Unary extends DataTypeExpressionAstNode {
    UnarySign getSign();
    DataTypeExpressionAstNode getValue();
    UnaryType getType();
  }
  
  enum LiteralType {
    STRING, INTEGER, BOOLEAN, DECIMAL, UNKNOWN, NUMERIC, DATE, DATE_TIME
  }
  
  enum UnaryType {
    POSTFIX, PREFIX
  }

  enum UnarySign {
    ADD, SUB, 
    INC, DEC, NOT
  }
  
  enum ArithmeticalType {
    ADD, SUBSTRACT, DIVIDE, MULTIPLY
  }
  
  enum ConditionType {
    NOTEQUAL("!="),
    EQUAL("="),
    LESS("<"),
    LESS_THEN("<="),
    GREATER(">"),
    GREATER_THEN(">=");
    
    private final String value;

    ConditionType(String value) {
      this.value = value;
    }
    
    public String getValue() {
      return value;
    }
    public static ConditionType from(String v) {
      if(v.equals(NOTEQUAL.value)) {
        return NOTEQUAL;
      } else if(v.equals(EQUAL.value)) {
        return EQUAL;
      } else if(v.equals(LESS.value)) {
        return LESS;
      } else if(v.equals(LESS_THEN.value)) {
        return LESS_THEN;
      } else if(v.equals(GREATER.value)) {
        return GREATER;
      }
      return GREATER_THEN;
    } 
  }
  
  interface Visitor<T, R> {
    R visit(Root element);
    T visit(AmbiguousExpression element);
    T visit(ConditionalExpression element);
    T visit(Condition element);
    T visit(MethodName element);
    T visit(MethodInvocation element);
    T visit(Args element);
    T visit(TypeName element);
    T visit(Literal element);
    T visit(Unary element);
    T visit(AndCondition element);
    T visit(OrCondition element);
    T visit(ArithmeticalExpression element);
    T visit(Primary element);
    T visit(DataTypeConversion element);
    T visit(DateConversion element);
    T visit(DateTimeConversion element);
    T visit(BetweenExpression element);
  }
  
  interface DependencyTree {
    Root getNode();
    List<DataType> getDependencies();
    List<Literal> getLiterals();
    
    Optional<DataType> get(MethodInvocation invocation);
    Optional<DataType> get(TypeName typeName);
  }
  
  interface MethodInvocationDependency extends Function<MethodInvocation, DataType> {
  }

  interface TypeNameDependency extends Function<TypeName, DataType> {
  }
}
