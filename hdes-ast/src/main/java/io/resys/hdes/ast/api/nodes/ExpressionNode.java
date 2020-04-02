package io.resys.hdes.ast.api.nodes;

/*-
 * #%L
 * hdes-ast
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

import java.util.List;
import java.util.Optional;

import org.immutables.value.Value;

public interface ExpressionNode extends AstNode {
  enum AdditiveType { ADD, SUBSTRACT }
  enum MultiplicativeType { DIVIDE, MULTIPLY }
  enum EqualityType { NOTEQUAL("!="), EQUAL("="), LESS("<"), LESS_THEN("<="), GREATER(">"), GREATER_THEN(">=");
    
    private final String value;
    
    EqualityType(String value) {
      this.value = value;
    }
    public String getValue() {
      return value;
    }
  }

  interface UnaryOperation extends ExpressionNode {
    AstNode getValue();
  }

  @Value.Immutable
  interface EvalNode extends ExpressionNode {
    AstNode getValue();
    ScalarType getType();
  }

  /*
   * Unary operation
   */
  @Value.Immutable
  interface NotUnaryOperation extends UnaryOperation { }

  @Value.Immutable
  interface NegateUnaryOperation extends UnaryOperation { }
  
  @Value.Immutable
  interface PositiveUnaryOperation extends UnaryOperation { }
  
  @Value.Immutable
  interface PreIncrementUnaryOperation extends UnaryOperation { }
  
  @Value.Immutable
  interface PreDecrementUnaryOperation extends UnaryOperation { }

  @Value.Immutable
  interface PostIncrementUnaryOperation extends UnaryOperation { }
  
  @Value.Immutable
  interface PostDecrementUnaryOperation extends UnaryOperation { }
  
  /*
   * Ref nodes
   */
  @Value.Immutable
  interface MethodRefNode extends ExpressionNode {
    Optional<TypeRefNode> getType();
    String getName();
    List<AstNode> getValues();
  }

  @Value.Immutable
  interface TypeRefNode extends ExpressionNode {
    String getName();
  }

  /*
   * Conditions and expressions
   */
  @Value.Immutable
  interface EqualityOperation extends ExpressionNode {
    EqualityType getType();
    AstNode getLeft();
    AstNode getRight();
  }

  @Value.Immutable
  interface ConditionalExpression extends ExpressionNode {
    EqualityOperation getOperation();
    AstNode getLeft();
    AstNode getRight();
  }

  @Value.Immutable
  interface BetweenExpression extends ExpressionNode {
    AstNode getValue();
    AstNode getLeft();
    AstNode getRight();
  }

  @Value.Immutable
  interface AndOperation extends ExpressionNode {
    AstNode getLeft();
    AstNode getRight();
  }

  @Value.Immutable
  interface OrOperation extends ExpressionNode {
    AstNode getLeft();
    AstNode getRight();
  }

  @Value.Immutable
  interface AdditiveOperation extends ExpressionNode {
    AdditiveType getType();
    AstNode getLeft();
    AstNode getRight();
  }

  @Value.Immutable
  interface MultiplicativeOperation extends ExpressionNode {
    MultiplicativeType getType();
    AstNode getLeft();
    AstNode getRight();
  }
}
