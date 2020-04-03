package io.resys.hdes.ast.spi.visitors.ast.util;

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
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import io.resys.hdes.ast.ExpressionParser;
import io.resys.hdes.ast.api.AstNodeException;
import io.resys.hdes.ast.api.nodes.AstNode;
import io.resys.hdes.ast.api.nodes.AstNode.Literal;
import io.resys.hdes.ast.api.nodes.AstNode.ScalarType;
import io.resys.hdes.ast.api.nodes.ImmutableLiteral;
import io.resys.hdes.ast.api.nodes.ImmutableToken;

public class Nodes {
  private final List<Object> results;
  
  public Nodes(List<Object> results) {
    super();
    this.results = results;
  }

  @SuppressWarnings("unchecked")
  public <T> Optional<T> of(Class<T> clazz) {
    for(Object entry :results) {
      if(clazz.isAssignableFrom(entry.getClass()) ) {
        return Optional.of((T) entry);
      }
    }
    return Optional.empty();
  }

  @SuppressWarnings("unchecked")
  public <T> List<T> list(Class<T> clazz) {
    List<T> result = new ArrayList<>();
    for(Object entry : results) {
      if(clazz.isAssignableFrom(entry.getClass()) ) {
        result.add((T) entry);
      }
    }
    return result;
  }
  
  public static Nodes from(ParserRuleContext node, AbstractParseTreeVisitor<?> visitor) {
    List<Object> results = new ArrayList<>();
    int n = node.getChildCount();
    for (int i = 0; i < n; i++) {
      ParseTree c = node.getChild(i);
      Object childResult = c.accept(visitor);
      if (childResult != null) {
        results.add(childResult);
      }
    }
    return new Nodes(results);
  }
 
  public static AstNode.Token token(ParserRuleContext context, TokenIdGenerator idGen) {
    Token startToken = context.getStart();
    //Token stopToken = context.getStop();
    return ImmutableToken.builder()
        .id(idGen.next())
        .line(startToken.getLine())
        .col(startToken.getStartIndex())
        .text(context.getText()).build();
  }
 
  public static Literal literal(ParserRuleContext ctx, AstNode.Token token) {
      String value = ctx.getText();
      ScalarType type = null;
      TerminalNode terminalNode = (TerminalNode) ctx.getChild(0);
      switch (terminalNode.getSymbol().getType()) {
      case ExpressionParser.StringLiteral:
        type = ScalarType.STRING;
        value = getStringLiteralValue(ctx);
        break;
      case ExpressionParser.BooleanLiteral:
        type = ScalarType.BOOLEAN;
        break;
      case ExpressionParser.DecimalLiteral:
        type = ScalarType.DECIMAL;
        break;
      case ExpressionParser.IntegerLiteral:
        type = ScalarType.INTEGER;
        value = value.replaceAll("_", "");
        break;
      default:
        throw new AstNodeException("Unknown literal: " + ctx.getText() + "!");
      }

      return ImmutableLiteral.builder()
          .token(token)
          .type(type)
          .value(value)
          .build();
  }
  
  public static String getStringLiteralValue(ParserRuleContext ctx) {
    String value = ctx.getText();
    return value.substring(1, value.length() - 1);
  }
  
  public static class TokenIdGenerator {
    private int current = 1;
    
    public int next() {
      return current++;
    }
  }
}
