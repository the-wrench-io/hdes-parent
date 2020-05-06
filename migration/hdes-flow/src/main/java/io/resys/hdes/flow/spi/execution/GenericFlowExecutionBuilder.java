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

import io.reactivex.Single;
import io.resys.hdes.datatype.api.DataType;
import io.resys.hdes.datatype.api.DataTypeInput;
import io.resys.hdes.datatype.api.DataTypeService;
import io.resys.hdes.datatype.api.exceptions.HdesException;
import io.resys.hdes.datatype.spi.Assert;
import io.resys.hdes.flow.api.FlowAst;
import io.resys.hdes.flow.api.FlowAst.FlowTaskType;
import io.resys.hdes.flow.api.FlowExecution;
import io.resys.hdes.flow.api.FlowExecution.ExecutionStatus;
import io.resys.hdes.flow.api.FlowExecution.ExecutionTask;
import io.resys.hdes.flow.api.FlowExecutionException;
import io.resys.hdes.flow.api.FlowInputException;
import io.resys.hdes.flow.api.FlowService;
import io.resys.hdes.flow.api.FlowService.ExecutionBuilder;
import io.resys.hdes.flow.api.ImmutableFlowExecution;

public class GenericFlowExecutionBuilder implements FlowService.ExecutionBuilder {

  private final DataTypeService dataTypeService;
  private final FlowTaskExecutorFactory taskExecutorFactory;
  
  private String id;
  private FlowAst ast;
  private FlowExecution execution;
  private DataTypeInput input;
  
  public GenericFlowExecutionBuilder(DataTypeService dataTypeService, FlowTaskExecutorFactory taskExecutorFactory) {
    super();
    this.dataTypeService = dataTypeService;
    this.taskExecutorFactory = taskExecutorFactory;
  }

  @Override
  public ExecutionBuilder id(String id) {
    this.id = id;
    return this;
  }
  
  @Override
  public ExecutionBuilder from(FlowAst ast) {
    this.ast = ast;
    return this;
  }

  @Override
  public ExecutionBuilder input(DataTypeInput input) {
    this.input = input;
    return this;
  }
  
  @Override
  public ExecutionBuilder from(FlowExecution execution) {
    this.execution = execution;
    return this;
  }

  @Override
  public Single<FlowExecution> build() {
    final String id = execution == null ? this.id : execution.getId();

    Assert.notNull(id, () -> "id can't be null!");
    Assert.isTrue(ast != null || execution != null, () -> "ast or execution must be defined!");

    final FlowAst ast = this.ast;
    final FlowExecution start = this.execution;
    final DataTypeInput input = this.input;

    return Single.fromCallable(() -> {
      FlowExecution execution = createExecution(id, ast, start, input);
      FlowAst.Task task = getTaskToRun(execution);
      return run(execution, task);
    });
  }
  
  private FlowExecution createExecution(String id, FlowAst ast, FlowExecution execution, DataTypeInput input) {
    if (execution != null) {
      return execution;
    }

    List<String> requiredButNull = new ArrayList<>();
    Map<String, Serializable> entries = new HashMap<>();
    for (DataType dataType : ast.getInputs()) {
      Serializable value = input.apply(dataType);
      if (dataType.isRequired() && value == null) {
        requiredButNull.add(dataType.getName());
        continue;
      }
      if (value == null) {
        continue;
      }
      entries.put(dataType.getName(), value);
    }
    FlowExecution result = ImmutableFlowExecution.builder().id(id).ast(ast).input(entries).build();
    if (!requiredButNull.isEmpty()) {
      throw FlowInputException.builder().required(requiredButNull).model(result).build();
    }
    return result;
  }
  
  private FlowAst.Task getTaskToRun(FlowExecution execution) {
    if(execution.getValue().isEmpty()) {
      return execution.getAst().getTask();
    }
    
    List<ExecutionTask> executionTasks = execution.getValue();
    ExecutionTask executionTask = executionTasks.get(executionTasks.size() -1);
    return execution.getAst().getTask().get(executionTask.getId());
  }
  
  private FlowExecution run(FlowExecution execution, FlowAst.Task node) {
    try {
      ExecutionTask taskEnd = taskExecutorFactory.create(node).apply(node, execution);

      FlowExecution newState = ImmutableFlowExecution.builder()
        .from(execution)
        .addValue(taskEnd)
        .build();

      if (taskEnd.getStatus() == ExecutionStatus.SUSPENDED ||
        node.getType() == FlowTaskType.END) {
        return newState;
      }

      String nextTaskId = taskEnd.getNextTaskId();
      return run(newState, node.get(nextTaskId));
    } catch (HdesException e) {
      throw e;
    } catch (Exception e) {
      throw FlowExecutionException.builder()
        .model(execution, node)
        .msg(e.getMessage())
        .original(e)
        .body(dataTypeService.write().type(execution).build())
        .build();
    }
  }
}
