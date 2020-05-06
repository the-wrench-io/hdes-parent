package io.resys.hdes.flow.spi.ast;

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

import io.resys.hdes.datatype.api.DataType;
import io.resys.hdes.datatype.api.DataType.Direction;
import io.resys.hdes.datatype.api.DataType.ValueType;
import io.resys.hdes.datatype.api.DataTypeService;
import io.resys.hdes.datatype.api.DataTypeService.ExpressionSourceType;
import io.resys.hdes.datatype.spi.Assert;
import io.resys.hdes.flow.api.*;
import io.resys.hdes.flow.api.FlowAst.FlowTaskType;
import io.resys.hdes.flow.spi.ast.beans.FlowAstTaskBean;
import io.resys.hdes.flow.spi.model.beans.FlowModelRootBean;

import java.util.*;


public class GenericFlowAstBuilder implements FlowService.AstBuilder {

  private static final FlowAstTaskBean EMPTY = new FlowAstTaskBean("empty", null, FlowTaskType.END);
  
  private final FlowAstTaskBean endNode = new FlowAstTaskBean("end", null, FlowTaskType.END);
  private final Map<String, FlowModel.Task> tasksById = new HashMap<>();
  private final Map<String, FlowAstTaskBean> taskModels = new HashMap<>();  
  private final List<FlowModel.Task> tasksByOrder = new ArrayList<>();
  private final DataTypeService dataTypeService;

  private FlowModel.Root model;
  
  public GenericFlowAstBuilder(DataTypeService dataTypeService) {
    super();
    this.dataTypeService = dataTypeService;
  }

  @Override
  public FlowService.AstBuilder from(FlowModel.Root model) {
    Assert.notNull(model, () -> "model can't be null!");
    this.model = model;

    model.getTasks().values().stream().forEach(n -> tasksById.put(n.getId().getValue(), n));
    tasksByOrder.addAll(model.getTasks().values());
    Collections.sort(tasksByOrder);

    return this;
  }

  @Override
  public FlowAst build() {
    return ImmutableFlowAst.builder()
      .id(this.model.getId().getValue())
      .rev(this.model.getRev())
      .inputs(getInputs(model))
      .task(tasksById.isEmpty() ? EMPTY: 
        createNode(model.getTasks().values().stream()
          .filter(task -> task.getOrder() == 0)
          .findFirst().orElse(null)))
      .build();
  }

  protected FlowAstTaskBean createNode(FlowModel.Task task) {
    
    String taskId = task.getId().getValue();
    
    if(taskModels.containsKey(taskId)) {
      return taskModels.get(taskId);
    }

    FlowTaskType type = getTaskType(task);
    FlowAst.TaskValue taskValue = createFlowTaskValue(task, type);
    final FlowAstTaskBean result = new FlowAstTaskBean(taskId, taskValue, type);
    taskModels.put(taskId, result);

    final FlowAstTaskBean intermediate;

    if(type != FlowTaskType.EMPTY) {
      intermediate = new FlowAstTaskBean(taskId + "-" + FlowTaskType.MERGE, null, FlowTaskType.MERGE);
      result.addNext(intermediate);
    } else {
      intermediate = result;
    }

    boolean isEnd = endNode.getId().equals(getStringValue(task.getThen()));
    String thenTaskId = getStringValue(task.getThen());

    // Add next
    if(thenTaskId != null && !isEnd) {
      FlowModel.Task then = tasksById.get(getThenTaskId(task, thenTaskId, type));
      FlowAstTaskBean next = createNode(then);
      intermediate.addNext(next);
    }

    if(isEnd) {
      intermediate.addNext(endNode);
    }

    // Only exclusive decisions have next in here
    if(task.getSwitch().isEmpty()) {
      return result;
    }

    // Exclusive decision gateway
    FlowAstTaskBean exclusive = new FlowAstTaskBean(taskId + "-" + FlowTaskType.EXCLUSIVE, null, FlowTaskType.EXCLUSIVE);
    intermediate.addNext(exclusive);

    List<FlowModel.Switch> decisions = new ArrayList<>(task.getSwitch().values());
    Collections.sort(decisions, (o1, o2) -> Integer.compare(o1.getOrder(), o2.getOrder()));

    for(FlowModel.Switch decision : decisions) {
      String decisionId = decision.getKeyword();

      try {
        String when = getStringValue(decision.getWhen());
        String thenValue = getStringValue(decision.getThen());

        DataTypeService.Expression expression = dataTypeService.expression().srcType(ExpressionSourceType.JEXL).src(when).valueType(ValueType.OBJECT).build();
        
        FlowAstTaskBean next = new FlowAstTaskBean(
          decisionId,
          ImmutableTaskValue.builder().expression(expression).isCollection(false).build(),
          FlowTaskType.DECISION);
        exclusive.addNext(next);

        if(thenValue != null && !endNode.getId().equals(thenValue)) {
          next.addNext(createNode(tasksById.get(getThenTaskId(task, thenValue, type))));
        }

      } catch(Exception e) {
        throw FlowAstException.builder().root(this.model).task(task).original(e).build();
      }
    }

    return result;
  }

  private String getThenTaskId(FlowModel.Task task, String then, FlowTaskType type) {
    if(!FlowModelRootBean.VALUE_NEXT.equalsIgnoreCase(then)) {
      return then;
    }

    FlowModel.Task next = null;
    for(FlowModel.Task node : tasksByOrder) {
      if(node.getStart() > task.getStart()) {
        next = node;
      }
    }

    if(next == null) {
      throw FlowAstException.builder().root(this.model).task(task).msg("Missing next task!").build();
    }
    return getStringValue(next.getId());
  }

  public FlowAst.TaskValue createFlowTaskValue(FlowModel.Task task, FlowTaskType type) {
    if(type == FlowTaskType.SERVICE || type == FlowTaskType.DT || type == FlowTaskType.USER_TASK) {

      boolean collection = getBooleanValue(task.getRef().getCollection());
      String ref = getStringValue(task.getRef().getRef());
      Map<String, String> inputs = new HashMap<>();
      for(Map.Entry<String, FlowModel> entry : task.getRef().getInputs().entrySet()) {
        inputs.put(entry.getKey(), getStringValue(entry.getValue()));
      }

      return ImmutableTaskValue.builder()
          .isCollection(collection)
          .inputs(inputs)
          .ref(ref)
          .build();
    } else if(type == FlowTaskType.EMPTY) {
      return ImmutableTaskValue.builder().isCollection(false).build();
    }
    throw FlowAstException.builder().root(this.model).task(task).msg("Not implemented task value!").build();
  }

  public Collection<DataType> getInputs(FlowModel.Root data) {
    Map<String, FlowModel.Input> inputs = data.getInputs();

    Collection<DataType> result = new ArrayList<>();
    for (Map.Entry<String, FlowModel.Input> entry : inputs.entrySet()) {
      if (entry.getValue().getType() == null) {
        continue;
      }
      ValueType valueType = ValueType.valueOf(entry.getValue().getType().getValue());
      boolean required = getBooleanValue(entry.getValue().getRequired());
      result.add(dataTypeService.model().name(entry.getKey()).valueType(valueType).direction(Direction.IN).required(required).build());
      
    }
    return Collections.unmodifiableCollection(result);
  }

  private static FlowTaskType getTaskType(FlowModel.Task task) {
    if (task.getUserTask() != null) {
      return FlowTaskType.USER_TASK;
    } else if (task.getDecisionTable() != null) {
      return FlowTaskType.DT;
    } else if (task.getService() != null) {
      return FlowTaskType.SERVICE;
    }

    return FlowTaskType.EMPTY;
  }
  
  private static String getStringValue(FlowModel node) {
    if (node == null || node.getValue() == null) {
      return null;
    }
    return node.getValue();
  }

  private static boolean getBooleanValue(FlowModel node) {
    if (node == null || node.getValue() == null) {
      return false;
    }
    return Boolean.parseBoolean(node.getValue());
  }
}
