package io.resys.hdes.client.spi.envir;

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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.immutables.value.Value;

import io.resys.hdes.client.api.HdesAstTypes;
import io.resys.hdes.client.api.ast.AstBody.AstBodyType;
import io.resys.hdes.client.api.ast.AstCommand;
import io.resys.hdes.client.api.ast.AstDecision;
import io.resys.hdes.client.api.ast.AstFlow;
import io.resys.hdes.client.api.ast.AstFlow.FlowCommandMessageType;
import io.resys.hdes.client.api.ast.AstService;
import io.resys.hdes.client.api.programs.DecisionProgram;
import io.resys.hdes.client.api.programs.FlowProgram;
import io.resys.hdes.client.api.programs.ImmutableProgramEnvir;
import io.resys.hdes.client.api.programs.ImmutableProgramMessage;
import io.resys.hdes.client.api.programs.ImmutableProgramWrapper;
import io.resys.hdes.client.api.programs.ProgramEnvir;
import io.resys.hdes.client.api.programs.ProgramEnvir.ProgramMessage;
import io.resys.hdes.client.api.programs.ProgramEnvir.ProgramStatus;
import io.resys.hdes.client.api.programs.ProgramEnvir.ProgramWrapper;
import io.resys.hdes.client.api.programs.ServiceProgram;
import io.resys.hdes.client.spi.HdesTypeDefsFactory;
import io.resys.hdes.client.spi.decision.DecisionProgramBuilder;
import io.resys.hdes.client.spi.flow.FlowProgramBuilder;
import io.resys.hdes.client.spi.groovy.ServiceProgramBuilder;

public class EnvirFactory {

  private final HdesAstTypes hdesTypes;
  private final HdesTypeDefsFactory hdesFactory;
  private final AssociationVisitor tree = new AssociationVisitor(); 
  
  public EnvirFactory(HdesAstTypes hdesTypes, HdesTypeDefsFactory hdesFactory) {
    super();
    this.hdesTypes = hdesTypes;
    this.hdesFactory = hdesFactory;
  }
  
  @Value.Immutable
  public interface EvirBuilderSourceEntity {
    String getExternalId();
    AstBodyType getBodyType();
    List<AstCommand> getCommands();
  }
  
  public EnvirFactory add(EvirBuilderSourceEntity entity) {
    final var wrapper = visitSource(entity);
    tree.add(entity, wrapper);
    return this;
  }
  
  public ProgramEnvir build() {
    final var envir = ImmutableProgramEnvir.builder();
    tree.build().forEach(e -> envir.putValues(e.getId(), e));
    return envir.build();
  }

  private ProgramWrapper<?, ?> visitSource(EvirBuilderSourceEntity entity) {
    switch (entity.getBodyType()) {
    case DT: return visitDecision(entity);
    case FLOW: return visitFlow(entity);
    case FLOW_TASK: return visitFlowTask(entity);
    default: throw new IllegalArgumentException("unknown command format type: '" + entity.getBodyType() + "'!");
    }
  }

  private ProgramWrapper<AstDecision, DecisionProgram> visitDecision(EvirBuilderSourceEntity src) {
    final ImmutableProgramWrapper.Builder<AstDecision, DecisionProgram> builder = ImmutableProgramWrapper.builder();
    builder.status(ProgramStatus.UP);
    AstDecision ast = null;
    try {
      ast = hdesTypes.decision().src(src.getCommands()).build();
    } catch(Exception e) {
      builder.status(ProgramStatus.AST_ERROR).addAllErrors(visitException(e));
    }
    
    DecisionProgram program = null;
    if(ast != null) {
      try {
        program = new DecisionProgramBuilder(hdesFactory).build(ast);
      } catch(Exception e) {
        builder.status(ProgramStatus.PROGRAM_ERROR).addAllErrors(visitException(e));
      }
    }
    return builder.id(src.getExternalId()).ast(ast).program(program).build();
  }
  
  private ProgramWrapper<AstFlow, FlowProgram> visitFlow(EvirBuilderSourceEntity src) {
    final ImmutableProgramWrapper.Builder<AstFlow, FlowProgram> builder = ImmutableProgramWrapper.builder();
    builder.status(ProgramStatus.UP);
    
    AstFlow ast = null;
    try {
      ast = hdesTypes.flow().src(src.getCommands()).build();
      final var errors = ast.getMessages().stream()
        .filter(m -> m.getType() == FlowCommandMessageType.ERROR)
        .map(error -> ImmutableProgramMessage.builder()
            .id("ast-error")
            .msg("line: " + error.getLine() + ": " + error.getValue())
            .build())
        .collect(Collectors.toList());
      builder.addAllErrors(errors);
      
      if(!errors.isEmpty()) {
        builder.status(ProgramStatus.AST_ERROR);
      }
      
    } catch(Exception e) {
      builder.status(ProgramStatus.AST_ERROR).addAllErrors(visitException(e));
    }
    
    FlowProgram program = null;
    if(ast != null) {
      try {
        program = new FlowProgramBuilder(hdesFactory).build(ast);
      } catch(Exception e) {
        builder.status(ProgramStatus.PROGRAM_ERROR).addAllErrors(visitException(e));
      }
    }
    return builder.id(src.getExternalId()).ast(ast).program(program).build(); 
  }
  
  private ProgramWrapper<AstService, ServiceProgram> visitFlowTask(EvirBuilderSourceEntity src) {
    final ImmutableProgramWrapper.Builder<AstService, ServiceProgram> builder = ImmutableProgramWrapper.builder();
    builder.status(ProgramStatus.UP);
    AstService ast = null;
    try {
      ast = hdesTypes.service().src(src.getCommands()).build();
    } catch(Exception e) {
      builder.status(ProgramStatus.AST_ERROR).addAllErrors(visitException(e));
    }
    
    ServiceProgram program = null;
    if(ast != null) {
      try {
        program = new ServiceProgramBuilder(hdesFactory).build(ast);
      } catch(Exception e) {
        builder.status(ProgramStatus.PROGRAM_ERROR).addAllErrors(visitException(e));
      }
    }
    return builder.id(src.getExternalId()).ast(ast).program(program).build(); 
  }
  
  private List<ProgramMessage> visitException(Exception e) {
    return Arrays.asList(ImmutableProgramMessage.builder()
          .id("exception")
          .msg(e.getMessage())
          .exception(e)
          .build()
        );
  }
}
