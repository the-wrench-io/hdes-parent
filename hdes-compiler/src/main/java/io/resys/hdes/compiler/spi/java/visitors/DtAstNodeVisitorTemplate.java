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
import io.resys.hdes.ast.api.nodes.AstNode.Headers;
import io.resys.hdes.ast.api.nodes.AstNode.Literal;
import io.resys.hdes.ast.api.nodes.AstNode.ObjectTypeDefNode;
import io.resys.hdes.ast.api.nodes.AstNode.ScalarTypeDefNode;
import io.resys.hdes.ast.api.nodes.AstNode.TimeConversion;
import io.resys.hdes.ast.api.nodes.AstNode.TypeDefNode;
import io.resys.hdes.ast.api.nodes.AstNode.TypeName;
import io.resys.hdes.ast.api.nodes.AstNodeVisitor.DtAstNodeVisitor;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.DecisionTableBody;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.ExpressionValue;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.HeaderRefValue;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.HitPolicyAll;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.HitPolicyFirst;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.HitPolicyMatrix;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.InOperation;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.LiteralValue;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.Rule;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.RuleRow;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.UndefinedValue;
import io.resys.hdes.ast.api.nodes.ExpressionNode.AndOperation;
import io.resys.hdes.ast.api.nodes.ExpressionNode.EqualityOperation;
import io.resys.hdes.ast.api.nodes.ExpressionNode.NotUnaryOperation;
import io.resys.hdes.ast.api.nodes.ExpressionNode.OrOperation;

public class DtAstNodeVisitorTemplate<T, R> implements DtAstNodeVisitor<T, R> {

  @Override
  public T visitTypeName(TypeName node) {

    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitLiteral(Literal node) {

    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitDateConversion(DateConversion node) {

    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitDateTimeConversion(DateTimeConversion node) {

    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitTimeConversion(TimeConversion node) {

    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitDecimalConversion(DecimalConversion node) {

    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitObjectInputNode(ObjectTypeDefNode node) {

    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitArrayInputNode(ArrayTypeDefNode node) {

    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitScalarInputNode(ScalarTypeDefNode node) {

    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public R visitDecisionTableBody(DecisionTableBody node) {

    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitHeaders(Headers node) {

    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitHeader(TypeDefNode node) {

    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitHitPolicyAll(HitPolicyAll node) {

    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitHitPolicyMatrix(HitPolicyMatrix node) {

    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitHitPolicyFirst(HitPolicyFirst node) {

    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitRuleRow(RuleRow node) {

    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitRule(Rule node) {

    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitUndefinedValue(UndefinedValue node) {

    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitLiteralValue(LiteralValue node) {
    
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitExpressionValue(ExpressionValue node) {
    
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitEqualityOperation(EqualityOperation node) {
    
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitAndOperation(AndOperation node) {
    
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitOrOperation(OrOperation node) {
    
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitInOperation(InOperation node) {
    
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitNotOperation(NotUnaryOperation node) {
    
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitHeaderRefValue(HeaderRefValue node) {

    throw new IllegalArgumentException("Not implemented");
  }
}
