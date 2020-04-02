package io.resys.hdes.ast.spi.visitors.ast;

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
    Token stopToken = context.getStop();
    
    
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
      if (type == ScalarType.STRING) {
        value = value.substring(1, value.length() - 1);
      }
      return ImmutableLiteral.builder()
          .token(token)
          .type(type)
          .value(value)
          .build();
  }
  
  
  public static class TokenIdGenerator {
    private int current = 1;
    
    public int next() {
      return current++;
    }
  }
}
