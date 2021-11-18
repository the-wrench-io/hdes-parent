package io.resys.hdes.client.spi;

import java.util.ArrayList;
import java.util.Arrays;

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

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.client.api.HdesAstTypes;
import io.resys.hdes.client.api.HdesClient;
import io.resys.hdes.client.api.HdesStore;
import io.resys.hdes.client.api.ast.AstDecision;
import io.resys.hdes.client.api.ast.AstFlow;
import io.resys.hdes.client.api.ast.AstService;
import io.resys.hdes.client.api.programs.DecisionProgram;
import io.resys.hdes.client.api.programs.FlowProgram;
import io.resys.hdes.client.api.programs.ProgramEnvir;
import io.resys.hdes.client.api.programs.ServiceProgram;
import io.resys.hdes.client.spi.HdesTypeDefsFactory.ServiceInit;
import io.resys.hdes.client.spi.config.HdesClientConfig;
import io.resys.hdes.client.spi.config.HdesClientConfig.AstFlowNodeVisitor;
import io.resys.hdes.client.spi.decision.DecisionCSVBuilder;
import io.resys.hdes.client.spi.decision.DecisionProgramBuilder;
import io.resys.hdes.client.spi.envir.ProgramEnvirFactory;
import io.resys.hdes.client.spi.flow.FlowProgramBuilder;
import io.resys.hdes.client.spi.flow.validators.IdValidator;
import io.resys.hdes.client.spi.groovy.ServiceProgramBuilder;
import io.resys.hdes.client.spi.util.HdesAssert;

public class HdesClientImpl implements HdesClient {

  private final HdesTypeDefsFactory defs;
  private final HdesAstTypes ast;
  private final HdesStore store;
  private final HdesClientConfig config;
  
  public HdesClientImpl(HdesTypeDefsFactory types, HdesStore store, HdesAstTypes ast, HdesClientConfig config) {
    super();
    this.defs = types;
    this.store = store;
    this.ast = ast;
    this.config = config;
  }
  @Override
  public ExecutorBuilder executor(ProgramEnvir envir) {
    return new HdesClientExecutorBuilder(envir, defs);    
  }
  @Override
  public EnvirBuilder envir() {
    ProgramEnvirFactory factory = new ProgramEnvirFactory(ast, defs, null);
    return new HdesClientEnvirBuilder(factory, defs);
  }
  @Override
  public AstBuilder ast() {
    return new HdesClientAstBuilder(defs, ast);
  }
  @Override
  public HdesStore store() {
    return store;
  }
  @Override
  public HdesAstTypes types() {
    return ast;
  }
  @Override
  public ProgramBuilder program() {
    return new ProgramBuilder() {
      @Override
      public ServiceProgram ast(AstService ast) {
        return new ServiceProgramBuilder(defs).build(ast);
      }
      @Override
      public DecisionProgram ast(AstDecision ast) {
        return new DecisionProgramBuilder(defs).build(ast);
      }
      @Override
      public FlowProgram ast(AstFlow ast) {
        return new FlowProgramBuilder(defs).build(ast);
      }
    };
  }
  @Override
  public CSVBuilder csv() {
    return new CSVBuilder() {
      @Override
      public String ast(AstDecision ast) {
        return DecisionCSVBuilder.build(ast);
      }
    };
  }
  
  public HdesClientConfig config() {
    return this.config;
  }
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static class Builder {  
    private ObjectMapper objectMapper;
    private ServiceInit serviceInit;
    private HdesStore store;
    private final List<AstFlowNodeVisitor> flowVisitors = new ArrayList<>(Arrays.asList(new IdValidator()));

    public Builder flowVisitors(AstFlowNodeVisitor ...visitors) {
      this.flowVisitors.addAll(Arrays.asList(visitors));
      return this;
    }
    public Builder objectMapper(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
      return this;
    }
    public Builder serviceInit(ServiceInit serviceInit) {
      this.serviceInit = serviceInit;
      return this;
    }
    public Builder store(HdesStore store) {
      this.store = store;
      return this;
    }
    public HdesClientImpl build() {
      HdesAssert.notNull(objectMapper, () -> "objectMapper must be defined!");
      HdesAssert.notNull(serviceInit, () -> "serviceInit must be defined!");
      final var config = new HdesClientConfigImpl(flowVisitors);
      final var types = new HdesTypeDefsFactory(objectMapper, serviceInit, config);
      final var ast = new HdesAstTypesImpl(objectMapper, serviceInit, config);
      return new HdesClientImpl(types, store, ast, config);
    }
  }

  private static class HdesClientConfigImpl implements HdesClientConfig {
    private final List<AstFlowNodeVisitor> flowVisitors = new ArrayList<>();
    public HdesClientConfigImpl(List<AstFlowNodeVisitor> flowVisitors) {
      this.flowVisitors.addAll(flowVisitors);
    }
    @Override
    public List<AstFlowNodeVisitor> getFlowVisitors() {
      return flowVisitors;
    }
    @Override
    public HdesClientConfig config(AstFlowNodeVisitor... changes) {
      this.flowVisitors.addAll(Arrays.asList(changes));
      return this;
    }
  }
}
