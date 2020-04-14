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

import com.squareup.javapoet.JavaFile;

import io.resys.hdes.ast.api.nodes.AstNode.ArrayInputNode;
import io.resys.hdes.ast.api.nodes.AstNode.DateConversion;
import io.resys.hdes.ast.api.nodes.AstNode.DateTimeConversion;
import io.resys.hdes.ast.api.nodes.AstNode.DecimalConversion;
import io.resys.hdes.ast.api.nodes.AstNode.Literal;
import io.resys.hdes.ast.api.nodes.AstNode.ObjectInputNode;
import io.resys.hdes.ast.api.nodes.AstNode.ScalarInputNode;
import io.resys.hdes.ast.api.nodes.AstNode.TimeConversion;
import io.resys.hdes.ast.api.nodes.AstNode.TypeName;
import io.resys.hdes.ast.api.nodes.AstNodeVisitor.DtAstNodeVisitor;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.DecisionTableBody;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.ExpressionValue;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.Header;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.Headers;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.HitPolicyAll;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.HitPolicyFirst;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.HitPolicyMatrix;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.LiteralValue;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.Rule;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.RuleRow;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.UndefinedValue;

public class JavaDtAstNodeVisitor implements DtAstNodeVisitor<Void, JavaFile> {

  @Override
  public Void visitTypeName(TypeName node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitLiteral(Literal node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitDateConversion(DateConversion node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitDateTimeConversion(DateTimeConversion node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitTimeConversion(TimeConversion node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitDecimalConversion(DecimalConversion node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitObjectInputNode(ObjectInputNode node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitArrayInputNode(ArrayInputNode node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitScalarInputNode(ScalarInputNode node) {
    // TODO Auto-generated method stub
    return null;
  }

  
  
  @Override
  public JavaFile visitDecisionTableBody(DecisionTableBody node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitHeaders(Headers node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitHeader(Header node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitHitPolicyAll(HitPolicyAll node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitHitPolicyMatrix(HitPolicyMatrix node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitHitPolicyFirst(HitPolicyFirst node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitRuleRow(RuleRow node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitRule(Rule node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitUndefinedValue(UndefinedValue node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitLiteralValue(LiteralValue node) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitExpressionValue(ExpressionValue node) {
    // TODO Auto-generated method stub
    return null;
  }
}
