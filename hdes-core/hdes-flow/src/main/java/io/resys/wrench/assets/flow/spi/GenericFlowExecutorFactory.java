package io.resys.wrench.assets.flow.spi;

/*-
 * #%L
 * wrench-component-flow
 * %%
 * Copyright (C) 2016 - 2017 Copyright 2016 ReSys OÜ
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

import java.util.Map;

import io.resys.hdes.client.api.programs.FlowProgram.FlowProgramStepType;
import io.resys.wrench.assets.flow.api.FlowExecutorRepository;
import io.resys.wrench.assets.flow.spi.executors.GenericFlowExecutor;

public class GenericFlowExecutorFactory implements FlowExecutorRepository {

  private final Map<FlowProgramStepType, FlowTaskExecutor> executors;

  public GenericFlowExecutorFactory(Map<FlowProgramStepType, FlowTaskExecutor> executors) {
    this.executors = executors;
  }

  @Override
  public FlowExecutor createExecutor() {
    return new GenericFlowExecutor(type -> createTaskExecutor(type));
  }

  @Override
  public FlowTaskExecutor createTaskExecutor(FlowProgramStepType type) {
    if(!executors.containsKey(type)) {
      throw new FlowDefinitionException("No executor for flow type: " + type + "!");
    }
    return executors.get(type);
  }
}
