package io.resys.hdes.ast.spi.visitors.ast;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.immutables.value.Value;

import io.resys.hdes.ast.DecisionTableParser;
import io.resys.hdes.ast.DecisionTableParser.AllContext;
import io.resys.hdes.ast.DecisionTableParser.DescriptionContext;
import io.resys.hdes.ast.DecisionTableParser.DirectionTypeContext;
import io.resys.hdes.ast.DecisionTableParser.DtContext;
import io.resys.hdes.ast.DecisionTableParser.FirstContext;
import io.resys.hdes.ast.DecisionTableParser.HeaderArgsContext;
import io.resys.hdes.ast.DecisionTableParser.HeaderContext;
import io.resys.hdes.ast.DecisionTableParser.HeadersContext;
import io.resys.hdes.ast.DecisionTableParser.HitPolicyContext;
import io.resys.hdes.ast.DecisionTableParser.IdContext;
import io.resys.hdes.ast.DecisionTableParser.LiteralContext;
import io.resys.hdes.ast.DecisionTableParser.MatrixContext;
import io.resys.hdes.ast.DecisionTableParser.RuleEqualityExpressionContext;
import io.resys.hdes.ast.DecisionTableParser.RuleMatchingExpressionContext;
import io.resys.hdes.ast.DecisionTableParser.RuleMatchingOrExpressionContext;
import io.resys.hdes.ast.DecisionTableParser.RuleRelationalExpressionContext;
import io.resys.hdes.ast.DecisionTableParser.RuleUndefinedValueContext;
import io.resys.hdes.ast.DecisionTableParser.RuleValueContext;
import io.resys.hdes.ast.DecisionTableParser.RulesContext;
import io.resys.hdes.ast.DecisionTableParser.RulesetContext;
import io.resys.hdes.ast.DecisionTableParser.RulesetsContext;
import io.resys.hdes.ast.DecisionTableParser.ScalarTypeContext;
import io.resys.hdes.ast.DecisionTableParser.TypeNameContext;
import io.resys.hdes.ast.DecisionTableParserBaseVisitor;
import io.resys.hdes.ast.api.AstNodeException;
import io.resys.hdes.ast.api.nodes.AstNode;
import io.resys.hdes.ast.api.nodes.AstNode.Literal;
import io.resys.hdes.ast.api.nodes.AstNode.ScalarType;
import io.resys.hdes.ast.api.nodes.DecisionTableNode;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.DecisionTableBody;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.DirectionType;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.Header;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.Headers;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.HitPolicy;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.HitPolicyAll;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.HitPolicyFirst;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.HitPolicyMatrix;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.Rule;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.RuleRow;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.RuleValue;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.UndefinedValue;
import io.resys.hdes.ast.api.nodes.ExpressionNode.EqualityType;
import io.resys.hdes.ast.api.nodes.ImmutableAndOperation;
import io.resys.hdes.ast.api.nodes.ImmutableDecisionTableBody;
import io.resys.hdes.ast.api.nodes.ImmutableEmptyNode;
import io.resys.hdes.ast.api.nodes.ImmutableEqualityOperation;
import io.resys.hdes.ast.api.nodes.ImmutableErrorNode;
import io.resys.hdes.ast.api.nodes.ImmutableExpressionValue;
import io.resys.hdes.ast.api.nodes.ImmutableHeader;
import io.resys.hdes.ast.api.nodes.ImmutableHeaderRefValue;
import io.resys.hdes.ast.api.nodes.ImmutableHeaders;
import io.resys.hdes.ast.api.nodes.ImmutableHitPolicyAll;
import io.resys.hdes.ast.api.nodes.ImmutableHitPolicyFirst;
import io.resys.hdes.ast.api.nodes.ImmutableHitPolicyMatrix;
import io.resys.hdes.ast.api.nodes.ImmutableInOperation;
import io.resys.hdes.ast.api.nodes.ImmutableLiteral;
import io.resys.hdes.ast.api.nodes.ImmutableLiteralValue;
import io.resys.hdes.ast.api.nodes.ImmutableNotUnaryOperation;
import io.resys.hdes.ast.api.nodes.ImmutableOrOperation;
import io.resys.hdes.ast.api.nodes.ImmutableRule;
import io.resys.hdes.ast.api.nodes.ImmutableRuleRow;
import io.resys.hdes.ast.api.nodes.ImmutableUndefinedValue;
import io.resys.hdes.ast.spi.visitors.ast.util.Nodes;
import io.resys.hdes.ast.spi.visitors.ast.util.Nodes.TokenIdGenerator;

public class DtParserAstNodeVisitor extends DecisionTableParserBaseVisitor<AstNode> {
  private final TokenIdGenerator tokenIdGenerator;
  private Headers headers;

  public DtParserAstNodeVisitor(TokenIdGenerator tokenIdGenerator) {
    super();
    this.tokenIdGenerator = tokenIdGenerator;
  }

  // Internal only
  @Value.Immutable
  public interface DtRedundentId extends DecisionTableNode {
    String getValue();
  }

  @Value.Immutable
  public interface DtRedundentDescription extends DecisionTableNode {
    String getValue();
  }

  @Value.Immutable
  public interface DtRedundentTypeName extends DecisionTableNode {
    String getValue();
  }

  @Value.Immutable
  public interface RedundentHeaderType extends DecisionTableNode {
    ScalarType getValue();
  }

  @Value.Immutable
  public interface RedundentDirection extends DecisionTableNode {
    DirectionType getValue();
  }

  @Value.Immutable
  public interface RedundentRulesets extends DecisionTableNode {
    List<RuleRow> getRows();
  }

  @Override
  public DecisionTableBody visitDt(DtContext ctx) {
    Nodes children = nodes(ctx);
    this.headers = children.of(Headers.class).get();
    return ImmutableDecisionTableBody.builder()
        .token(token(ctx))
        .id(children.of(DtRedundentId.class).get().getValue())
        .description(children.of(DtRedundentDescription.class).map(e -> e.getValue()))
        .headers(headers)
        .hitPolicy(children.of(HitPolicy.class).get())
        .build();
  }

  @Override
  public Literal visitLiteral(LiteralContext ctx) {
    return literal(ctx, token(ctx));
  }

  @Override
  public UndefinedValue visitRuleUndefinedValue(RuleUndefinedValueContext ctx) {
    return ImmutableUndefinedValue.builder().token(token(ctx)).build();
  }

  @Override
  public AstNode visitRuleValue(RuleValueContext ctx) {
    AstNode node = first(ctx);
    if(node instanceof RuleValue) {
      return node;
      
    } else if(node instanceof Literal) {
      return ImmutableLiteralValue.builder()
          .token(token(ctx))
          .value((Literal) node)
          .build();      
    }
    
    return ImmutableExpressionValue.builder()
      .token(token(ctx))
      .value(ctx.getText())
      .expression(node)
      .build();
  }

  @Override
  public AstNode visitRuleMatchingExpression(RuleMatchingExpressionContext ctx) {
    ParseTree tree = ctx.getChild(0);
    if(tree instanceof TerminalNode && 
        ((TerminalNode) tree).getSymbol().getType() == DecisionTableParser.NOT_OP) {
      return ImmutableNotUnaryOperation.builder()
          .value(nodes(ctx).of(AstNode.class).get())
          .token(token(ctx))
          .build();
    }
    
    return nodes(ctx).of(AstNode.class).get();
  }

  @Override
  public AstNode visitRuleMatchingOrExpression(RuleMatchingOrExpressionContext ctx) {
    if(ctx.getChildCount() == 1) {
      return first(ctx);      
    }
    return ImmutableInOperation.builder()
        .token(token(ctx))
        .values(nodes(ctx).list(Literal.class))
        .build();
  }

  @Override
  public AstNode visitRuleEqualityExpression(RuleEqualityExpressionContext ctx) {
    if(ctx.getChildCount() == 1) {
      return first(ctx);
    }
    
    AstNode left = ctx.getChild(0).accept(this);
    TerminalNode v = (TerminalNode) ctx.getChild(1);
    AstNode right = ctx.getChild(2).accept(this);
    
    return v.getSymbol().getType() == DecisionTableParser.AND ? 
        ImmutableAndOperation.builder().token(token(ctx)).left(left).right(right).build() : 
        ImmutableOrOperation.builder().token(token(ctx)).left(left).right(right).build();
  }

  @Override
  public AstNode visitRuleRelationalExpression(RuleRelationalExpressionContext ctx) {
    String v = ctx.getChild(0).getText();
    AstNode right = ctx.getChild(1).accept(this);
    
    EqualityType type;
    if (v.equals(EqualityType.NOTEQUAL.getValue())) {
      type = EqualityType.NOTEQUAL;
      
    } else if(v.equals(EqualityType.EQUAL.getValue())) {
      type = EqualityType.EQUAL;
    
    } else if(v.equals(EqualityType.GREATER.getValue())) {
      type = EqualityType.GREATER;
    
    } else if(v.equals(EqualityType.GREATER_THEN.getValue())) {
      type = EqualityType.GREATER_THEN;
    
    } else if(v.equals(EqualityType.LESS.getValue())) {
      type = EqualityType.LESS;
    
    } else if(v.equals(EqualityType.LESS_THEN.getValue())) {
      type = EqualityType.LESS_THEN;
    
    } else {
      throw new IllegalArgumentException("Not implemented equality type: " + v + "!");
    }
    
    AstNode.Token token = token(ctx);
    return ImmutableEqualityOperation.builder()
      .type(type)
      .token(token)
      .left(ImmutableHeaderRefValue.builder().token(token).build())
      .right(right)    
      .build();
  }

  @Override
  public RedundentHeaderType visitScalarType(ScalarTypeContext ctx) {
    return ImmutableRedundentHeaderType.builder()
        .token(token(ctx))
        .value(ScalarType.valueOf(ctx.getText()))
        .build();
  }

  @Override
  public HitPolicy visitHitPolicy(HitPolicyContext ctx) {
    return nodes(ctx).of(HitPolicy.class).get();
  }

  @Override
  public HitPolicyFirst visitFirst(FirstContext ctx) {
    List<RuleRow> rulesets = nodes(ctx).of(RedundentRulesets.class).map(e -> e.getRows()).orElse(Collections.emptyList());
    return ImmutableHitPolicyFirst.builder()
        .token(token(ctx))
        .rows(rulesets)
        .build();
  }

  @Override
  public HitPolicyAll visitAll(AllContext ctx) {
    List<RuleRow> rulesets = nodes(ctx).of(RedundentRulesets.class).map(e -> e.getRows()).orElse(Collections.emptyList());
    return ImmutableHitPolicyAll.builder()
        .token(token(ctx))
        .rows(rulesets)
        .build();
  }

  @Override
  public HitPolicyMatrix visitMatrix(MatrixContext ctx) {
    List<RuleRow> rulesets = nodes(ctx).of(RedundentRulesets.class).map(e -> e.getRows()).orElse(Collections.emptyList());
    return ImmutableHitPolicyMatrix.builder()
        .token(token(ctx))
        .rows(rulesets)
        .build();
  }

  @Override
  public RedundentRulesets visitRulesets(RulesetsContext ctx) {
    return ImmutableRedundentRulesets.builder()
        .token(token(ctx))
        .rows(nodes(ctx).list(RuleRow.class).stream()
            .filter(r -> !r.getRules().isEmpty())
            .collect(Collectors.toList()))
        .build();
  }

  @Override
  public RuleRow visitRuleset(RulesetContext ctx) {
    if (ctx.getChildCount() == 3) {
      return (RuleRow) ctx.getChild(1).accept(this);
    }
    
    return ImmutableRuleRow.builder()
        .token(token(ctx))
        .text(ctx.getStart().getInputStream().getText(new Interval(ctx.start.getStartIndex(), ctx.stop.getStopIndex())))
        .build();
  }

  @Override
  public RuleRow visitRules(RulesContext ctx) {
    List<Rule> rules = new ArrayList<>();
    int n = ctx.getChildCount();
    int header = 0;
    for (int i = 0; i < n; i++) {
      ParseTree c = ctx.getChild(i);
      if (c instanceof TerminalNode) {
        continue;
      }
      RuleValue childResult = (RuleValue) c.accept(this);
      rules.add(ImmutableRule.builder()
          .token(childResult.getToken())
          .header(header++)
          .value(childResult)
          .build());
    }
    
    return ImmutableRuleRow.builder()
        .token(token(ctx))
        .text(ctx.getStart().getInputStream().getText(new Interval(ctx.start.getStartIndex(), ctx.stop.getStopIndex())))
        .rules(rules)
        .build();
  }

  @Override
  public AstNode visitHeaders(HeadersContext ctx) {
    return nodes(ctx).of(Headers.class)
        .orElse(ImmutableHeaders.builder().token(token(ctx)).build());
  }

  @Override
  public Headers visitHeaderArgs(HeaderArgsContext ctx) {
    return ImmutableHeaders.builder()
        .token(token(ctx))
        .values(nodes(ctx).list(Header.class))
        .build();
  }

  @Override
  public Header visitHeader(HeaderContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableHeader.builder()
        .token(token(ctx))
        .name(nodes.of(DtRedundentTypeName.class).get().getValue())
        .type(nodes.of(RedundentHeaderType.class).get().getValue())
        .direction(nodes.of(RedundentDirection.class).get().getValue())
        .build();
  }

  @Override
  public RedundentDirection visitDirectionType(DirectionTypeContext ctx) {
    return ImmutableRedundentDirection.builder()
        .token(token(ctx))
        .value(DirectionType.valueOf(ctx.getText()))
        .build();
  }

  @Override
  public DtRedundentId visitId(IdContext ctx) {
    return ImmutableDtRedundentId.builder()
        .token(token(ctx))
        .value(nodes(ctx).of(DtRedundentTypeName.class).get().getValue())
        .build();
  }

  @Override
  public DtRedundentDescription visitDescription(DescriptionContext ctx) {
    return ImmutableDtRedundentDescription.builder()
        .token(token(ctx))
        .value(nodes(ctx).of(Literal.class).get().getValue())
        .build();
  }

  @Override
  public DtRedundentTypeName visitTypeName(TypeNameContext ctx) {
    return ImmutableDtRedundentTypeName.builder()
        .token(token(ctx))
        .value(ctx.getText())
        .build();
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

  private Literal literal(ParserRuleContext ctx, AstNode.Token token) {
    String value = ctx.getText();
    ScalarType type = null;
    TerminalNode terminalNode = (TerminalNode) ctx.getChild(0);
    switch (terminalNode.getSymbol().getType()) {
    case DecisionTableParser.StringLiteral:
      type = ScalarType.STRING;
      value = Nodes.getStringLiteralValue(ctx);
      break;
    case DecisionTableParser.BooleanLiteral:
      type = ScalarType.BOOLEAN;
      break;
    case DecisionTableParser.DecimalLiteral:
      type = ScalarType.DECIMAL;
      break;
    case DecisionTableParser.IntegerLiteral:
      type = ScalarType.INTEGER;
      value = value.replaceAll("_", "");
      break;
    default:
      throw new AstNodeException(Arrays.asList(ImmutableErrorNode.builder()
          .message("Unknown literal: " + ctx.getText() + "!")
          .target(ImmutableEmptyNode.builder().value(ctx.getText()).token(token).build())
          .build()));
    }
    return ImmutableLiteral.builder()
        .token(token)
        .type(type)
        .value(value)
        .build();
  }
}
