package io.resys.hdes.client.spi.groovy;

import java.util.stream.Collectors;

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
    
    final var inputs = ast.getHeaders().getAcceptDefs().stream()
        .sorted((p1, p2) -> Integer.compare(p1.getOrder(), p2.getOrder()))
        .collect(Collectors.toList());

    final var program = ImmutableServiceProgram.builder().bean(executable).executorType(ast.getExecutorType());
    switch (ast.getExecutorType()) {
      case TYPE_1:
        program.typeDef0(inputs.get(0));
        break;
      case TYPE_2:
        program.typeDef0(inputs.get(0)).typeDef1(inputs.get(1));
        break;
      default: break;
    } 
    return program.build();
  }
}
