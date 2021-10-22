package io.resys.hdes.client.spi.groovy;

import io.resys.hdes.client.api.ast.AstService.ServiceExecutorType0;
import io.resys.hdes.client.api.ast.AstService.ServiceExecutorType1;
import io.resys.hdes.client.api.ast.AstService.ServiceExecutorType2;
import io.resys.hdes.client.api.exceptions.ProgramException;
import io.resys.hdes.client.api.programs.ImmutableServiceResult;
import io.resys.hdes.client.api.programs.Program.ProgramContext;
import io.resys.hdes.client.api.programs.ServiceProgram;
import io.resys.hdes.client.api.programs.ServiceProgram.ServiceResult;

public class ServiceProgramExecutor {
  
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static ServiceResult run(ServiceProgram program, ProgramContext context) {

    switch (program.getExecutorType()) {
    case TYPE_0:
      return ImmutableServiceResult.builder()
          .value(((ServiceExecutorType0) program.getBean()).execute())
          .build();
    case TYPE_1:
      return ImmutableServiceResult.builder()
          .value(((ServiceExecutorType1) program.getBean()).execute(context.getValue(program.getTypeDef0())))
          .build();      
    case TYPE_2:
      return ImmutableServiceResult.builder()
          .value(((ServiceExecutorType2) program.getBean()).execute(context.getValue(program.getTypeDef0()), context.getValue(program.getTypeDef1())))
          .build();
    default:
      throw new ProgramException("Can't find/call execute method!");
    }
  }
}
