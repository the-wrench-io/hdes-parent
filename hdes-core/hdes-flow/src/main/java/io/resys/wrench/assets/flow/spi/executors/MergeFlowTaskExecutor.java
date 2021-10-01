package io.resys.wrench.assets.flow.spi.executors;

import io.resys.hdes.client.api.execution.Flow;
import io.resys.hdes.client.api.execution.Flow.FlowStatus;
import io.resys.hdes.client.api.execution.Flow.FlowTask;
import io.resys.hdes.client.api.execution.Flow.FlowTaskStatus;
import io.resys.hdes.client.api.model.FlowModel.FlowTaskModel;

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

import io.resys.wrench.assets.flow.api.FlowExecutorRepository.FlowTaskExecutor;

public class MergeFlowTaskExecutor implements FlowTaskExecutor {

  @Override
  public FlowTaskModel execute(Flow flow, FlowTask task) {
    FlowTaskModel node = flow.getModel().getTask().get(task.getModelId());

    boolean isOpen = flow.getContext().getTasks().stream()
        .filter(e -> e.getModelId().equals(node.getPrevious().getId()))
        .filter(e -> e.getStatus().equals(FlowTaskStatus.OPEN))
        .findFirst().isPresent();

    if(isOpen) {
      flow.getContext().setStatus(FlowStatus.SUSPENDED);
      flow.suspend(task);
      return null;
    }

    flow.complete(task);
    return node.getNext().iterator().next();
  }

}
