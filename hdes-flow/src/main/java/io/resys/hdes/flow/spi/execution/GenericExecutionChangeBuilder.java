package io.resys.hdes.flow.spi.execution;

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.resys.hdes.datatype.spi.Assert;
import io.resys.hdes.flow.api.FlowExecution;
import io.resys.hdes.flow.api.FlowService;
import io.resys.hdes.flow.api.ImmutableExecutionTask;
import io.resys.hdes.flow.api.ImmutableFlowExecution;

public class GenericExecutionChangeBuilder implements FlowService.ExecutionChangeBuilder {

  private final Map<String, Serializable> inputs = new HashMap<>();
  private FlowExecution execution;
  private String endTaskId;

  @Override
  public FlowService.ExecutionChangeBuilder from(FlowExecution execution) {
    this.execution = execution;
    return this;
  }

  @Override
  public FlowService.ExecutionChangeBuilder addInput(String name, Serializable value) {
    if(value != null) {
      this.inputs.put(name, value);
    }
    return this;
  }

  @Override
  public FlowService.ExecutionChangeBuilder addInputs(Map<String, Serializable> inputs) {
    inputs.forEach((n, v) -> addInput(n, v));
    return this;
  }

  @Override
  public FlowService.ExecutionChangeBuilder endTask(String id) {
    this.endTaskId = id;
    return this;
  }

  @Override
  public FlowExecution build() {
    Assert.notNull(execution, () -> "execution can't be null!");
    Assert.notNull(endTaskId, () -> "endTaskId can't be null!");

    List<FlowExecution.ExecutionTask> tasks = new ArrayList<>();
    for(FlowExecution.ExecutionTask task : execution.getValue()) {
      if(task.getId().equals(endTaskId)) {
        tasks.add(ImmutableExecutionTask.builder().from(task).status(FlowExecution.ExecutionStatus.ENDED).build());
      } else {
        tasks.add(task);
      }
    }
    return ImmutableFlowExecution.builder()
      .from(execution)
      .putAllInput(this.inputs)
      .value(tasks)
      .build();
  }
}
