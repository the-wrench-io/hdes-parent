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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import io.resys.hdes.ast.HdesLexer;
import io.resys.hdes.ast.HdesParser;
import io.resys.hdes.ast.api.AstEnvir;
import io.resys.hdes.ast.api.AstNodeException;
import io.resys.hdes.ast.api.nodes.AstNode.BodyNode;
import io.resys.hdes.ast.api.nodes.AstNode.ErrorNode;
import io.resys.hdes.ast.api.nodes.ImmutableEmptyBodyNode;
import io.resys.hdes.ast.api.nodes.ImmutableToken;
import io.resys.hdes.ast.spi.errors.HdesAntlrErrorListener;
import io.resys.hdes.ast.spi.visitors.ast.HdesParserAstNodeVisitor;
import io.resys.hdes.ast.spi.visitors.ast.util.Nodes.TokenIdGenerator;

public class ImmutableAstEnvir implements AstEnvir {
  
  private final Map<String, BodyNode> body;
  private final Map<String, String> src;
  private final Map<String, List<ErrorNode>> errors;

  public ImmutableAstEnvir(Map<String, BodyNode> body, Map<String, String> src,  Map<String, List<ErrorNode>> errors) {
    super();
    this.body = body;
    this.src = src;
    this.errors = errors;
  }
  @Override
  public Map<String, BodyNode> getBody() {
    return body;
  }
  @Override
  public BodyNode getBody(String id) {
    if(!body.containsKey(id)) {
      throw new AstNodeException("No node by id: " + id + "!");
    }
    return body.get(id);
  }
  @Override
  public String getSrc(String id) {
    if(!src.containsKey(id)) {
      throw new AstNodeException("No node by id: " + id + "!");
    }
    return src.get(id);
  }
  @Override
  public List<ErrorNode> getErrors(String id) {
    if(!src.containsKey(id)) {
      throw new AstNodeException("No node by id: " + id + "!");
    }
    return errors.get(id);
  }
  @Override
  public Map<String, List<ErrorNode>> getErrors() {
    return errors;
  }
  
  public static Builder builder() {
    return new GenericBuilder();
  }
  
  public static class GenericBuilder implements Builder {

    private final Map<String, BodyNode> body = new HashMap<>();
    private final Map<String, String> src = new HashMap<>();
    private final Map<String, List<ErrorNode>> errors = new HashMap<>();
    private final List<String> toBeRemoved = new ArrayList<>();
    private boolean ignoreErrors;
    private AstEnvir from;
    
    @Override
    public Builder ignoreErrors() {
      this.ignoreErrors = true;
      return this;
    }
    @Override
    public Builder delete(String id) {
      this.toBeRemoved.add(id);
      return this;
    }
    @Override
    public Builder from(AstEnvir envir) {
      this.from = envir;
      return this;
    }
    @Override
    public AstEnvir build() {
      if(from != null) {
        from.getBody().keySet().stream()
        .filter(id -> !toBeRemoved.contains(id))
        .filter(id -> !src.containsKey(id))
        .forEach(id -> add().externalId(id).src(from.getSrc(id)));
      }
      if(!ignoreErrors) {
        List<ErrorNode> errors = new ArrayList<>();
        this.errors.values().forEach(v -> errors.addAll(v));
        if(!errors.isEmpty()) {
          throw new AstNodeException(errors);
        }
      }
      
      AstEnvir result = new ImmutableAstEnvir(
          Collections.unmodifiableMap(body), 
          Collections.unmodifiableMap(src),
          Collections.unmodifiableMap(errors));
      
      // TODO :: Post processing > 
      // * validations(data types, refs)
      // * data type conversions
      
      
      return result;
    }

    @Override
    public SourceBuilder<Builder> add() {
      Builder result = this;
      return new GenericSourceBuilder() {
        private final HdesAntlrErrorListener errorListener = new HdesAntlrErrorListener();
        private String value;
        
        @Override
        protected Builder parent(BodyNode node) {
          String id = externalId == null ? node.getId() : externalId;
          body.put(id, node);
          src.put(id, value);
          errors.put(id, Collections.unmodifiableList(errorListener.getErrors()));
          return result;
        }
        @Override
        public Builder src(String value) {
          this.value = value;
          
          return super.src(value);
        }
        @Override
        protected HdesAntlrErrorListener errorListener() {
          return errorListener;
        }
        
      };
    } 
  }
  
  public static abstract class GenericSourceBuilder implements SourceBuilder<Builder> {
    
    protected String externalId;
    protected abstract HdesAntlrErrorListener errorListener();
    protected abstract Builder parent(BodyNode node);
    
    @Override
    public SourceBuilder<Builder> externalId(String externalId) {
      this.externalId = externalId;
      return this;
    }
    @Override
    public Builder src(String src) {
      HdesLexer lexer = new HdesLexer(CharStreams.fromString(src));
      CommonTokenStream tokens = new CommonTokenStream(lexer);
      HdesParser parser = new HdesParser(tokens);
      parser.addErrorListener(errorListener());
      ParseTree tree = parser.hdesBody();
      
      BodyNode result;
      try {
        result = (BodyNode) tree.accept(new HdesParserAstNodeVisitor(new TokenIdGenerator()));
      } catch(Exception e) {
        result = ImmutableEmptyBodyNode.builder().id(externalId).token(ImmutableToken.builder()
            .id(0)
            .startCol(0).startLine(0)
            .endCol(0).endLine(0)
            .text(e.getMessage())
            .build()).build();
      }
      return parent(result);
    }
  }
}
