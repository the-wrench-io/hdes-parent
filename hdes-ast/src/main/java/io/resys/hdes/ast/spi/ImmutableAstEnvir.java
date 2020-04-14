package io.resys.hdes.ast.spi;

import java.util.ArrayList;

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

import java.util.List;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import io.resys.hdes.ast.DecisionTableParser;
import io.resys.hdes.ast.ExpressionParser;
import io.resys.hdes.ast.FlowParser;
import io.resys.hdes.ast.HdesLexer;
import io.resys.hdes.ast.ManualTaskParser;
import io.resys.hdes.ast.api.AstEnvir;
import io.resys.hdes.ast.api.AstNodeException;
import io.resys.hdes.ast.api.nodes.AstNode;
import io.resys.hdes.ast.api.nodes.AstNode.ScalarType;
import io.resys.hdes.ast.spi.errors.AntlrErrorListener;
import io.resys.hdes.ast.spi.visitors.ast.DtParserAstNodeVisitor;
import io.resys.hdes.ast.spi.visitors.ast.EnParserAstNodeVisitor;
import io.resys.hdes.ast.spi.visitors.ast.FwParserAstNodeVisitor;
import io.resys.hdes.ast.spi.visitors.ast.MtParserAstNodeVisitor;
import io.resys.hdes.ast.spi.visitors.ast.util.Nodes.TokenIdGenerator;
import io.resys.hdes.ast.spi.visitors.loggers.DtParserConsoleVisitor;
import io.resys.hdes.ast.spi.visitors.loggers.ExpressionParserConsoleVisitor;
import io.resys.hdes.ast.spi.visitors.loggers.FlowParserConsoleVisitor;
import io.resys.hdes.ast.spi.visitors.loggers.ManualTaskParserConsoleVisitor;

public class ImmutableAstEnvir implements AstEnvir {
  
  private final List<AstNode> nodes;

  public ImmutableAstEnvir(List<AstNode> nodes) {
    super();
    this.nodes = nodes;
  }
  @Override
  public List<AstNode> getValues() {
    return nodes;
  } 
  
  public static Builder builder() {
    return new GenericBuilder();
  }
  
  public static class GenericBuilder implements Builder {

    private final AntlrErrorListener errorListener = new AntlrErrorListener();
    private final List<AstNode> nodes = new ArrayList<>();
    @Override
    public Builder from(AstEnvir envir) {
      // TODO Auto-generated method stub
      return this;
    }

    @Override
    public AstEnvir build() {
      if(!errorListener.getErrors().isEmpty()) {
        throw new AstNodeException(errorListener.getErrors());
      }
      AstEnvir result = new ImmutableAstEnvir(nodes);
      
      // TODO :: Post processing > 
      // * validations(data types, refs)
      // * data type conversions
      
      
      return result;
    }

    @Override
    public SourceBuilder<Builder> add() {
      Builder result = this;
      return new GenericSourceBuilder() {
        @Override
        protected Builder parent(AstNode node) {
          nodes.add(node);
          return result;
        }
        @Override
        protected ANTLRErrorListener errorListener() {
          return errorListener;
        }
      };
    } 
  }
  
  public static abstract class GenericSourceBuilder implements SourceBuilder<Builder> {
    protected abstract ANTLRErrorListener errorListener();
    protected abstract Builder parent(AstNode node);
    
    
    @Override
    public SourceBuilder<Builder> externalId(String externalId) {
      // TODO Auto-generated method stub
      return this;
    }

    @Override
    public Builder flow(String src) {
      HdesLexer lexer = new HdesLexer(CharStreams.fromString(src));
      CommonTokenStream tokens = new CommonTokenStream(lexer);
      FlowParser parser = new FlowParser(tokens);
      parser.addErrorListener(errorListener());
      ParseTree tree = parser.flow();
      tree.accept(new FlowParserConsoleVisitor());
      return parent(tree.accept(new FwParserAstNodeVisitor(new TokenIdGenerator())));
    }

    @Override
    public Builder expression(String src, ScalarType type) {
      HdesLexer lexer = new HdesLexer(CharStreams.fromString(src));
      CommonTokenStream tokens = new CommonTokenStream(lexer);
      ExpressionParser parser = new ExpressionParser(tokens);
      parser.addErrorListener(errorListener());
      ParseTree tree = parser.compilationUnit();
      tree.accept(new ExpressionParserConsoleVisitor());
      return parent(tree.accept(new EnParserAstNodeVisitor(new TokenIdGenerator(), type)));
    }

    @Override
    public Builder decisionTable(String src) {
      HdesLexer lexer = new HdesLexer(CharStreams.fromString(src));
      CommonTokenStream tokens = new CommonTokenStream(lexer);
      DecisionTableParser parser = new DecisionTableParser(tokens);
      parser.addErrorListener(errorListener());
      ParseTree tree = parser.dt();
      tree.accept(new DtParserConsoleVisitor());
      return parent(tree.accept(new DtParserAstNodeVisitor(new TokenIdGenerator())));
    }

    @Override
    public Builder manualTask(String src) {
      HdesLexer lexer = new HdesLexer(CharStreams.fromString(src));
      CommonTokenStream tokens = new CommonTokenStream(lexer);
      ManualTaskParser parser = new ManualTaskParser(tokens);
      parser.addErrorListener(errorListener());
      ParseTree tree = parser.mt();
      tree.accept(new ManualTaskParserConsoleVisitor());
      return parent(tree.accept(new MtParserAstNodeVisitor(new TokenIdGenerator())));
    }

  }
}
