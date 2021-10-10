package io.resys.hdes.client.spi.flow;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.resys.hdes.client.api.ast.AstFlow;
import io.resys.hdes.client.api.ast.AstFlow.AstFlowInputNode;
import io.resys.hdes.client.api.ast.AstFlow.AstFlowNode;
import io.resys.hdes.client.api.ast.AstFlow.AstFlowRoot;
import io.resys.hdes.client.api.ast.AstFlow.AstFlowSwitchNode;
import io.resys.hdes.client.api.ast.AstFlow.AstFlowTaskNode;
import io.resys.hdes.client.api.ast.TypeDef;
import io.resys.hdes.client.api.ast.TypeDef.Direction;
import io.resys.hdes.client.api.ast.TypeDef.ValueType;
import io.resys.hdes.client.api.exceptions.FlowAstException;
import io.resys.hdes.client.api.programs.FlowProgram;
import io.resys.hdes.client.api.programs.FlowProgram.FlowTaskType;
import io.resys.hdes.client.api.programs.FlowProgram.Step;
import io.resys.hdes.client.api.programs.FlowProgram.StepBody;
import io.resys.hdes.client.api.programs.ImmutableFlowProgram;
import io.resys.hdes.client.api.programs.ImmutableStepBody;
import io.resys.hdes.client.spi.HdesTypeDefsFactory;
import io.resys.hdes.client.spi.flow.ast.AstFlowNodesFactory;
import io.resys.hdes.client.spi.flow.ast.beans.NodeFlowBean;
import io.resys.hdes.client.spi.flow.program.ImmutableStep;
import io.resys.hdes.client.spi.flow.program.ImmutableStepExpression;

public class FlowProgramBuilder {
  private final HdesTypeDefsFactory typesFactory;

  private final Map<String, ImmutableStep> taskModels = new HashMap<>();
  private final ImmutableStep endNode = new ImmutableStep("end", null, FlowTaskType.END);
  private List<AstFlowTaskNode> tasksByOrder;
  private Map<String, AstFlowTaskNode> tasksById;  
  private String flowId;
  
  public FlowProgramBuilder(HdesTypeDefsFactory typesFactory) {
    super();
    this.typesFactory = typesFactory;
  }

  
  public FlowProgram build(AstFlow ast) {
    final var data = ast.getSrc();
    
    this.flowId = AstFlowNodesFactory.getStringValue(data.getId());
    this.tasksById = data.getTasks().values().stream()
        .collect(Collectors.toMap(n -> AstFlowNodesFactory.getStringValue(n.getId()), n -> n));
    
    this.tasksByOrder = new ArrayList<>(data.getTasks().values());
    Collections.sort(tasksByOrder);
    
    final var firstTask = data.getTasks().values().stream()
        .filter(task -> task.getOrder() == 0)
        .findFirst().orElse(null);

    final ImmutableStep task = tasksById.isEmpty() ? ImmutableStep.EMPTY: visitTasks(firstTask);
    
    final Map<String, Step> steps = new HashMap<>();
    visitSteps(steps, task);
    
    return ImmutableFlowProgram.builder()
        .id(flowId)
        .ast(ast)
        .step(task)
        .steps(steps.values())
        .acceptDefs(visitAcceptDefs(data))
        .build();
  }

  private void visitSteps(Map<String, Step> visited, Step node) {
    if(visited.containsKey(node.getId())) {
      return;
    }
    visited.put(node.getId(), node);
    node.getNext().forEach(n -> visitSteps(visited, n));
  }


  private ImmutableStep visitTasks(AstFlowTaskNode task) {
    String taskId = AstFlowNodesFactory.getStringValue(task.getId());
    if(taskModels.containsKey(taskId)) {
      return taskModels.get(taskId);
    }

    FlowTaskType type = getTaskType(task);
    StepBody taskValue = createFlowTaskValue(task, type);
    final ImmutableStep result = new ImmutableStep(taskId, taskValue, type);
    taskModels.put(taskId, result);

    final ImmutableStep intermediate;

    if(type != FlowTaskType.EMPTY) {
      intermediate = new ImmutableStep(taskId + "-" + FlowTaskType.MERGE, null, FlowTaskType.MERGE);
      result.addNext(intermediate);
    } else {
      intermediate = result;
    }

    boolean isEnd = endNode.getId().equals(AstFlowNodesFactory.getStringValue(task.getThen()));
    String then = AstFlowNodesFactory.getStringValue(task.getThen());

    // Add next
    if(then != null && !isEnd) {
      intermediate.addNext(visitTasks(tasksById.get(getThenTaskId(task, then, type))));
    }

    if(isEnd) {
      intermediate.addNext(endNode);
    }

    // Only exclusive decisions have next in here
    if(task.getSwitch().isEmpty()) {
      return result;
    }

    // Exclusive decision gateway
    ImmutableStep exclusive = new ImmutableStep(taskId + "-" + FlowTaskType.EXCLUSIVE, null, FlowTaskType.EXCLUSIVE);
    intermediate.addNext(exclusive);

    List<AstFlowSwitchNode> decisions = new ArrayList<>(task.getSwitch().values());
    Collections.sort(decisions, (o1, o2) -> Integer.compare(o1.getOrder(), o2.getOrder()));

    for(AstFlowSwitchNode decision : decisions) {
      String decisionId = decision.getKeyword();

      try {
        String when = AstFlowNodesFactory.getStringValue(decision.getWhen());
        String thenValue = AstFlowNodesFactory.getStringValue(decision.getThen());
        
        final var isTrue = when == null || when.isBlank();
        final var expression = isTrue ? 
            typesFactory.expression(ValueType.FLOW_CONTEXT, "true") :
            typesFactory.expression(ValueType.FLOW_CONTEXT, when);
        
        Map<String, String> inputMapping = new HashMap<>();
        expression.getConstants().forEach(name -> inputMapping.put(name, name));
        
        final var body = ImmutableStepBody.builder()
            .expression(new ImmutableStepExpression(expression))
            .isCollection(false)
            .inputs(inputMapping)
            .build();

        ImmutableStep next = new ImmutableStep(decisionId, body, FlowTaskType.DECISION);
        exclusive.addNext(next);

        if(thenValue != null && !endNode.getId().equals(thenValue)) {
          next.addNext(visitTasks(tasksById.get(getThenTaskId(task, thenValue, type))));
        }

      } catch(Exception e) {
        String message = "Failed to evaluate expression: \"" + taskId + "\" in flow: " + flowId + ", decision: " + decisionId + "!" + System.lineSeparator() + e.getMessage();
        throw new FlowAstException(message, e);
      }
    }

    return result;
  }

  private String getThenTaskId(AstFlowTaskNode task, String then, FlowTaskType type) {
    if(!NodeFlowBean.VALUE_NEXT.equalsIgnoreCase(then)) {
      return then;
    }

    AstFlowTaskNode next = null;
    for(AstFlowTaskNode node : tasksByOrder) {
      if(node.getStart() > task.getStart()) {
        next = node;
      }
    }

    if(next == null) {
      String taskId = AstFlowNodesFactory.getStringValue(task.getId());
      String message = "There are no next task after: \"" + taskId + "\" in flow: " + flowId + ", decision: " + taskId + "!";
      throw new FlowAstException(message);
    }
    return AstFlowNodesFactory.getStringValue(next.getId());
  }

  public StepBody createFlowTaskValue(AstFlowTaskNode task, FlowTaskType type) {
    if(type == FlowTaskType.SERVICE || type == FlowTaskType.DT || type == FlowTaskType.USER_TASK) {

      boolean collection = AstFlowNodesFactory.getBooleanValue(task.getRef().getCollection());
      String ref = AstFlowNodesFactory.getStringValue(task.getRef().getRef());
      Map<String, String> inputs = new HashMap<>();
      for(Map.Entry<String, AstFlowNode> entry : task.getRef().getInputs().entrySet()) {
        inputs.put(entry.getKey(), AstFlowNodesFactory.getStringValue(entry.getValue()));
      }
      return ImmutableStepBody.builder()
          .isCollection(collection)
          .ref(ref)
          .putAllInputs(inputs)
          .build();
    } else if(type == FlowTaskType.EMPTY) {
      return ImmutableStepBody.builder().isCollection(false).build();
    }

    throw new IllegalArgumentException("Can't create task value from type: " + type + "!");
  }
  

  private Collection<TypeDef> visitAcceptDefs(AstFlowRoot data) {
    Map<String, AstFlowInputNode> inputs = data.getInputs();

    int index = 0;
    Collection<TypeDef> result = new ArrayList<>();
    for (Map.Entry<String, AstFlowInputNode> entry : inputs.entrySet()) {
      if (entry.getValue().getType() == null) {
        continue;
      }
      try {
        ValueType valueType = ValueType.valueOf(entry.getValue().getType().getValue());
        boolean required = AstFlowNodesFactory.getBooleanValue(entry.getValue().getRequired());
        result.add(this.typesFactory.dataType()
            .id(entry.getValue().getStart() + "")
            .order(index++)
            .name(entry.getKey()).valueType(valueType).direction(Direction.IN).required(required)
            .values(AstFlowNodesFactory.getStringValue(entry.getValue().getDebugValue()))
            .build());
        
      } catch (Exception e) {
        final String msg = String.format("Failed to convert data type from: %s, error: %s", entry.getValue().getType().getValue(), e.getMessage());
        throw new FlowAstException(msg, e);
      }
    }
    return Collections.unmodifiableCollection(result);
  }

  private static FlowTaskType getTaskType(AstFlowTaskNode task) {
    if (task.getUserTask() != null) {
      return FlowTaskType.USER_TASK;
    } else if (task.getDecisionTable() != null) {
      return FlowTaskType.DT;
    } else if (task.getService() != null) {
      return FlowTaskType.SERVICE;
    }

    return FlowTaskType.EMPTY;
  }
}
