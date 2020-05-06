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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.jexl3.JexlContext;

import io.resys.hdes.datatype.spi.Assert;
import io.resys.hdes.flow.api.FlowAst;
import io.resys.hdes.flow.api.FlowExecution;
import io.resys.hdes.flow.api.FlowExecution.ExecutionStatus;
import io.resys.hdes.flow.api.FlowExecution.ExecutionTask;
import io.resys.hdes.flow.api.FlowExecutionSwitchException;
import io.resys.hdes.flow.api.ImmutableExecutionTask;
import io.resys.hdes.flow.spi.execution.FlowTaskExecutorFactory;

public class ExclusiveFlowTaskExecutor implements FlowTaskExecutorFactory.Executor {

  @Override
  public ExecutionTask apply(FlowAst.Task task, FlowExecution execution) {

    // Need to eval all child node to figure out what's the next node
    FlowAst.Task defaultNode = null;
    for(FlowAst.Task next : task.getNext()) {
      if(next.getValue() == null) {
        defaultNode = next;
        continue;
      }
      
      Map<String, Serializable> inputs = new HashMap<>();
      if(eval(inputs, next, execution)) {
        Assert.isTrue(next.getNext().size() <= 1, () -> "There can be only 1 or 0 next nodes in single decision point: " + next.getId() + "!");
        if(next.getNext().isEmpty()) {
          return null;
        }
        String nextTaskId = next.getNext().iterator().next().getId();
        return ImmutableExecutionTask.builder()
          .id(next.getId())
          .nextTaskId(nextTaskId)
          .inputs(inputs)
          .status(ExecutionStatus.ENDED)
          .build();
      }
    }
    if (defaultNode == null) {
      throw FlowExecutionSwitchException.builder().model(execution, task).msg("no gateways could be matched in switch and there is no default gateway").build();
    }
    String nextTaskId = defaultNode.getNext().isEmpty() ? null : defaultNode.getNext().iterator().next().getId();
    return ImmutableExecutionTask.builder()
      .id(defaultNode.getId())
      .nextTaskId(nextTaskId)
      .status(ExecutionStatus.ENDED)
      .build();
  }

  @SuppressWarnings("unchecked")
  private boolean eval(Map<String, Serializable> inputs, FlowAst.Task task, FlowExecution execution) {
    JexlContext entity = new FlowJexlContext(inputs, execution);
    return (boolean) task.getValue().getExpression().getOperation().apply(entity);
  }

  
  private static class FlowJexlContext implements JexlContext {
    private final FlowExecution execution;
    private final Map<String, Serializable> cache;
    
    public FlowJexlContext(Map<String, Serializable> inputs, FlowExecution execution) {
      super();
      this.cache = inputs;
      this.execution = execution;
    }
    
    @Override
    public Object get(String name) {
      load(name);
      return cache.get(name);
    }
    @Override
    public boolean has(String name) {
      load(name);
      return cache.containsKey(name);
    }
    @Override
    public void set(String name, Object value) {
     // can't change context 
    }
    
    private void load(String name) {
      if(cache.containsKey(name)) {
        return;
      }
      
      if(execution.getInput().containsKey(name)) {
        cache.put(name, execution.getInput().get(name));
        return;
      }
      
      int taskNameIndex = name.indexOf(".");
      if(taskNameIndex < 1) {
        cache.put(name, null);  
        return;
      }
      
      String taskName = name.substring(0, taskNameIndex);
      Optional<ExecutionTask> task = execution.getValue().stream().filter(t -> t.getId().equals(taskName)).findFirst();
      if(task.isPresent()) {
        cache.put(taskName, task.get().getOutputs().get(name.substring(taskNameIndex + 1)));
        return;
      }
      cache.put(name, null); 
    }
  }
}
