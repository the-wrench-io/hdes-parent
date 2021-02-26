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

import javax.annotation.Nullable;

import org.immutables.value.Value;

import io.resys.hdes.ast.api.nodes.InvocationNode.SimpleInvocation;
import io.resys.hdes.ast.api.nodes.InvocationNode.StaticMethodType;

public interface ExpressionNode extends HdesNode {
  enum AdditiveType { ADD, SUBSTRACT }
  enum MultiplicativeType { DIVIDE, MULTIPLY }
  enum EqualityType { 
    NOTEQUAL("!="), EQUAL("="), 
    LESS("<"), LESS_THEN("<="), 
    GREATER(">"), GREATER_THEN(">=");
    
    private final String value;
    
    EqualityType(String value) {
      this.value = value;
    }
    public String getValue() {
      return value;
    }
  }
  

  @Value.Immutable
  interface ExpressionBody extends ExpressionNode, BodyNode {
    String getSrc();
    HdesNode getValue();
    @Nullable @Value.Default
    default HdesNodeType getNodeType() { return HdesNodeType.BODY_EX; }
  }

  /*
   * Unary operation
   */
  interface Unary extends ExpressionNode {
    HdesNode getValue();
  }
  @Value.Immutable
  interface NotUnary extends Unary {
    default HdesNodeType getNodeType() { return HdesNodeType.EX_UNARY_NOT; }
  }

  @Value.Immutable
  interface NegateUnary extends Unary {
    default HdesNodeType getNodeType() { return HdesNodeType.EX_UNARY_NEGATE; }
  }
  
  @Value.Immutable
  interface PositiveUnary extends Unary {
    default HdesNodeType getNodeType() { return HdesNodeType.EX_UNARY_POS; }
  }

  /*
   * Conditions and expressions
   */
  @Value.Immutable
  interface EqualityOperation extends ExpressionNode {
    EqualityType getType();
    HdesNode getLeft();
    HdesNode getRight();
    default HdesNodeType getNodeType() { return HdesNodeType.EX_EQUALITY; }
  }

  // operation ? val1 : val2 
  @Value.Immutable
  interface ConditionalExpression extends ExpressionNode {
    EqualityOperation getOperation();
    HdesNode getLeft();
    HdesNode getRight();
    default HdesNodeType getNodeType() { return HdesNodeType.EX_CONDITIONAL; }
  }

  @Value.Immutable
  interface BetweenExpression extends ExpressionNode {
    HdesNode getValue();
    HdesNode getLeft();
    HdesNode getRight();
    default HdesNodeType getNodeType() { return HdesNodeType.EX_BETWEEN; }
  }
  
  @Value.Immutable
  interface InExpression extends ExpressionNode {
    HdesNode getLeft();
    List<HdesNode> getRight();
    default HdesNodeType getNodeType() { return HdesNodeType.EX_IN; }
  }

  @Value.Immutable
  interface AndExpression extends ExpressionNode {
    HdesNode getLeft();
    HdesNode getRight();
    default HdesNodeType getNodeType() { return HdesNodeType.EX_AND; }
  }

  @Value.Immutable
  interface OrExpression extends ExpressionNode {
    HdesNode getLeft();
    HdesNode getRight();
    default HdesNodeType getNodeType() { return HdesNodeType.EX_OR; }
  }

  @Value.Immutable
  interface AdditiveExpression extends ExpressionNode {
    AdditiveType getType();
    HdesNode getLeft();
    HdesNode getRight();
    default HdesNodeType getNodeType() { return HdesNodeType.EX_ADDITIVE; }
  }

  @Value.Immutable
  interface MultiplicativeExpression extends ExpressionNode {
    MultiplicativeType getType();
    HdesNode getLeft();
    HdesNode getRight();
    default HdesNodeType getNodeType() { return HdesNodeType.EX_MULTIPLICATIVE; }
  }
  
  interface CallMethodExpression extends ExpressionNode {}
  
  @Value.Immutable
  interface StaticMethodExpression extends CallMethodExpression {
    StaticMethodType getType();
    List<HdesNode> getValues();
    default HdesNodeType getNodeType() { return HdesNodeType.EX_INSTANCE_STATIC_METHOD; }
  }

  @Value.Immutable
  interface InstanceMethodExpression extends CallMethodExpression {
    SimpleInvocation getName();
    List<HdesNode> getValues();
    Optional<HdesNode> getNext();
    default HdesNodeType getNodeType() { return HdesNodeType.EX_INSTANCE_METHOD; }
  }
  
  @Value.Immutable
  interface LambdaExpression extends CallMethodExpression {    
    InvocationNode getType();
    SimpleInvocation getParam();
    HdesNode getBody();
    
    Boolean getFindFirst();
    List<LambdaSortExpression> getSort();
    List<LambdaFilterExpression> getFilter();
    
    Optional<HdesNode> getNext();
    default HdesNodeType getNodeType() { return HdesNodeType.EX_LAMBDA; }
  }
  
  @Value.Immutable
  interface LambdaSortExpression extends ExpressionNode {
    SimpleInvocation getParam();
    HdesNode getBody();
    default HdesNodeType getNodeType() { return HdesNodeType.EX_LAMBDA_SORT; }
  }
  
  @Value.Immutable
  interface LambdaFilterExpression extends ExpressionNode {
    SimpleInvocation getParam();
    HdesNode getBody();
    default HdesNodeType getNodeType() { return HdesNodeType.EX_LAMBDA_FILTER; }
  }
}
