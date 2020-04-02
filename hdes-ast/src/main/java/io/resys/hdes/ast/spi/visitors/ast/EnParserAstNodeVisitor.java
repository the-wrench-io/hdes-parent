package io.resys.hdes.ast.spi.visitors.ast;

import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.immutables.value.Value;

import io.resys.hdes.ast.ExpressionParser;
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
import io.resys.hdes.ast.api.AstNodeException;
import io.resys.hdes.ast.api.nodes.AstNode;
import io.resys.hdes.ast.api.nodes.AstNode.ScalarType;
import io.resys.hdes.ast.api.nodes.ExpressionNode;
import io.resys.hdes.ast.api.nodes.ExpressionNode.AdditiveType;
import io.resys.hdes.ast.api.nodes.ExpressionNode.EqualityOperation;
import io.resys.hdes.ast.api.nodes.ExpressionNode.EqualityType;
import io.resys.hdes.ast.api.nodes.ExpressionNode.EvalNode;
import io.resys.hdes.ast.api.nodes.ExpressionNode.MethodRefNode;
import io.resys.hdes.ast.api.nodes.ExpressionNode.MultiplicativeType;
import io.resys.hdes.ast.api.nodes.ExpressionNode.TypeRefNode;
import io.resys.hdes.ast.api.nodes.ImmutableAdditiveOperation;
import io.resys.hdes.ast.api.nodes.ImmutableAndOperation;
import io.resys.hdes.ast.api.nodes.ImmutableBetweenExpression;
import io.resys.hdes.ast.api.nodes.ImmutableConditionalExpression;
import io.resys.hdes.ast.api.nodes.ImmutableEqualityOperation;
import io.resys.hdes.ast.api.nodes.ImmutableEvalNode;
import io.resys.hdes.ast.api.nodes.ImmutableMethodRefNode;
import io.resys.hdes.ast.api.nodes.ImmutableMultiplicativeOperation;
import io.resys.hdes.ast.api.nodes.ImmutableNegateUnaryOperation;
import io.resys.hdes.ast.api.nodes.ImmutableNotUnaryOperation;
import io.resys.hdes.ast.api.nodes.ImmutableOrOperation;
import io.resys.hdes.ast.api.nodes.ImmutablePositiveUnaryOperation;
import io.resys.hdes.ast.api.nodes.ImmutablePostDecrementUnaryOperation;
import io.resys.hdes.ast.api.nodes.ImmutablePostIncrementUnaryOperation;
import io.resys.hdes.ast.api.nodes.ImmutablePreDecrementUnaryOperation;
import io.resys.hdes.ast.api.nodes.ImmutablePreIncrementUnaryOperation;
import io.resys.hdes.ast.api.nodes.ImmutableTypeRefNode;
import io.resys.hdes.ast.spi.visitors.ast.Nodes.TokenIdGenerator;

public class EnParserAstNodeVisitor extends ExpressionParserBaseVisitor<AstNode> {
  private final TokenIdGenerator tokenIdGenerator;
  private final ScalarType evalType;

  public EnParserAstNodeVisitor(TokenIdGenerator tokenIdGenerator, ScalarType evalType) {
    super();
    this.tokenIdGenerator = tokenIdGenerator;
    this.evalType = evalType;
  }

  // Internal only
  @Value.Immutable
  public interface RedundentMethodName extends ExpressionNode {
    String getValue();
  }

  @Value.Immutable
  public interface RedundentArgs extends ExpressionNode {
    List<AstNode> getValues();
  }

  @Override
  public AstNode visitLiteral(LiteralContext ctx) {
    return Nodes.literal(ctx, token(ctx));
  }

  @Override
  public TypeRefNode visitTypeName(TypeNameContext ctx) {
    return ImmutableTypeRefNode.builder()
        .token(token(ctx))
        .name(ctx.getText()).build();
  }

  @Override
  public RedundentMethodName visitMethodName(MethodNameContext ctx) {
    return ImmutableRedundentMethodName.builder()
        .token(token(ctx))
        .value(ctx.getText())
        .build();
  }

  @Override
  public MethodRefNode visitMethodInvocation(MethodInvocationContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableMethodRefNode.builder()
        .token(token(ctx))
        .name(nodes.of(RedundentMethodName.class).get().getValue())
        .type(nodes.of(TypeRefNode.class))
        .values(nodes.of(RedundentArgs.class).map(a -> a.getValues()).orElse(Collections.emptyList()))
        .build();
  }

  @Override
  public RedundentArgs visitArgs(ArgsContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableRedundentArgs.builder()
        .token(token(ctx))
        .values(nodes.list(AstNode.class))
        .build();
  }

  @Override
  public AstNode visitPrimary(PrimaryContext ctx) {
    int n = ctx.getChildCount();
    for (int i = 0; i < n; i++) {
      ParseTree c = ctx.getChild(i);
      if (c instanceof TerminalNode) {
        continue;
      }
      return c.accept(this);
    }
    throw new AstNodeException("unknown primary node: " + ctx.getText() + "!");
  }

  @Override
  public EvalNode visitCompilationUnit(CompilationUnitContext ctx) {
    return ImmutableEvalNode.builder()
        .value(first(ctx))
        .token(token(ctx))
        .type(evalType)
        .build();
  }

  @Override
  public AstNode visitExpression(ExpressionContext ctx) {
    int n = ctx.getChildCount();
    if (n == 1) {
      return first(ctx);
    }
    ParseTree c = ctx.getChild(0);
    AstNode childResult = c.accept(this);
    throw new AstNodeException("Unknown node: '" + childResult + "', '" + c.getText() + "'");
  }

  @Override
  public AstNode visitConditionalExpression(ConditionalExpressionContext ctx) {
    int n = ctx.getChildCount();
    if (n == 1) {
      return first(ctx);
    }
    // x < 20 ? 5 : 120
    // child[0] = x < 20
    // child[1] = ?
    // child[2] = 5
    // child[3] = :
    // child[4] = 120
    AstNode condition = ctx.getChild(0).accept(this);
    AstNode left = ctx.getChild(2).accept(this);
    AstNode right = ctx.getChild(4).accept(this);
    ParseTree first = ctx.getChild(1);
    if (first instanceof TerminalNode &&
        ((TerminalNode) first).getSymbol().getType() == ExpressionParser.BETWEEN) {
      return ImmutableBetweenExpression.builder()
          .token(token(ctx))
          .value(condition)
          .left(left)
          .right(right)
          .build();
    }
    return ImmutableConditionalExpression.builder()
        .token(token(ctx))
        .operation((EqualityOperation) condition)
        .left(left)
        .right(right)
        .build();
  }

  @Override
  public AstNode visitConditionalOrExpression(ConditionalOrExpressionContext ctx) {
    int n = ctx.getChildCount();
    if (n == 1) {
      return first(ctx);
    }
    AstNode left = ctx.getChild(0).accept(this);
    AstNode right = ctx.getChild(2).accept(this);
    return ImmutableOrOperation.builder()
        .token(token(ctx))
        .left(left)
        .right(right).build();
  }

  @Override
  public AstNode visitConditionalAndExpression(ConditionalAndExpressionContext ctx) {
    int n = ctx.getChildCount();
    if (n == 1) {
      return first(ctx);
    }
    ParseTree c = ctx.getChild(0);
    AstNode childResult = c.accept(this);
    throw new AstNodeException("Unknown node: '" + childResult + "', '" + c.getText() + "'");
  }

  @Override
  public AstNode visitAndExpression(AndExpressionContext ctx) {
    int n = ctx.getChildCount();
    if (n == 1) {
      return first(ctx);
    }
    AstNode left = ctx.getChild(0).accept(this);
    AstNode right = ctx.getChild(2).accept(this);
    return ImmutableAndOperation.builder()
        .token(token(ctx))
        .left(left).right(right).build();
  }

  @Override
  public AstNode visitEqualityExpression(EqualityExpressionContext ctx) {
    int n = ctx.getChildCount();
    if (n == 1) {
      return first(ctx);
    }

    AstNode left = ctx.getChild(0).accept(this);
    String v = ctx.getChild(1).getText();
    AstNode right = ctx.getChild(2).accept(this);
    EqualityType type;
    
    if (v.equals(EqualityType.NOTEQUAL.getValue())) {
      type = EqualityType.NOTEQUAL;
    } else {
      type = EqualityType.EQUAL;
    }
    
    return ImmutableEqualityOperation.builder()
        .token(token(ctx))
        .type(type)
        .left(left).right(right).build();
  }

  @Override
  public AstNode visitRelationalExpression(RelationalExpressionContext ctx) {
    int n = ctx.getChildCount();
    if (n == 1) {
      return first(ctx);
    }

    
    AstNode left = ctx.getChild(0).accept(this);
    String v = ctx.getChild(1).getText();
    AstNode right = ctx.getChild(2).accept(this);
    EqualityType type;
    if (v.equals(EqualityType.LESS.getValue())) {
      type = EqualityType.LESS;
    } else if (v.equals(EqualityType.LESS_THEN.getValue())) {
      type = EqualityType.LESS_THEN;
    } else if (v.equals(EqualityType.GREATER.getValue())) {
      type = EqualityType.GREATER;
    } else {
      type = EqualityType.GREATER_THEN;
    }
    return ImmutableEqualityOperation.builder()
        .token(token(ctx))
        .type(type)
        .left(left).right(right).build();
  }

  @Override
  public AstNode visitAdditiveExpression(AdditiveExpressionContext ctx) {
    int n = ctx.getChildCount();
    if (n == 1) {
      return first(ctx);
    }

    AstNode left = ctx.getChild(0).accept(this);
    AstNode right = ctx.getChild(2).accept(this);
    TerminalNode sign = (TerminalNode) ctx.getChild(1);
    return ImmutableAdditiveOperation.builder()
        .token(token(ctx))
        .type(sign.getSymbol().getType() == ExpressionParser.ADD ? AdditiveType.ADD : AdditiveType.SUBSTRACT)
        .left(left).right(right).build();
  }

  @Override
  public AstNode visitMultiplicativeExpression(MultiplicativeExpressionContext ctx) {
    int n = ctx.getChildCount();
    if (n == 1) {
      return first(ctx);
    }

    AstNode left = ctx.getChild(0).accept(this);
    AstNode right = ctx.getChild(2).accept(this);
    TerminalNode sign = (TerminalNode) ctx.getChild(1);
    return ImmutableMultiplicativeOperation.builder()
        .token(token(ctx))
        .type(sign.getSymbol().getType() == ExpressionParser.MULTIPLY ? MultiplicativeType.MULTIPLY : MultiplicativeType.DIVIDE)
        .left(left).right(right).build();
  }

  @Override
  public AstNode visitUnaryExpression(UnaryExpressionContext ctx) {
    int n = ctx.getChildCount();
    if (n == 1) {
      return first(ctx);
    }
    
    TerminalNode terminalNode = null;
    AstNode childResult = null;
    for (int i = 0; i < n; i++) {
      ParseTree c = ctx.getChild(i);

      if (c instanceof TerminalNode) {
        terminalNode = (TerminalNode) c;
        continue;
      }
      childResult = c.accept(this);
    }
    
    if(terminalNode.getSymbol().getType() == ExpressionParser.ADD) {
      return ImmutablePositiveUnaryOperation.builder()
          .token(token(ctx))
          .value(childResult).build();
    }
    return ImmutableNegateUnaryOperation.builder()
        .token(token(ctx))
        .value(childResult).build();
  }

  @Override
  public AstNode visitPreIncrementExpression(PreIncrementExpressionContext ctx) {
    int n = ctx.getChildCount();
    if (n == 1) {
      return first(ctx);
    }
    
    AstNode childResult = null;
    for (int i = 0; i < n; i++) {
      ParseTree c = ctx.getChild(i);
      if (c instanceof TerminalNode) {
        continue;
      }
      childResult = c.accept(this);
    }
    
    return ImmutablePreIncrementUnaryOperation.builder()
        .token(token(ctx))
        .value(childResult).build();
  }

  @Override
  public AstNode visitPreDecrementExpression(PreDecrementExpressionContext ctx) {
    int n = ctx.getChildCount();
    if (n == 1) {
      return first(ctx);
    }
    
    AstNode childResult = null;
    for (int i = 0; i < n; i++) {
      ParseTree c = ctx.getChild(i);
      if (c instanceof TerminalNode) {
        continue;
      }
      childResult = c.accept(this);
    }
    
    return ImmutablePreDecrementUnaryOperation.builder()
        .token(token(ctx))
        .value(childResult).build();
  }

  @Override
  public AstNode visitUnaryExpressionNotPlusMinus(UnaryExpressionNotPlusMinusContext ctx) {
    int n = ctx.getChildCount();
    if (n == 1) {
      return first(ctx);
    }

    AstNode childResult = null;
    for (int i = 0; i < n; i++) {
      ParseTree c = ctx.getChild(i);
      if (c instanceof TerminalNode) {
        continue;
      }
      childResult = c.accept(this);
    }
    return ImmutableNotUnaryOperation.builder()
        .token(token(ctx))
        .value(childResult).build();
  }

  @Override
  public AstNode visitPostfixExpression(PostfixExpressionContext ctx) {
    int n = ctx.getChildCount();
    if (n == 1) {
      return first(ctx);
    }
    
    TerminalNode terminalNode = null;
    AstNode childResult = null;
    for (int i = 0; i < n; i++) {
      ParseTree c = ctx.getChild(i);
      if (c instanceof TerminalNode) {
        terminalNode = (TerminalNode) c;
        continue;
      }
      childResult = c.accept(this);
    }
    if(terminalNode.getSymbol().getType() == ExpressionParser.INCREMENT) {
      return ImmutablePostIncrementUnaryOperation.builder()
        .token(token(ctx))
        .value(childResult).build();
    } 
    return ImmutablePostDecrementUnaryOperation.builder()
        .token(token(ctx))
        .value(childResult).build();
  }

  private AstNode first(ParserRuleContext ctx) {
    ParseTree c = ctx.getChild(0);
    return c.accept(this);
  }

  private Nodes nodes(ParserRuleContext node) {
    return Nodes.from(node, this);
  }

  private AstNode.Token token(ParserRuleContext node) {
    return Nodes.token(node, tokenIdGenerator);
  }
}
