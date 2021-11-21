package io.resys.hdes.client.spi;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import io.resys.hdes.client.api.HdesClient.DecisionExecutor;
import io.resys.hdes.client.api.HdesClient.ExecutorBuilder;
import io.resys.hdes.client.api.HdesClient.ExecutorInput;
import io.resys.hdes.client.api.HdesClient.FlowExecutor;
import io.resys.hdes.client.api.HdesClient.HdesTypesMapper;
import io.resys.hdes.client.api.HdesClient.ServiceExecutor;
import io.resys.hdes.client.api.programs.DecisionProgram;
import io.resys.hdes.client.api.programs.DecisionProgram.DecisionResult;
import io.resys.hdes.client.api.programs.FlowProgram;
import io.resys.hdes.client.api.programs.FlowProgram.FlowResult;
import io.resys.hdes.client.api.programs.FlowProgram.FlowResultLog;
import io.resys.hdes.client.api.programs.ProgramEnvir;
import io.resys.hdes.client.api.programs.ProgramEnvir.ProgramStatus;
import io.resys.hdes.client.api.programs.ProgramEnvir.ProgramWrapper;
import io.resys.hdes.client.api.programs.ServiceProgram;
import io.resys.hdes.client.api.programs.ServiceProgram.ServiceResult;
import io.resys.hdes.client.spi.decision.DecisionProgramExecutor;
import io.resys.hdes.client.spi.flow.FlowProgramExecutor;
import io.resys.hdes.client.spi.groovy.ServiceProgramExecutor;
import io.resys.hdes.client.spi.util.HdesAssert;

public class HdesClientExecutorBuilder implements ExecutorBuilder {
  private final ProgramEnvir envir;
  private final HdesTypesMapper defs;
  private final ImmutableProgramContext.Builder data;
  
  public HdesClientExecutorBuilder(ProgramEnvir envir, HdesTypesMapper defs) {
    this.envir = envir;
    this.defs = defs;
    this.data = ImmutableProgramContext.builder(defs, envir);
  }
  
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
  public ServiceExecutor service(String nameOrId) {
    return new ServiceExecutor() {
      @Override
      public ServiceResult andGetBody() { 
        final ServiceProgram program = getProgram(nameOrId, envir.getServicesByName());
        return ServiceProgramExecutor.run(program, data.build());
      }
    };
  }
  @Override
  public FlowExecutor flow(String nameOrId) {
    final FlowProgram program = getProgram(nameOrId, envir.getFlowsByName());
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
  public DecisionExecutor decision(String nameOrId) {
    DecisionProgram program = getProgram(nameOrId, envir.getDecisionsByName());
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
  
  @SuppressWarnings("unchecked")
  private <T> T getProgram(String nameOrId, Map<String, ? extends ProgramWrapper<?, ?>> src) {
    HdesAssert.isTrue(nameOrId != null && !nameOrId.isBlank(), () -> "nameOrId must be defined!");
    ProgramWrapper<?, ?> wrapperByNameOrId = src.get(nameOrId);
    if(wrapperByNameOrId == null) {
      wrapperByNameOrId = this.envir.getValues().get(nameOrId);
    }
    
    ProgramWrapper<?, ?> wrapper = wrapperByNameOrId;
    HdesAssert.isTrue(wrapper != null, () -> "Can't find program by nameOrId: '" + nameOrId + "', names: [" + String.join(", ", src.keySet())  + "]!");
    HdesAssert.isTrue(wrapper.getStatus() == ProgramStatus.UP, () -> "Can't run program by name/id: '" + nameOrId + "' because program status is: '" + wrapper.getStatus() + "'!");
    return (T) wrapper.getProgram().get();
  }
}
