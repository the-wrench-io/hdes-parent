package io.resys.hdes.client.spi;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import io.resys.hdes.client.api.ast.TypeDef;
import io.resys.hdes.client.api.programs.DecisionProgram;
import io.resys.hdes.client.api.programs.FlowProgram;
import io.resys.hdes.client.api.programs.Program.ProgramContext;
import io.resys.hdes.client.api.programs.ServiceProgram;

public class ImmutableProgramContext implements ProgramContext {

  private final Map<String, Object> entity; 
  public ImmutableProgramContext(Map<String, Object> entity) {
    super();
    this.entity = entity;
  }

  @Override
  public Serializable getValue(TypeDef typeDef) {
    return (Serializable) entity.get(typeDef.getName());
  }
  @Override
  public FlowProgram getFlowProgram(String name) {
    throw new RuntimeException("must impl");
  }
  @Override
  public DecisionProgram getDecisionProgram(String name) {
    throw new RuntimeException("must impl");
  }
  @Override
  public ServiceProgram getServiceProgram(String name) {
    throw new RuntimeException("must impl");
  }
  
  public static Builder builder(HdesTypeDefsFactory factory) {
    return new Builder(factory);
  }
  
  public static class Builder {
    private final HdesTypeDefsFactory factory;
    private final Map<String, Object> entity = new HashMap<>();
    public Builder(HdesTypeDefsFactory factory) {
      super();
      this.factory = factory;
    }
    public Builder map(Map<String, Object> entity) {
      this.entity.putAll(entity);
      return this;
    }
    public Builder entity(Object entity) {
      this.entity.putAll(this.factory.toMap(entity));
      return this;
    }
    public Builder json(JsonNode json) {
      this.entity.putAll(this.factory.toMap(json));
      return this;
    }
    public ImmutableProgramContext build() {
      return new ImmutableProgramContext(entity);
    }
  }
}
