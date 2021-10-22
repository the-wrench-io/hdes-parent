package io.resys.hdes.client.spi.envir;

import java.util.Arrays;
import java.util.List;

import org.immutables.value.Value;

import io.resys.hdes.client.api.HdesAstTypes;
import io.resys.hdes.client.api.ast.AstBody.AstBodyType;
import io.resys.hdes.client.api.ast.AstCommand;
import io.resys.hdes.client.api.ast.AstDecision;
import io.resys.hdes.client.api.ast.AstFlow;
import io.resys.hdes.client.api.ast.AstService;
import io.resys.hdes.client.api.programs.DecisionProgram;
import io.resys.hdes.client.api.programs.FlowProgram;
import io.resys.hdes.client.api.programs.ImmutableProgramEnvir;
import io.resys.hdes.client.api.programs.ImmutableProgramError;
import io.resys.hdes.client.api.programs.ImmutableProgramWrapper;
import io.resys.hdes.client.api.programs.ProgramEnvir;
import io.resys.hdes.client.api.programs.ProgramEnvir.ProgramError;
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
    AstFlow ast = null;
    try {
      ast = hdesTypes.flow().src(src.getCommands()).build();
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
  
  private List<ProgramError> visitException(Exception e) {
    return Arrays.asList(ImmutableProgramError.builder()
          .id("exception")
          .msg(e.getMessage())
          .exception(e)
          .build()
        );
  }
}
