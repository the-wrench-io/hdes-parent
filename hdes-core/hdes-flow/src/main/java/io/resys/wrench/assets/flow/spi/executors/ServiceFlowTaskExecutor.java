package io.resys.wrench.assets.flow.spi.executors;

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
import io.resys.wrench.assets.flow.api.model.Flow;
import io.resys.wrench.assets.flow.api.model.Flow.FlowTask;
import io.resys.wrench.assets.flow.api.model.FlowModel.FlowTaskModel;

public class ServiceFlowTaskExecutor implements FlowExecutorRepository.FlowTaskExecutor {

  @Override
  public FlowTaskModel execute(Flow flow, FlowTask task) {
    FlowTaskModel node = flow.getModel().getTask().get(task.getModelId());
    flow.complete(task);
    return node.getNext().iterator().next();
  }
}