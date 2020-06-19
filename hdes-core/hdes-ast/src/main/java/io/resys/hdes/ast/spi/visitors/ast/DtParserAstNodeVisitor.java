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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.immutables.value.Value;

import io.resys.hdes.ast.HdesParser;
import io.resys.hdes.ast.HdesParser.AllContext;
import io.resys.hdes.ast.HdesParser.DtBodyContext;
import io.resys.hdes.ast.HdesParser.FirstContext;
import io.resys.hdes.ast.HdesParser.HitPolicyContext;
import io.resys.hdes.ast.HdesParser.MatrixContext;
import io.resys.hdes.ast.HdesParser.MatrixRuleContext;
import io.resys.hdes.ast.HdesParser.MatrixRulesContext;
import io.resys.hdes.ast.HdesParser.MatrixRulesetContext;
import io.resys.hdes.ast.HdesParser.MatrixRulesetsContext;
import io.resys.hdes.ast.HdesParser.RuleEqualityExpressionContext;
import io.resys.hdes.ast.HdesParser.RuleMatchingExpressionContext;
import io.resys.hdes.ast.HdesParser.RuleMatchingOrExpressionContext;
import io.resys.hdes.ast.HdesParser.RuleRelationalExpressionContext;
import io.resys.hdes.ast.HdesParser.RuleUnaryExpressionContext;
import io.resys.hdes.ast.HdesParser.RuleUndefinedValueContext;
import io.resys.hdes.ast.HdesParser.RuleValueContext;
import io.resys.hdes.ast.HdesParser.RulesContext;
import io.resys.hdes.ast.HdesParser.RulesetContext;
import io.resys.hdes.ast.HdesParser.RulesetsContext;
import io.resys.hdes.ast.api.AstNodeException;
import io.resys.hdes.ast.api.nodes.AstNode;
import io.resys.hdes.ast.api.nodes.AstNode.Headers;
import io.resys.hdes.ast.api.nodes.AstNode.Literal;
import io.resys.hdes.ast.api.nodes.AstNode.TypeName;
import io.resys.hdes.ast.api.nodes.DecisionTableNode;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.DecisionTableBody;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.HitPolicy;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.HitPolicyAll;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.HitPolicyFirst;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.HitPolicyMatrix;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.MatrixRow;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.Rule;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.RuleRow;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.RuleValue;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.UndefinedValue;
import io.resys.hdes.ast.api.nodes.ExpressionNode.EqualityType;
import io.resys.hdes.ast.api.nodes.ImmutableAndOperation;
import io.resys.hdes.ast.api.nodes.ImmutableBetweenExpression;
import io.resys.hdes.ast.api.nodes.ImmutableDecisionTableBody;
import io.resys.hdes.ast.api.nodes.ImmutableEqualityOperation;
import io.resys.hdes.ast.api.nodes.ImmutableExpressionValue;
import io.resys.hdes.ast.api.nodes.ImmutableHeaderRefValue;
import io.resys.hdes.ast.api.nodes.ImmutableHitPolicyAll;
import io.resys.hdes.ast.api.nodes.ImmutableHitPolicyFirst;
import io.resys.hdes.ast.api.nodes.ImmutableHitPolicyMatrix;
import io.resys.hdes.ast.api.nodes.ImmutableInOperation;
import io.resys.hdes.ast.api.nodes.ImmutableLiteralValue;
import io.resys.hdes.ast.api.nodes.ImmutableMatrixRow;
import io.resys.hdes.ast.api.nodes.ImmutableNegateLiteralValue;
import io.resys.hdes.ast.api.nodes.ImmutableNotUnaryOperation;
import io.resys.hdes.ast.api.nodes.ImmutableOrOperation;
import io.resys.hdes.ast.api.nodes.ImmutableRule;
import io.resys.hdes.ast.api.nodes.ImmutableRuleRow;
import io.resys.hdes.ast.api.nodes.ImmutableUndefinedValue;
import io.resys.hdes.ast.spi.visitors.ast.HdesParserAstNodeVisitor.RedundentDescription;
import io.resys.hdes.ast.spi.visitors.ast.HdesParserAstNodeVisitor.RedundentScalarType;
import io.resys.hdes.ast.spi.visitors.ast.util.Nodes;
import io.resys.hdes.ast.spi.visitors.ast.util.Nodes.TokenIdGenerator;

public class DtParserAstNodeVisitor extends EnParserAstNodeVisitor {
  private Headers headers;

  public DtParserAstNodeVisitor(TokenIdGenerator tokenIdGenerator) {
    super(tokenIdGenerator);
  }

  @Value.Immutable
  public interface DtRedundentRulesets extends DecisionTableNode {
    List<RuleRow> getRows();
  }
  
  @Value.Immutable
  public interface DtRedundentMatrixRules extends DecisionTableNode {
    List<Literal> getValues();
  }
  @Value.Immutable
  public interface DtRedundentMatrixRows extends DecisionTableNode {
    List<MatrixRow> getValues();
  }  
  

  @Override
  public DecisionTableBody visitDtBody(DtBodyContext ctx) {
    Nodes children = nodes(ctx);
    this.headers = children.of(Headers.class).get();
    return ImmutableDecisionTableBody.builder()
        .token(token(ctx))
        .id(children.of(TypeName.class).get())
        .description(children.of(RedundentDescription.class).map(e -> e.getValue()))
        .headers(headers)
        .hitPolicy(children.of(HitPolicy.class).get())
        .build();
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
        ((TerminalNode) tree).getSymbol().getType() == HdesParser.NOT_OP) {
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
    
    ParseTree first = ctx.getChild(0);
    if (first instanceof TerminalNode &&
        ((TerminalNode) first).getSymbol().getType() == HdesParser.BETWEEN) {      
      AstNode.Token token = token(ctx);
      return ImmutableBetweenExpression.builder()
          .token(token)
          .value(ImmutableHeaderRefValue.builder().token(token).build())
          .left(ctx.getChild(1).accept(this)).right(ctx.getChild(3).accept(this))
          .build();
    }
    
    AstNode left = first.accept(this);
    TerminalNode v = (TerminalNode) ctx.getChild(1);
    AstNode right = ctx.getChild(2).accept(this);
    
    return v.getSymbol().getType() == HdesParser.AND ? 
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
      // TODO:: error handling
      throw new AstNodeException("Not implemented equality type: " + v + "!");
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
  public HitPolicy visitHitPolicy(HitPolicyContext ctx) {
    return nodes(ctx).of(HitPolicy.class).get();
  }

  @Override
  public HitPolicyFirst visitFirst(FirstContext ctx) {
    List<RuleRow> rulesets = nodes(ctx).of(DtRedundentRulesets.class).map(e -> e.getRows()).orElse(Collections.emptyList());
    return ImmutableHitPolicyFirst.builder()
        .token(token(ctx))
        .rows(rulesets)
        .build();
  }

  @Override
  public HitPolicyAll visitAll(AllContext ctx) {
    List<RuleRow> rulesets = nodes(ctx).of(DtRedundentRulesets.class).map(e -> e.getRows()).orElse(Collections.emptyList());
    return ImmutableHitPolicyAll.builder()
        .token(token(ctx))
        .rows(rulesets)
        .build();
  }

  @Override
  public HitPolicyMatrix visitMatrix(MatrixContext ctx) {
    AstNode.ScalarType from = ((RedundentScalarType) ctx.getChild(2).accept(this)).getValue();
    AstNode.ScalarType to = ((RedundentScalarType) ctx.getChild(4).accept(this)).getValue();
    List<Rule> rules = new ArrayList<>();
    List<MatrixRow> rows = new ArrayList<>();
    
    for (int i = 5; i < ctx.getChildCount(); i++) {
      ParseTree c = ctx.getChild(i);
      if (c instanceof TerminalNode) {
        continue;
      }
      
      AstNode node = c.accept(this);
      if(node instanceof RuleRow) {
        rules.addAll(((RuleRow) node).getRules()); 
      } else if(node instanceof DtRedundentMatrixRows) {
        rows.addAll(((DtRedundentMatrixRows) node).getValues());
      }
    }
    return ImmutableHitPolicyMatrix.builder()
        .token(token(ctx))
        .fromType(from)
        .toType(to)
        .rules(rules)
        .rows(rows)
        .build();
  }
  
  @Override
  public DtRedundentMatrixRows visitMatrixRulesets(MatrixRulesetsContext ctx) {
    return ImmutableDtRedundentMatrixRows.builder()
        .token(token(ctx))
        .values(nodes(ctx).list(MatrixRow.class))
        .build();
  }
  
  @Override
  public MatrixRow visitMatrixRuleset(MatrixRulesetContext ctx) {
    // type name : { rules }
    TypeName typeName = (TypeName) ctx.getChild(0).accept(this);
    List<Literal> values = new ArrayList<>();
    
    for (int i = 1; i < ctx.getChildCount(); i++) {
      ParseTree c = ctx.getChild(i);
      if (c instanceof TerminalNode) {
        continue;
      }
      
      DtRedundentMatrixRules node = (DtRedundentMatrixRules) c.accept(this);
      values.addAll(node.getValues());
    }
    return ImmutableMatrixRow.builder().token(token(ctx)).typeName(typeName).values(values).build();
  }

  @Override
  public AstNode visitMatrixRules(MatrixRulesContext ctx) {
    return ImmutableDtRedundentMatrixRules.builder()
        .token(token(ctx))
        .values(nodes(ctx).list(Literal.class))
        .build();
  }
  
  @Override
  public AstNode visitMatrixRule(MatrixRuleContext ctx) {
    return nodes(ctx).of(Literal.class).get();
  }
  
  @Override
  public DtRedundentRulesets visitRulesets(RulesetsContext ctx) {
    return ImmutableDtRedundentRulesets.builder()
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
  public AstNode visitRuleUnaryExpression(RuleUnaryExpressionContext ctx) {
    Nodes nodes = nodes(ctx);
    Literal literal = nodes.of(Literal.class).get();
    if(ctx.getChildCount() == 1) {
      return literal;
    }
    return ImmutableNegateLiteralValue.builder()
        .token(token(ctx))
        .value(literal)
        .build();
  }
}
