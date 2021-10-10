package io.resys.wrench.assets.flow.spi.executors;

import io.resys.hdes.client.api.programs.FlowResult;
import io.resys.hdes.client.api.programs.FlowProgram.Step;
import io.resys.hdes.client.api.programs.FlowResult.FlowTask;

/*-
 * #%L
 * wrench-assets-flow
 * %%
 * Copyright (C) 2016 - 2018 Copyright 2016 ReSys OÜ
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

import io.resys.wrench.assets.flow.api.FlowExecutorRepository;

public class ServiceFlowTaskExecutor implements FlowExecutorRepository.FlowTaskExecutor {

  @Override
  public Step execute(FlowResult flow, FlowTask task) {
    Step node = flow.getModel().getStep().get(task.getModelId());
    flow.complete(task);
    return node.getNext().iterator().next();
  }
}
