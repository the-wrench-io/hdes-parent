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

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import io.resys.hdes.datatype.DataTypeParser;
import io.resys.hdes.datatype.DataTypeParser.AdditiveExpressionContext;
import io.resys.hdes.datatype.DataTypeParser.AndExpressionContext;
import io.resys.hdes.datatype.DataTypeParser.ArgsContext;
import io.resys.hdes.datatype.DataTypeParser.CompilationUnitContext;
import io.resys.hdes.datatype.DataTypeParser.ConditionalAndExpressionContext;
import io.resys.hdes.datatype.DataTypeParser.ConditionalExpressionContext;
import io.resys.hdes.datatype.DataTypeParser.ConditionalOrExpressionContext;
import io.resys.hdes.datatype.DataTypeParser.EqualityExpressionContext;
import io.resys.hdes.datatype.DataTypeParser.ExpressionContext;
import io.resys.hdes.datatype.DataTypeParser.LiteralContext;
import io.resys.hdes.datatype.DataTypeParser.MethodInvocationContext;
import io.resys.hdes.datatype.DataTypeParser.MethodNameContext;
import io.resys.hdes.datatype.DataTypeParser.MultiplicativeExpressionContext;
import io.resys.hdes.datatype.DataTypeParser.PostfixExpressionContext;
import io.resys.hdes.datatype.DataTypeParser.PreDecrementExpressionContext;
import io.resys.hdes.datatype.DataTypeParser.PreIncrementExpressionContext;
import io.resys.hdes.datatype.DataTypeParser.PrimaryContext;
import io.resys.hdes.datatype.DataTypeParser.RelationalExpressionContext;
import io.resys.hdes.datatype.DataTypeParser.TypeNameContext;
import io.resys.hdes.datatype.DataTypeParser.UnaryExpressionContext;
import io.resys.hdes.datatype.DataTypeParser.UnaryExpressionNotPlusMinusContext;
import io.resys.hdes.datatype.DataTypeParserBaseVisitor;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.AmbiguousExpression;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.Args;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.ArithmeticalType;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.Condition;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.ConditionType;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.Literal;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.LiteralType;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.MethodInvocation;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.MethodName;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.Position;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.PositionToken;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.TypeName;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.UnarySign;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.UnaryType;
import io.resys.hdes.datatype.api.ImmutableAndCondition;
import io.resys.hdes.datatype.api.ImmutableArgs;
import io.resys.hdes.datatype.api.ImmutableArithmeticalExpression;
import io.resys.hdes.datatype.api.ImmutableBetweenExpression;
import io.resys.hdes.datatype.api.ImmutableCondition;
import io.resys.hdes.datatype.api.ImmutableConditionalExpression;
import io.resys.hdes.datatype.api.ImmutableLiteral;
import io.resys.hdes.datatype.api.ImmutableMethodInvocation;
import io.resys.hdes.datatype.api.ImmutableMethodName;
import io.resys.hdes.datatype.api.ImmutableOrCondition;
import io.resys.hdes.datatype.api.ImmutablePosition;
import io.resys.hdes.datatype.api.ImmutablePositionToken;
import io.resys.hdes.datatype.api.ImmutablePrimary;
import io.resys.hdes.datatype.api.ImmutableRoot;
import io.resys.hdes.datatype.api.ImmutableTypeName;
import io.resys.hdes.datatype.api.ImmutableUnary;
import io.resys.hdes.datatype.spi.antlr.errors.AstNodeException;

public class AstNodeVisitor extends DataTypeParserBaseVisitor<DataTypeExpressionAstNode> {

  @Override
  public DataTypeExpressionAstNode visitCompilationUnit(CompilationUnitContext ctx) {
    log("compilation unit", ctx);
    return ImmutableRoot.builder()
        .value(first(ctx))
        .pos(createPosition(ctx))
        .build();
  }

  @Override
  public Literal visitLiteral(LiteralContext ctx) {
    log("literal", ctx);
    String value = ctx.getText();
    
    LiteralType type = null;
    switch (ctx.getStart().getType()) {
    case DataTypeParser.StringLiteral:
      type = LiteralType.STRING;
      break;
    case DataTypeParser.BooleanLiteral:
      type = LiteralType.BOOLEAN;
      break;
    case DataTypeParser.DecimalLiteral:
      type = LiteralType.DECIMAL;
      break;
    case DataTypeParser.IntegerLiteral:
      type = LiteralType.INTEGER;
      value = value.replaceAll("_", "");
      break;
    default: throw new AstNodeException("Unknown literal: " + ctx.getText() + "!");
    }
    
    if (type == LiteralType.STRING && value.length() > 2) {
      value = value.substring(1, value.length() - 1);
    }
    return ImmutableLiteral.builder().pos(createPosition(ctx)).literalType(type).value(value).build();
  }

  @Override
  public TypeName visitTypeName(TypeNameContext ctx) {
    return ImmutableTypeName.builder().pos(createPosition(ctx)).value(ctx.getText()).build();
  }

  @Override
  public MethodName visitMethodName(MethodNameContext ctx) {
    return ImmutableMethodName.builder().pos(createPosition(ctx)).value(ctx.getText()).build();
  }

  @Override
  public MethodInvocation visitMethodInvocation(MethodInvocationContext ctx) {
    log("method-invocation", ctx);
    
    MethodName methodName = null;
    TypeName typeName = null;
    Args args = null;
    int n = ctx.getChildCount();
    for (int i = 0; i < n; i++) {
      ParseTree c = ctx.getChild(i);
      if (c instanceof TerminalNode) {
        continue;
      }
      DataTypeExpressionAstNode childResult = c.accept(this);
      if (childResult instanceof MethodName) {
        methodName = (MethodName) childResult;
      } else if (childResult instanceof TypeName) {
        typeName = (TypeName) childResult;
      } else if (childResult instanceof Args) {
        args = (Args) childResult;
      } else {
        throw new RuntimeException("Unknown node: '" + childResult + "', '" + c.getText() + "'");
      }
    }
    return ImmutableMethodInvocation.builder()
        .name(methodName)
        .pos(createPosition(ctx))
        .typeName(typeName)
        .args(args)
        .build();
  }

  @Override
  public Args visitArgs(ArgsContext ctx) {
    
    log("args", ctx);
    List<DataTypeExpressionAstNode> values = new ArrayList<>();
    int n = ctx.getChildCount();
    for (int i = 0; i < n; i++) {
      ParseTree c = ctx.getChild(i);
      if (c instanceof TerminalNode) {
        continue;
      }
      DataTypeExpressionAstNode childResult = c.accept(this);
      values.add(childResult);
    }
    return ImmutableArgs.builder().pos(createPosition(ctx)).values(values).build();
  }

  @Override
  public DataTypeExpressionAstNode visitExpression(ExpressionContext ctx) {
    DataTypeExpressionAstNode.AmbiguousExpression result = null;
    int n = ctx.getChildCount();
    if (n == 1) {
      return first(ctx);
    }
    
    if (n > 0) {
      ParseTree c = ctx.getChild(0);
      DataTypeExpressionAstNode childResult = c.accept(this);
      throw new AstNodeException("Unknown node: '" + childResult + "', '" + c.getText() + "'");
    }
    return result;
  }

  @Override
  public DataTypeExpressionAstNode visitConditionalExpression(ConditionalExpressionContext ctx) {
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
    
    log("conditional", ctx);
    
    DataTypeExpressionAstNode condition = ctx.getChild(0).accept(this);
    DataTypeExpressionAstNode left = ctx.getChild(2).accept(this);
    DataTypeExpressionAstNode right = ctx.getChild(4).accept(this);
    
    ParseTree first = ctx.getChild(1);
    if (first instanceof TerminalNode && 
        ((TerminalNode) first).getSymbol().getType() == DataTypeParser.BETWEEN) {
      return ImmutableBetweenExpression.builder()
          .pos(createPosition(ctx))
          .value(condition)
          .left(left)
          .right(right)
          .build();
    }
    return ImmutableConditionalExpression.builder()
        .pos(createPosition(ctx))
        .condition((Condition) condition)
        .left(left)
        .right(right)
        .build();
  }

  @Override
  public DataTypeExpressionAstNode visitRelationalExpression(RelationalExpressionContext ctx) {
    int n = ctx.getChildCount();
    if (n == 1) {
      return first(ctx);
    }
    log("relational", ctx);
    DataTypeExpressionAstNode left = ctx.getChild(0).accept(this);
    String sign = ctx.getChild(1).getText();
    DataTypeExpressionAstNode right = ctx.getChild(2).accept(this);
    return ImmutableCondition.builder().pos(createPosition(ctx)).left(left).sign(ConditionType.from(sign)).right(right).build();
  }
  
  @Override
  public DataTypeExpressionAstNode visitEqualityExpression(EqualityExpressionContext ctx) {
    int n = ctx.getChildCount();
    if (n == 1) {
      return first(ctx);
    }
    log("equality", ctx);
    DataTypeExpressionAstNode left = ctx.getChild(0).accept(this);
    String sign = ctx.getChild(1).getText();
    DataTypeExpressionAstNode right = ctx.getChild(2).accept(this);
    return ImmutableCondition.builder().pos(createPosition(ctx)).sign(ConditionType.from(sign)).left(left).right(right).build();
  }

  @Override
  public DataTypeExpressionAstNode visitPrimary(PrimaryContext ctx) {
    int n = ctx.getChildCount();
    for (int i = 0; i < n; i++) {
      ParseTree c = ctx.getChild(i);
      if (c instanceof TerminalNode) {
        continue;
      }
      DataTypeExpressionAstNode childResult = c.accept(this);
      if (childResult instanceof AmbiguousExpression) {
        return ImmutablePrimary.builder().pos(createPosition(ctx)).value(childResult).build();
      } else {
        return childResult;
      }
    }
    return null;
  }

  @Override
  public DataTypeExpressionAstNode visitConditionalOrExpression(ConditionalOrExpressionContext ctx) {
    int n = ctx.getChildCount();
    if (n == 1) {
      return first(ctx);
    }
    log("conditional-or", ctx);
    DataTypeExpressionAstNode left = ctx.getChild(0).accept(this);
    DataTypeExpressionAstNode right = ctx.getChild(2).accept(this);
    return ImmutableOrCondition.builder().pos(createPosition(ctx)).left(left).right(right).build();
  }

  @Override
  public DataTypeExpressionAstNode visitConditionalAndExpression(ConditionalAndExpressionContext ctx) {
    int n = ctx.getChildCount();
    if (n == 1) {
      return first(ctx);
    }
    if (n > 0) {
      log("conditional-and", ctx);
      ParseTree c = ctx.getChild(0);
      DataTypeExpressionAstNode childResult = c.accept(this);
      throw new AstNodeException("Unknown node: '" + childResult + "', '" + c.getText() + "'");
    }
    return null;
  }

  @Override
  public DataTypeExpressionAstNode visitAndExpression(AndExpressionContext ctx) {
    int n = ctx.getChildCount();
    if (n == 1) {
      return first(ctx);
    }
    log("and", ctx);
    DataTypeExpressionAstNode left = ctx.getChild(0).accept(this);
    DataTypeExpressionAstNode right = ctx.getChild(2).accept(this);
    return ImmutableAndCondition.builder().pos(createPosition(ctx)).left(left).right(right).build();
  }

  @Override
  public DataTypeExpressionAstNode visitAdditiveExpression(AdditiveExpressionContext ctx) {
    int n = ctx.getChildCount();
    if (n == 1) {
      return first(ctx);
    }
    log("add", ctx);
    DataTypeExpressionAstNode left = ctx.getChild(0).accept(this);
    DataTypeExpressionAstNode right = ctx.getChild(2).accept(this);
    return ImmutableArithmeticalExpression.builder()
        .pos(createPosition(ctx))
        .type(ctx.getChild(1).getText().equals("+") ? ArithmeticalType.ADD : ArithmeticalType.SUBSTRACT)
        .left(left).right(right).build();
  }

  @Override
  public DataTypeExpressionAstNode visitMultiplicativeExpression(MultiplicativeExpressionContext ctx) {
    int n = ctx.getChildCount();
    if (n == 1) {
      return first(ctx);
    }
    log("multiplication", ctx);
    DataTypeExpressionAstNode left = ctx.getChild(0).accept(this);
    DataTypeExpressionAstNode right = ctx.getChild(2).accept(this);
    return ImmutableArithmeticalExpression.builder()
        .pos(createPosition(ctx))
        .type(ctx.getChild(1).getText().equals("*") ? ArithmeticalType.MULTIPLY : ArithmeticalType.DIVIDE)
        .left(left).right(right).build();
  }

  @Override
  public DataTypeExpressionAstNode visitUnaryExpression(UnaryExpressionContext ctx) {
    int n = ctx.getChildCount();
    if (n == 1) {
      return first(ctx);
    }
    
    log("unary", ctx);
    TerminalNode terminalNode = null;
    DataTypeExpressionAstNode childResult = null;
    for (int i = 0; i < n; i++) {
      ParseTree c = ctx.getChild(i);

      if (c instanceof TerminalNode) {
        terminalNode = (TerminalNode) c;
        continue;
      }
      childResult = c.accept(this);
    }
    
    return ImmutableUnary.builder()
        .pos(createPosition(ctx))
        .type(UnaryType.PREFIX)
        .sign(terminalNode.getSymbol().getType() == DataTypeParser.ADD ? UnarySign.ADD : UnarySign.SUB)
        .value(childResult).build();
  }

  @Override
  public DataTypeExpressionAstNode visitPreIncrementExpression(PreIncrementExpressionContext ctx) {
    
    int n = ctx.getChildCount();
    if (n == 1) {
      return first(ctx);
    }
    
    log("unary-preincrement", ctx);
    DataTypeExpressionAstNode childResult = null;
    for (int i = 0; i < n; i++) {
      ParseTree c = ctx.getChild(i);
      if (c instanceof TerminalNode) {
        continue;
      }
      childResult = c.accept(this);
    }
    
    return ImmutableUnary.builder()
        .pos(createPosition(ctx))
        .type(UnaryType.PREFIX)
        .sign(UnarySign.INC)
        .value(childResult).build();
  }

  @Override
  public DataTypeExpressionAstNode visitPreDecrementExpression(PreDecrementExpressionContext ctx) {
    int n = ctx.getChildCount();
    if (n == 1) {
      return first(ctx);
    }
    
    log("unary-predecrement", ctx);
    DataTypeExpressionAstNode childResult = null;
    for (int i = 0; i < n; i++) {
      ParseTree c = ctx.getChild(i);
      if (c instanceof TerminalNode) {
        continue;
      }
      childResult = c.accept(this);
    }
    
    return ImmutableUnary.builder()
        .pos(createPosition(ctx))
        .type(UnaryType.PREFIX)
        .sign(UnarySign.DEC)
        .value(childResult).build();
  }

  @Override
  public DataTypeExpressionAstNode visitUnaryExpressionNotPlusMinus(UnaryExpressionNotPlusMinusContext ctx) {
    int n = ctx.getChildCount();
    if (n == 1) {
      return first(ctx);
    }
    log("unary-not", ctx);
    DataTypeExpressionAstNode childResult = null;
    for (int i = 0; i < n; i++) {
      ParseTree c = ctx.getChild(i);
      if (c instanceof TerminalNode) {
        continue;
      }
      childResult = c.accept(this);
    }
    return ImmutableUnary.builder()
            .pos(createPosition(ctx))
            .type(UnaryType.POSTFIX)
            .sign(UnarySign.NOT)
            .value(childResult).build();
  }

  @Override
  public DataTypeExpressionAstNode visitPostfixExpression(PostfixExpressionContext ctx) {
    int n = ctx.getChildCount();
    if (n == 1) {
      return first(ctx);
    }
    
    log("postfix", ctx);
    TerminalNode terminalNode = null;
    DataTypeExpressionAstNode childResult = null;
    for (int i = 0; i < n; i++) {
      ParseTree c = ctx.getChild(i);
      if (c instanceof TerminalNode) {
        terminalNode = (TerminalNode) c;
        continue;
      }
      childResult = c.accept(this);
    }
    return ImmutableUnary.builder()
            .pos(createPosition(ctx))
            .type(UnaryType.POSTFIX)
            .sign(terminalNode.getSymbol().getType() == DataTypeParser.INCREMENT ? UnarySign.INC : UnarySign.DEC)
            .value(childResult).build();
  }

  private DataTypeExpressionAstNode first(ParserRuleContext ctx) {
    ParseTree c = ctx.getChild(0);
    return c.accept(this);
  }
  
  public void log(String name, ParseTree context) {
    //System.out.println(name + ": " + context.getText() + ", children: " + context.getChildCount());
  }
  
  private Position createPosition(ParserRuleContext context) {
    Token startToken = context.getStart();
    Token stopToken = context.getStop();
    
    PositionToken start = ImmutablePositionToken.builder()
        .line(startToken.getLine()).col(startToken.getStartIndex())
        .build();
    PositionToken stop = ImmutablePositionToken.builder()
        .line(stopToken.getLine()).col(stopToken.getStopIndex())
        .build();
    
    return ImmutablePosition.builder().start(start).text(context.getText()).stop(stop).build();
  }
}
