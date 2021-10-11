package io.resys.hdes.client.spi.groovy;

import java.util.stream.Collectors;

import io.resys.hdes.client.api.ast.AstService.ServiceExecutorType0;
import io.resys.hdes.client.api.ast.AstService.ServiceExecutorType1;
import io.resys.hdes.client.api.ast.AstService.ServiceExecutorType2;
import io.resys.hdes.client.api.exceptions.ServiceProgramException;
import io.resys.hdes.client.api.programs.ImmutableServiceResult;
import io.resys.hdes.client.api.programs.Program.ProgramContext;
import io.resys.hdes.client.api.programs.ServiceProgram;
import io.resys.hdes.client.api.programs.ServiceProgram.ServiceResult;

public class ServiceProgramExecutor {
  
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static ServiceResult run(ServiceProgram program, ProgramContext context) {
    final var inputs = program.getAst().getHeaders().getAcceptDefs().stream()
      .sorted((p1, p2) -> Integer.compare(p1.getOrder(), p2.getOrder()))
      .collect(Collectors.toList());

    switch (program.getAst().getExecutorType()) {
    case TYPE_0:
      return ImmutableServiceResult.builder()
          .value(((ServiceExecutorType0) program.getBean()).execute())
          .build();
    case TYPE_1:
      return ImmutableServiceResult.builder()
          .value(((ServiceExecutorType1) program.getBean()).execute(context.getValue(inputs.get(0))))
          .build();      
    case TYPE_2:
      return ImmutableServiceResult.builder()
          .value(((ServiceExecutorType2) program.getBean()).execute(context.getValue(inputs.get(0)), context.getValue(inputs.get(1))))
          .build();
    default:
      throw new ServiceProgramException("Service: '" + program.getId() + "' failure. Can't find/call execute method!");
    }
  }
}
