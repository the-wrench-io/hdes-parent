package io.resys.hdes.ast.spi.visitors.loggers;

import static io.resys.hdes.ast.spi.visitors.loggers.ParserContextLogger.log;

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

import io.resys.hdes.ast.ExpressionParser.AdditiveExpressionContext;
import io.resys.hdes.ast.ExpressionParser.AndExpressionContext;
import io.resys.hdes.ast.ExpressionParser.CompilationUnitContext;
import io.resys.hdes.ast.ExpressionParser.ConditionalAndExpressionContext;
import io.resys.hdes.ast.ExpressionParser.ConditionalExpressionContext;
import io.resys.hdes.ast.ExpressionParser.ConditionalOrExpressionContext;
import io.resys.hdes.ast.ExpressionParser.EqualityExpressionContext;
import io.resys.hdes.ast.ExpressionParser.ExpressionContext;
import io.resys.hdes.ast.ExpressionParser.LiteralContext;
import io.resys.hdes.ast.ExpressionParser.MethodArgsContext;
import io.resys.hdes.ast.ExpressionParser.MethodInvocationContext;
import io.resys.hdes.ast.ExpressionParser.MethodNameContext;
import io.resys.hdes.ast.ExpressionParser.MultiplicativeExpressionContext;
import io.resys.hdes.ast.ExpressionParser.PostfixExpressionContext;
import io.resys.hdes.ast.ExpressionParser.PreDecrementExpressionContext;
import io.resys.hdes.ast.ExpressionParser.PreIncrementExpressionContext;
import io.resys.hdes.ast.ExpressionParser.PrimaryContext;
import io.resys.hdes.ast.ExpressionParser.RelationalExpressionContext;
import io.resys.hdes.ast.ExpressionParser.TypeNameContext;
import io.resys.hdes.ast.ExpressionParser.UnaryExpressionContext;
import io.resys.hdes.ast.ExpressionParser.UnaryExpressionNotPlusMinusContext;
import io.resys.hdes.ast.ExpressionParserBaseVisitor;
import io.resys.hdes.ast.api.nodes.AstNode;


public class ExpressionParserConsoleVisitor extends ExpressionParserBaseVisitor<AstNode> {


  @Override
  public AstNode visitLiteral(LiteralContext ctx) {
    log(ctx);
    return super.visitLiteral(ctx);
  }

  @Override
  public AstNode visitTypeName(TypeNameContext ctx) {
    log(ctx);
    return super.visitTypeName(ctx);
  }

  @Override
  public AstNode visitMethodName(MethodNameContext ctx) {
    log(ctx);
    return super.visitMethodName(ctx);
  }

  @Override
  public AstNode visitMethodInvocation(MethodInvocationContext ctx) {
    log(ctx);
    return super.visitMethodInvocation(ctx);
  }

  @Override
  public AstNode visitMethodArgs(MethodArgsContext ctx) {
    log(ctx);
    return super.visitMethodArgs(ctx);
  }

  @Override
  public AstNode visitPrimary(PrimaryContext ctx) {
    log(ctx);
    return super.visitPrimary(ctx);
  }

  @Override
  public AstNode visitCompilationUnit(CompilationUnitContext ctx) {
    log(ctx);
    return super.visitCompilationUnit(ctx);
  }

  @Override
  public AstNode visitExpression(ExpressionContext ctx) {
    log(ctx);
    return super.visitExpression(ctx);
  }

  @Override
  public AstNode visitConditionalExpression(ConditionalExpressionContext ctx) {
    log(ctx);
    return super.visitConditionalExpression(ctx);
  }

  @Override
  public AstNode visitConditionalOrExpression(ConditionalOrExpressionContext ctx) {
    log(ctx);
    return super.visitConditionalOrExpression(ctx);
  }

  @Override
  public AstNode visitConditionalAndExpression(ConditionalAndExpressionContext ctx) {
    log(ctx);
    return super.visitConditionalAndExpression(ctx);
  }

  @Override
  public AstNode visitAndExpression(AndExpressionContext ctx) {
    log(ctx);
    return super.visitAndExpression(ctx);
  }

  @Override
  public AstNode visitEqualityExpression(EqualityExpressionContext ctx) {
    log(ctx);
    return super.visitEqualityExpression(ctx);
  }

  @Override
  public AstNode visitRelationalExpression(RelationalExpressionContext ctx) {
    log(ctx);
    return super.visitRelationalExpression(ctx);
  }

  @Override
  public AstNode visitAdditiveExpression(AdditiveExpressionContext ctx) {
    log(ctx);
    return super.visitAdditiveExpression(ctx);
  }

  @Override
  public AstNode visitMultiplicativeExpression(MultiplicativeExpressionContext ctx) {
    log(ctx);
    return super.visitMultiplicativeExpression(ctx);
  }

  @Override
  public AstNode visitUnaryExpression(UnaryExpressionContext ctx) {
    log(ctx);
    return super.visitUnaryExpression(ctx);
  }

  @Override
  public AstNode visitPreIncrementExpression(PreIncrementExpressionContext ctx) {
    log(ctx);
    return super.visitPreIncrementExpression(ctx);
  }

  @Override
  public AstNode visitPreDecrementExpression(PreDecrementExpressionContext ctx) {
    log(ctx);
    return super.visitPreDecrementExpression(ctx);
  }

  @Override
  public AstNode visitUnaryExpressionNotPlusMinus(UnaryExpressionNotPlusMinusContext ctx) {
    log(ctx);
    return super.visitUnaryExpressionNotPlusMinus(ctx);
  }

  @Override
  public AstNode visitPostfixExpression(PostfixExpressionContext ctx) {
    log(ctx);
    return super.visitPostfixExpression(ctx);
  }
}

