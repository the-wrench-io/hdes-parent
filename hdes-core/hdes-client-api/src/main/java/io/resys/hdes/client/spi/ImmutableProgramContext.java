package io.resys.hdes.client.spi;

/*-
 * #%L
 * hdes-client-api
 * %%
 * Copyright (C) 2020 - 2021 Copyright 2020 ReSys OÜ
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.fasterxml.jackson.databind.JsonNode;

import io.resys.hdes.client.api.HdesClient.ExecutorInput;
import io.resys.hdes.client.api.ast.TypeDef;
import io.resys.hdes.client.api.programs.DecisionProgram;
import io.resys.hdes.client.api.programs.FlowProgram;
import io.resys.hdes.client.api.programs.Program.ProgramContext;
import io.resys.hdes.client.api.programs.ServiceData;
import io.resys.hdes.client.api.programs.ServiceProgram;

public class ImmutableProgramContext implements ProgramContext {

  private final HdesTypeDefsFactory factory;
  private final List<Supplier<Map<String, Object>>> suppliers;
  private final Object serviceData;
  private final ExecutorInput input;
  private Map<String, Object> entity;
  
  public ImmutableProgramContext(List<Supplier<Map<String, Object>>> inputs, Object serviceData, ExecutorInput input, HdesTypeDefsFactory factory) {
    super();
    this.suppliers = inputs;
    this.input = input;
    this.factory = factory;
    this.serviceData = serviceData;
  }

  @Override
  public Serializable getValue(TypeDef typeDef) {
    // data class map compatible
    if(input != null) {
      return (Serializable) input.apply(typeDef);
    }
    
    if(typeDef.getData() && serviceData != null) {
      return (Serializable) factory.toType(serviceData, typeDef.getBeanType());
    }
    
    if(entity == null) {
      entity = new HashMap<>();
      suppliers.forEach(e -> entity.putAll(e.get()));
    }
    
    if(typeDef.getData() && typeDef.getBeanType() != null) {
      return (Serializable) factory.toType(entity, typeDef.getBeanType());
    }
    
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
    private final List<Supplier<Map<String, Object>>> suppliers = new ArrayList<>();
    private ExecutorInput input;
    private Object serviceData;
    
    public Builder(HdesTypeDefsFactory factory) {
      super();
      this.factory = factory;
    }
    public Builder callback(ExecutorInput input) {
      this.input = input;
      return this;
    }
    public Builder map(Map<String, Object> entity) {
      this.suppliers.add(() -> entity);
      return this;
    }
    public Builder entity(Object entity) {
      if(entity.getClass().isAnnotationPresent(ServiceData.class)) {
        serviceData = entity;
      } else {
        this.suppliers.add(() -> this.factory.toMap(entity));        
      }
      return this;
    }
    public Builder json(JsonNode json) {
      this.suppliers.add(() -> this.factory.toMap(json));
      return this;
    }
    public ImmutableProgramContext build() {
      return new ImmutableProgramContext(suppliers, serviceData, input, factory);
    }
  }
}
