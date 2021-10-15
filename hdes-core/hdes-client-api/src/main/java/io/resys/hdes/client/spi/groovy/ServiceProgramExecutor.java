package io.resys.hdes.client.spi.groovy;

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

import java.util.stream.Collectors;

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
      throw new ProgramException("Service: '" + program.getId() + "' failure. Can't find/call execute method!");
    }
  }
}
