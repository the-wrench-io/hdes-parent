package io.resys.hdes.flow.spi.execution.task;

/*-
 * #%L
 * hdes-flow
 * %%
 * Copyright (C) 2020 Copyright 2020 ReSys OÜ
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

import io.resys.hdes.flow.api.FlowAst;
import io.resys.hdes.flow.api.FlowExecution;
import io.resys.hdes.flow.api.FlowExecution.ExecutionStatus;
import io.resys.hdes.flow.api.FlowExecution.ExecutionTask;
import io.resys.hdes.flow.api.ImmutableExecutionTask;
import io.resys.hdes.flow.spi.execution.FlowTaskExecutorFactory;

public class UserFlowTaskExecutor implements FlowTaskExecutorFactory.Executor {

  @Override
  public ExecutionTask apply(FlowAst.Task task, FlowExecution execution) {
    String nextTaskId = task.getNext().iterator().next().getId();
    return ImmutableExecutionTask.builder()
        .id(task.getId())
        .status(ExecutionStatus.OPEN)
        .nextTaskId(nextTaskId)
        .build();
  }

}
