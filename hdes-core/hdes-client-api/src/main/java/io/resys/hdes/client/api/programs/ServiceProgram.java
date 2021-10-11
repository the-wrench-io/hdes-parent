package io.resys.hdes.client.api.programs;

import java.io.Serializable;

import org.immutables.value.Value;

import io.resys.hdes.client.api.ast.AstService;
import io.resys.hdes.client.api.ast.AstService.ServiceExecutorType;

@Value.Immutable
public interface ServiceProgram extends Program<AstService> {

  ServiceExecutorType getBean();
  
  @Value.Immutable
  interface ServiceResult extends ProgramResult {
    Serializable getValue();
  }
}
