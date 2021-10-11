package io.resys.hdes.client.spi.groovy;

import io.resys.hdes.client.api.ast.AstService;
import io.resys.hdes.client.api.ast.AstService.ServiceExecutorType;
import io.resys.hdes.client.api.programs.ImmutableServiceProgram;
import io.resys.hdes.client.api.programs.ServiceProgram;
import io.resys.hdes.client.spi.HdesTypeDefsFactory;

public class ServiceProgramBuilder {
  private final HdesTypeDefsFactory typesFactory;

  public ServiceProgramBuilder(HdesTypeDefsFactory typesFactory) {
    super();
    this.typesFactory = typesFactory;
  }
  public ServiceProgram build(AstService ast) {
    final ServiceExecutorType executable = typesFactory.getServiceInit().get(ast.getBeanType());
    
    return ImmutableServiceProgram.builder()
        .id(ast.getName())
        .ast(ast)
        .bean(executable)
        .build();
  }
}
