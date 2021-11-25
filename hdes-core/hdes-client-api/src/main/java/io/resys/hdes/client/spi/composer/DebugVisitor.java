package io.resys.hdes.client.spi.composer;

import java.io.Serializable;
import java.util.Map;

import io.resys.hdes.client.api.HdesClient;
import io.resys.hdes.client.api.HdesComposer.DebugRequest;
import io.resys.hdes.client.api.HdesComposer.DebugResponse;
import io.resys.hdes.client.api.HdesStore.StoreState;
import io.resys.hdes.client.api.ImmutableDebugResponse;
import io.resys.hdes.client.api.exceptions.HdesBadRequestException;
import io.resys.hdes.client.api.programs.ProgramEnvir;
import io.resys.hdes.client.api.programs.ProgramEnvir.ProgramStatus;
import io.resys.hdes.client.api.programs.ProgramEnvir.ProgramWrapper;
import io.resys.hdes.client.spi.util.HdesAssert;

public class DebugVisitor {
  private final HdesClient client;
  
  public DebugVisitor(HdesClient client) {
    super();
    this.client = client;
  }

  public DebugResponse visit(DebugRequest entity, StoreState state) {
    HdesAssert.isTrueOrBadRequest(entity.getInput() != null || entity.getInputCSV() != null, () -> "input or inputCSV must be defined!");
    HdesAssert.isTrueOrBadRequest(entity.getInput() != null && entity.getInputCSV() != null, () -> "input and inputCSV can't be both defined!");
    
    final var envir = ComposerEntityMapper.toEnvir(client.envir(), state).build();
    final var runnable = envir.getValues().get(entity.getId());
    HdesAssert.notFound(runnable, () -> "Entity was not found by id: '"  + entity.getId() + "'!");
    HdesAssert.isTrueOrBadRequest(runnable.getStatus() == ProgramStatus.UP, () -> "Program status: '" + runnable.getStatus() + "' is not runnable!");
    
    if(entity.getInputCSV() != null) {
      final var csv =  new DebugCSVVisitor(client, runnable, envir).visit(entity.getInputCSV());
      return ImmutableDebugResponse.builder().body(csv).build();
    }
    
    final var input = client.mapper().toMap(entity.getInput());
    final var json = visitProgram(input, runnable, envir);
    return ImmutableDebugResponse.builder().body(json).build();
  }
  
  
  private String visitProgram(Map<String, Serializable> input, ProgramWrapper<?, ?> wrapper, ProgramEnvir envir) {
    switch (wrapper.getType()) {
    case FLOW: return visitFlow(input, wrapper, envir);
    case FLOW_TASK: return visitFlowTask(input, wrapper, envir);
    case DT: return visitDecision(input, wrapper, envir);
    default: throw new HdesBadRequestException("Can't debug: '" + wrapper.getType() + "'!");
    }
  }
  
  
  private String visitFlow(Map<String, Serializable> input, ProgramWrapper<?, ?> wrapper, ProgramEnvir envir) {
    final var body = client.executor(envir).inputMap(input).flow(wrapper.getId()).andGetBody(); 
    return client.mapper().toJson(body);
  }
  
  private String visitFlowTask(Map<String, Serializable> input, ProgramWrapper<?, ?> wrapper, ProgramEnvir envir) {
    final var body = client.executor(envir).inputMap(input).service(wrapper.getId()).andGetBody();
    return client.mapper().toJson(body);
  }
  
  private String visitDecision(Map<String, Serializable> input, ProgramWrapper<?, ?> wrapper, ProgramEnvir envir) {
    final var body = client.executor(envir).inputMap(input).decision(wrapper.getId()).andGetBody();
    return client.mapper().toJson(body);
  }
}
