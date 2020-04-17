package io.resys.hdes.compiler.spi.java.visitors;

/*-
 * #%L
 * hdes-compiler
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

import io.resys.hdes.ast.api.nodes.AstNode.ArrayTypeDefNode;
import io.resys.hdes.ast.api.nodes.AstNode.DateConversion;
import io.resys.hdes.ast.api.nodes.AstNode.DateTimeConversion;
import io.resys.hdes.ast.api.nodes.AstNode.DecimalConversion;
import io.resys.hdes.ast.api.nodes.AstNode.Literal;
import io.resys.hdes.ast.api.nodes.AstNode.ObjectTypeDefNode;
import io.resys.hdes.ast.api.nodes.AstNode.ScalarTypeDefNode;
import io.resys.hdes.ast.api.nodes.AstNode.TimeConversion;
import io.resys.hdes.ast.api.nodes.AstNode.TypeName;
import io.resys.hdes.ast.api.nodes.AstNodeVisitor.ExpressionAstNodeVisitor;
import io.resys.hdes.ast.api.nodes.ExpressionNode.AdditiveOperation;
import io.resys.hdes.ast.api.nodes.ExpressionNode.AndOperation;
import io.resys.hdes.ast.api.nodes.ExpressionNode.BetweenExpression;
import io.resys.hdes.ast.api.nodes.ExpressionNode.ConditionalExpression;
import io.resys.hdes.ast.api.nodes.ExpressionNode.EqualityOperation;
import io.resys.hdes.ast.api.nodes.ExpressionNode.ExpressionBody;
import io.resys.hdes.ast.api.nodes.ExpressionNode.MethodRefNode;
import io.resys.hdes.ast.api.nodes.ExpressionNode.MultiplicativeOperation;
import io.resys.hdes.ast.api.nodes.ExpressionNode.NegateUnaryOperation;
import io.resys.hdes.ast.api.nodes.ExpressionNode.NotUnaryOperation;
import io.resys.hdes.ast.api.nodes.ExpressionNode.OrOperation;
import io.resys.hdes.ast.api.nodes.ExpressionNode.PositiveUnaryOperation;
import io.resys.hdes.ast.api.nodes.ExpressionNode.PostDecrementUnaryOperation;
import io.resys.hdes.ast.api.nodes.ExpressionNode.PostIncrementUnaryOperation;
import io.resys.hdes.ast.api.nodes.ExpressionNode.PreDecrementUnaryOperation;
import io.resys.hdes.ast.api.nodes.ExpressionNode.PreIncrementUnaryOperation;
import io.resys.hdes.ast.api.nodes.ExpressionNode.TypeRefNode;

public class JavaExpressionAstNodeVisitor implements ExpressionAstNodeVisitor {

  @Override
  public Object visitTypeName(TypeName node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object visitLiteral(Literal node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object visitDateConversion(DateConversion node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object visitDateTimeConversion(DateTimeConversion node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object visitTimeConversion(TimeConversion node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object visitDecimalConversion(DecimalConversion node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object visitObjectInputNode(ObjectTypeDefNode node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object visitArrayInputNode(ArrayTypeDefNode node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object visitScalarInputNode(ScalarTypeDefNode node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object visitExpressionBody(ExpressionBody node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object visitNotUnaryOperation(NotUnaryOperation node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object visitNegateUnaryOperation(NegateUnaryOperation node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object visitPositiveUnaryOperation(PositiveUnaryOperation node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object visitPreIncrementUnaryOperation(PreIncrementUnaryOperation node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object visitPreDecrementUnaryOperation(PreDecrementUnaryOperation node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object visitPostIncrementUnaryOperation(PostIncrementUnaryOperation node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object visitPostDecrementUnaryOperation(PostDecrementUnaryOperation node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object visitMethodRefNode(MethodRefNode node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object visitTypeRefNode(TypeRefNode node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object visitEqualityOperation(EqualityOperation node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object visitConditionalExpression(ConditionalExpression node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object visitBetweenExpression(BetweenExpression node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object visitAndOperation(AndOperation node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object visitOrOperation(OrOperation node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object visitAdditiveOperation(AdditiveOperation node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object visitMultiplicativeOperation(MultiplicativeOperation node) {
    // TODO Auto-generated method stub
    return null;
  }
}
