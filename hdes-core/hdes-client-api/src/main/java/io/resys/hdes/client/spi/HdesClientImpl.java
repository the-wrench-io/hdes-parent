package io.resys.hdes.client.spi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
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
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.resys.hdes.client.api.HdesAstTypes;
import io.resys.hdes.client.api.HdesClient;
import io.resys.hdes.client.api.HdesStore;
import io.resys.hdes.client.api.ast.AstBody.AstBodyType;
import io.resys.hdes.client.api.ast.AstCommand;
import io.resys.hdes.client.api.ast.AstCommand.AstCommandValue;
import io.resys.hdes.client.api.ast.AstDecision;
import io.resys.hdes.client.api.ast.AstFlow;
import io.resys.hdes.client.api.ast.AstService;
import io.resys.hdes.client.api.ast.ImmutableAstCommand;
import io.resys.hdes.client.api.programs.DecisionProgram;
import io.resys.hdes.client.api.programs.DecisionProgram.DecisionResult;
import io.resys.hdes.client.api.programs.FlowProgram;
import io.resys.hdes.client.api.programs.FlowProgram.FlowResult;
import io.resys.hdes.client.api.programs.FlowProgram.FlowResultLog;
import io.resys.hdes.client.api.programs.Program.ProgramSupplier;
import io.resys.hdes.client.api.programs.ProgramEnvir;
import io.resys.hdes.client.api.programs.ServiceProgram;
import io.resys.hdes.client.api.programs.ServiceProgram.ServiceResult;
import io.resys.hdes.client.spi.HdesTypeDefsFactory.ServiceInit;
import io.resys.hdes.client.spi.config.HdesClientConfig;
import io.resys.hdes.client.spi.config.HdesClientConfig.AstFlowNodeVisitor;
import io.resys.hdes.client.spi.decision.DecisionCSVBuilder;
import io.resys.hdes.client.spi.decision.DecisionProgramBuilder;
import io.resys.hdes.client.spi.decision.DecisionProgramExecutor;
import io.resys.hdes.client.spi.envir.EnvirFactory;
import io.resys.hdes.client.spi.envir.ImmutableEvirBuilderSourceEntity;
import io.resys.hdes.client.spi.flow.FlowProgramBuilder;
import io.resys.hdes.client.spi.flow.FlowProgramExecutor;
import io.resys.hdes.client.spi.groovy.ServiceProgramBuilder;
import io.resys.hdes.client.spi.groovy.ServiceProgramExecutor;
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
  public EnvirBuilder envir() {
    EnvirFactory factory = new EnvirFactory(ast, defs);
    return new EnvirBuilder() {
      @Override
      public EnvirCommandFormatBuilder addCommand() {
        return new EnvirCommandFormatBuilder() {
          private String id;
          private AstBodyType type;
          private String commandJson;
          @Override
          public EnvirCommandFormatBuilder id(String externalId) {
            this.id = externalId;
            return this;
          }
          @Override
          public EnvirCommandFormatBuilder service(String commandJson) {
            this.type = AstBodyType.FLOW_TASK;
            this.commandJson = commandJson;
            return null;
          }
          @Override
          public EnvirCommandFormatBuilder flow(String commandJson) {
            this.type = AstBodyType.FLOW;
            this.commandJson = commandJson;
            return this;
          }
          @Override
          public EnvirCommandFormatBuilder decision(String commandJson) {
            this.type = AstBodyType.DT;
            this.commandJson = commandJson;
            return this;
          }
          @Override
          public void build() {
            HdesAssert.notNull(id, () -> "id must be defined!");
            HdesAssert.notNull(commandJson, () -> "commandJson must be defined!");
            factory.add(ImmutableEvirBuilderSourceEntity.builder()
                .externalId(id)
                .bodyType(type)
                .commands(defs.commandsList(commandJson))
                .build());
          }
        };
      }
      @Override
      public ProgramEnvir build() {
        return factory.build();
      }
    };
  }

  @Override
  public AstBuilder ast() {
    return new AstBuilder() {
      private Integer version;
      private final List<AstCommand> commands = new ArrayList<>();
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
      public AstBuilder commands(String src) {
        this.json = defs.commandsJson(src);
        return this;
      }
      @Override
      public AstBuilder syntax(InputStream syntax) {
        try {
          BufferedReader br = new BufferedReader(new InputStreamReader(syntax));
          String line;
          int index = 0;
          while ((line = br.readLine()) != null) {
            commands.add(ImmutableAstCommand.builder()
                .id(String.valueOf(index++))
                .type(AstCommandValue.ADD)
                .value(line)
                .build());
          }
          return this;
        } catch(IOException e) {
          throw new RuntimeException(e.getMessage(), e);
        } finally {
          try {
            syntax.close();
          } catch(IOException e) {}
        }
      }
      @Override
      public AstBuilder commands(List<AstCommand> src, Integer version) {
        this.commands.addAll(src);
        this.version = version;
        return this;
      }
      @Override
      public AstBuilder commands(ArrayNode src, Integer version) {
        this.json = src;
        this.version = version;
        return this;
      }
      @Override
      public AstBuilder commands(List<AstCommand> src) {
        return commands(src, null);
      }
      @Override
      public AstBuilder commands(ArrayNode src) {
        return commands(src, null);
      }
    };
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
  public ExecutorBuilder executor() {
    return new ExecutorBuilder() {
      private final ImmutableProgramContext.Builder data = ImmutableProgramContext.builder(defs);
      @Override
      public ExecutorBuilder inputField(String name, Serializable value) {
        return inputMap(Map.of(name, value));
      }
      @Override
      public ExecutorBuilder inputMap(Map<String, Serializable> input) {
        this.data.map(input);
        return this;
      }
      @Override
      public ExecutorBuilder input(ExecutorInput input) {
        this.data.callback(input);
        return this;
      }
      @Override
      public ExecutorBuilder inputList(List<Object> inputObject) {
        this.data.entity(inputObject);
        return this;
      }
      @Override
      public ExecutorBuilder inputEntity(Object inputObject) {
        this.data.entity(inputObject);
        return this;
      }
      @Override
      public ExecutorBuilder inputJson(JsonNode inputObject) {
        this.data.entity(inputObject);
        return this;
      }
      @Override
      public ServiceExecutor service(ServiceProgram program) {
        return new ServiceExecutor() {
          @Override
          public ServiceResult andGetBody() {
            return ServiceProgramExecutor.run(program, data.build());
          }
        };
      }
      @Override
      public FlowExecutor flow(FlowProgram program) {
        return new FlowExecutor() {
          @Override
          public FlowResultLog andGetTask(String task) {
            return new FlowProgramExecutor(program, data.build(), defs).run().getLogs().stream()
                .filter(t -> t.getStepId().equals(task)).findFirst().orElse(null);
          }
          @Override
          public FlowResult andGetBody() {
            return new FlowProgramExecutor(program, data.build(), defs).run();
          }
        };
      }
      @Override
      public DecisionExecutor decision(DecisionProgram program) {
        return new DecisionExecutor() {
          @Override
          public DecisionResult andGetBody() {
            return DecisionProgramExecutor.run(program, data.build());
          }
          @Override
          public Map<String, Serializable> andGet() {
            return DecisionProgramExecutor.get(andGetBody());
          }
          @Override
          public List<Map<String, Serializable>> andFind() {
            return DecisionProgramExecutor.find(andGetBody());
          }
        };
      }
    };
  }

  @Override
  public HdesStore store() {
    // TODO Auto-generated method stub
    return null;
  }
  @Override
  public HdesAstTypes astTypes() {
    return ast;
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
    private ProgramSupplier programSupplier;
    private final List<AstFlowNodeVisitor> flowVisitors = new ArrayList<>();

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
    public Builder programSupplier(ProgramSupplier programSupplier) {
      this.programSupplier = programSupplier;
      return this;
    }
    public Builder store(HdesStore store) {
      this.store = store;
      return this;
    }
    public HdesClientImpl build() {
      HdesAssert.notNull(objectMapper, () -> "objectMapper must be defined!");
      HdesAssert.notNull(serviceInit, () -> "serviceInit must be defined!");
      HdesAssert.notNull(programSupplier, () -> "programSupplier must be defined!");
      final var config = new HdesClientConfigImpl(flowVisitors, programSupplier);
      final var types = new HdesTypeDefsFactory(objectMapper, serviceInit, config);
      final var ast = new HdesAstTypesImpl(objectMapper, serviceInit, config);
      return new HdesClientImpl(types, store, ast, config);
    }
  }

  private static class HdesClientConfigImpl implements HdesClientConfig {
    private final List<AstFlowNodeVisitor> flowVisitors = new ArrayList<>();
    private final ProgramSupplier programs;
    public HdesClientConfigImpl(List<AstFlowNodeVisitor> flowVisitors, ProgramSupplier programs) {
      this.flowVisitors.addAll(flowVisitors);
      this.programs = programs;
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
    @Override
    public ProgramSupplier getPrograms() {
      return programs;
    }
  }
}
