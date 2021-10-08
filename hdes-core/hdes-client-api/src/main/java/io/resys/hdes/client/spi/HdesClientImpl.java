package io.resys.hdes.client.spi;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.resys.hdes.client.api.HdesAstTypes;
import io.resys.hdes.client.api.HdesClient;
import io.resys.hdes.client.api.HdesStore;
import io.resys.hdes.client.api.ast.AstCommand;
import io.resys.hdes.client.api.ast.AstDecision;
import io.resys.hdes.client.api.ast.AstFlow;
import io.resys.hdes.client.api.ast.AstService;
import io.resys.hdes.client.api.execution.DecisionProgram;
import io.resys.hdes.client.api.execution.FlowProgram;
import io.resys.hdes.client.spi.decision.DecisionProgramBuilder;
import io.resys.hdes.client.spi.flow.FlowProgramBuilder;
import io.resys.hdes.client.spi.util.HdesAssert;

public class HdesClientImpl implements HdesClient {

  private final HdesTypeDefsFactory types;
  private final HdesAstTypes ast;
  private final HdesStore store;
  
  public HdesClientImpl(HdesTypeDefsFactory types, HdesStore store, HdesAstTypes ast) {
    super();
    this.types = types;
    this.store = store;
    this.ast = ast;
  }
  @Override
  public AstBuilder ast() {
    return new AstBuilder() {
      private String syntax;
      private Integer version;
      private List<AstCommand> commands;
      private ArrayNode json;
      @Override
      public AstService service() {
        return ast.service().src(json).src(commands).rev(version).build();
      }
      @Override
      public AstFlow flow() {
        return ast.flow().src(json).src(commands).rev(version).build();
      }
      @Override
      public AstDecision decision() {
        return ast.decision().src(json).src(commands).rev(version).build();
      }
      @Override
      public AstBuilder syntax(String syntax) {
        this.syntax = syntax;
        return this;
      }
      @Override
      public AstBuilder commands(List<AstCommand> src, Integer version) {
        this.commands = src;
        this.version = version;
        return this;
      }
      @Override
      public AstBuilder commands(ArrayNode src, Integer version) {
        this.json = src;
        this.version = version;
        return this;
      }
    };
  }
  @Override
  public ProgramBuilder program() {
    return new ProgramBuilder() {
      @Override
      public AstService ast(AstService ast) {
        return null;
      }
      @Override
      public DecisionProgram ast(AstDecision ast) {
        return new DecisionProgramBuilder(types).build(ast);
      }
      @Override
      public FlowProgram ast(AstFlow ast) {
        return new FlowProgramBuilder(types).build(ast);
      }
    };
  }

  @Override
  public ExecutorBuilder executor() {
    // TODO Auto-generated method stub
    return null;
  }

  
  @Override
  public HdesStore store() {
    // TODO Auto-generated method stub
    return null;
  }
  
  public static class Builder {  
    private ObjectMapper objectMapper;
    private HdesStore store;
    
    public Builder objectMapper(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
      return this;
    }
    public Builder store(HdesStore store) {
      this.store = store;
      return this;
    }
    public HdesClientImpl build() {
      HdesAssert.notNull(objectMapper, () -> "ObjectMapper must be defined!");
      final var types = new HdesTypeDefsFactory(objectMapper);
      final var ast = new HdesAstTypesImpl(objectMapper);
      return new HdesClientImpl(types, store, ast);
    }
  }
}
