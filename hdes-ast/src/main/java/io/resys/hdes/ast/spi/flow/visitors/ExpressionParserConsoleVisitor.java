package io.resys.hdes.ast.spi.flow.visitors;

import org.antlr.v4.runtime.ParserRuleContext;

import io.resys.hdes.ast.ExpressionParser.AdditiveExpressionContext;
import io.resys.hdes.ast.ExpressionParser.AndExpressionContext;
import io.resys.hdes.ast.ExpressionParser.ArgsContext;
import io.resys.hdes.ast.ExpressionParser.CompilationUnitContext;
import io.resys.hdes.ast.ExpressionParser.ConditionalAndExpressionContext;
import io.resys.hdes.ast.ExpressionParser.ConditionalExpressionContext;
import io.resys.hdes.ast.ExpressionParser.ConditionalOrExpressionContext;
import io.resys.hdes.ast.ExpressionParser.EqualityExpressionContext;
import io.resys.hdes.ast.ExpressionParser.ExpressionContext;
import io.resys.hdes.ast.ExpressionParser.LiteralContext;
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
  public AstNode visitArgs(ArgsContext ctx) {
    log(ctx);
    return super.visitArgs(ctx);
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

  private static final void log(ParserRuleContext context) {
    System.out.println("visiting: " + context.getClass().getSimpleName() 
        //+ ": " + context.getText()
        );
  }
}

