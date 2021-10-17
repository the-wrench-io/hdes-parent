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
import io.resys.hdes.client.api.programs.ImmutableProgramContextNamedValue;
import io.resys.hdes.client.api.programs.Program.ProgramContext;
import io.resys.hdes.client.api.programs.Program.ProgramContextNamedValue;
import io.resys.hdes.client.api.programs.Program.ProgramSupplier;
import io.resys.hdes.client.api.programs.ServiceData;

public class ImmutableProgramContext implements ProgramContext {

  private final HdesTypeDefsFactory factory;

  private final ExecutorInput callbackThatWillSupplyAllData;
  
  // data object that should be directly transformed to target
  private final Object serviceData;
  
  // generic data to transform to target
  private Map<String, Serializable> genericData;
  
  // generic data sources that will be used for init of genericData
  private final List<Supplier<Map<String, Serializable>>> suppliers;
  
  public ImmutableProgramContext(List<Supplier<Map<String, Serializable>>> inputs, Object serviceData, ExecutorInput input, HdesTypeDefsFactory factory) {
    super();
    this.suppliers = inputs;
    this.callbackThatWillSupplyAllData = input;
    this.factory = factory;
    this.serviceData = serviceData;
  }

  @Override
  public Serializable getValue(TypeDef typeDef) {
    // data class map compatible
    if(callbackThatWillSupplyAllData != null) {
      Serializable target = (Serializable) callbackThatWillSupplyAllData.apply(typeDef);
      if(target != null) {
        return target;
      }
    }
    
    if(typeDef.getData() && serviceData != null) {
      return (Serializable) factory.toType(serviceData, typeDef.getBeanType());
    }
    
    init();
    
    if(typeDef.getData() && typeDef.getBeanType() != null) {
      return (Serializable) factory.toType(genericData, typeDef.getBeanType());
    }
    
    return (Serializable) genericData.get(typeDef.getName());
  }
  
  @Override
  public ProgramContextNamedValue getValue(String typeDefName) {
    init();
    
    if(genericData.containsKey(typeDefName)) {
      return ImmutableProgramContextNamedValue.builder().value((Serializable) genericData.get(typeDefName)).found(true).build();
    }
    
    return ImmutableProgramContextNamedValue.builder().found(false).build();
  }
  
  private void init() {
    if(genericData == null) {
      genericData = new HashMap<>();
      suppliers.forEach(e -> genericData.putAll(e.get()));
    }
  }

  public static Builder builder(HdesTypeDefsFactory factory) {
    return new Builder(factory);
  }
  
  public static class Builder {
    private final HdesTypeDefsFactory factory;
    private final List<Supplier<Map<String, Serializable>>> suppliers = new ArrayList<>();
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
    public Builder map(Map<String, Serializable> entity) {
      this.suppliers.add(() -> entity);
      return this;
    }
    public Builder entity(Object entity) {
      if(entity.getClass().isAnnotationPresent(ServiceData.class)) {
        serviceData = entity;
      } else if(entity instanceof Map) {
        this.suppliers.add(() -> (Map) entity);
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

  @Override
  public ProgramSupplier getPrograms() {
    return factory.config().getPrograms();
  }
}
