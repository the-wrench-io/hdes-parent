package io.resys.wrench.assets.bundle.spi.flow;

/*-
 * #%L
 * wrench-component-assets
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import io.resys.hdes.client.api.HdesClient;
import io.resys.hdes.client.api.ast.AstFlow.AstFlowInputNode;
import io.resys.hdes.client.api.ast.AstFlow.AstFlowNode;
import io.resys.hdes.client.api.ast.AstFlow.AstFlowNodeVisitor;
import io.resys.hdes.client.api.ast.AstFlow.AstFlowRoot;
import io.resys.hdes.client.api.ast.AstFlow.AstFlowTaskNode;
import io.resys.hdes.client.api.ast.AstFlow.FlowAstCommandMessage;
import io.resys.hdes.client.api.ast.AstFlow.FlowCommandMessageType;
import io.resys.hdes.client.api.ast.ImmutableAstFlow;
import io.resys.hdes.client.api.ast.ImmutableFlowAstCommandMessage;
import io.resys.hdes.client.api.ast.TypeDef;
import io.resys.hdes.client.api.ast.TypeDef.Direction;
import io.resys.hdes.client.api.ast.TypeDef.ValueType;
import io.resys.hdes.client.api.programs.FlowProgram.Step;
import io.resys.hdes.client.spi.flow.ast.AstFlowNodesFactory;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.AssetService;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceQuery;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceStatus;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceStore;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceType;
import io.resys.wrench.assets.bundle.spi.builders.GenericServiceQuery;

public class FlowServiceDataModelValidator implements AstFlowNodeVisitor {

  private final ServiceStore serviceStore;
  private final HdesClient dataTypeRepository;

  public FlowServiceDataModelValidator(ServiceStore serviceStore, HdesClient dataTypeRepository) {
    this.serviceStore = serviceStore;
    this.dataTypeRepository = dataTypeRepository;
  }

  @Override
  public void visit(AstFlowRoot node, ImmutableAstFlow.Builder modelBuilder) {

    List<TypeDef> params = new ArrayList<>(AstFlowNodesFactory.headers(dataTypeRepository).build(node).getAcceptDefs());
    Map<String, AstFlowInputNode> unusedInputs = new HashMap<>(node.getInputs());
    Map<String, TypeDef> allParams = createModelParameterMap(node, params);

    for(AstFlowTaskNode taskModel : node.getTasks().values()) {

      ServiceType serviceType = getServiceType(taskModel);
      if(serviceType == null) {
        continue;
      }

      // No ref at all
      if(taskModel.getRef() == null) {
        modelBuilder.addMessages(
            error(
                taskModel.getStart(),
                taskModel.getSource().getValue().length(),
                "Task: " + taskModel.getId() + ", is missing 'ref'!"));
        continue;
      }

      // no ref value
      String taskServiceName = AstFlowNodesFactory.getStringValue(taskModel.getRef().getRef());
      if(StringUtils.isEmpty(taskServiceName)) {
        modelBuilder.addMessages(
            error(
                taskModel.getRef().getStart(),
                taskModel.getRef().getSource().getValue().length(),
                "Task: " + taskModel.getId() + ", is missing 'ref' value!"));
        continue;
      }

      // unknown service
      AssetService service = createQuery().type(serviceType).name(taskServiceName).get().orElse(null);
      if(service == null) {
        modelBuilder.addMessages(
            error(
                taskModel.getRef().getRef().getStart(),
                taskModel.getRef().getRef().getSource().getValue().length(),
                "Task: " + taskModel.getId() + ", refers to non existing " + serviceType + ": " + taskServiceName + "!"));
        continue;
      }

      // Validate inputs
      Map<String, TaskInput> taskInputs = getTaskServiceInput(modelBuilder, taskModel, allParams, service);
      for(TypeDef input : service.getDataModel().getParams()) {
        if(input.getDirection() == Direction.OUT) {
          continue;
        }

        if(taskInputs.containsKey(input.getName())) {
          TaskInput taskInput = taskInputs.get(input.getName());
          if(taskInput.getDataType() == null) {
            modelBuilder.addMessages(
                error(
                    taskInput.getNode().getStart(),
                    taskInput.getNode().getSource().getValue().length(),
                    "Task: " + taskModel.getKeyword() + ", input: '" + input.getName() + "', type has unknown mapping:'" + taskInput.getNode().getValue() + "'!"));
           continue;
          }
          ValueType ref = taskInput.getDataType().getValueType();
          if(input.getValueType() != ref) {
            modelBuilder.addMessages(
                error(
                    taskInput.getNode().getStart(),
                    taskInput.getNode().getSource().getValue().length(),
                    "Task: " + taskModel.getKeyword() + ", input: '" + input.getName() + "', type has wrong type, expecting:'" + input.getValueType() + "' but was: '" + ref + "'!"));
          }
          taskInputs.remove(input.getName());
          unusedInputs.remove(taskInput.getDataType().getName());
        } else {
          modelBuilder.addMessages(
              error(
                  taskModel.getRef().getInputsNode().getStart(),
                  taskModel.getRef().getInputsNode().getSource().getValue().length(),
                  "Task: " + taskModel.getKeyword() + ", is missing input: '" + input.getName() + "'!"));
        }


      }

      // Unused inputs on task
      for(TaskInput input : taskInputs.values()) {
        String inputName = input.getDataType() == null ? input.getNode().getKeyword() : input.getDataType().getName();
        modelBuilder.addMessages(
            error(
                input.getNode().getStart(),
                input.getNode().getSource().getValue().length(),
                "Task: " + taskModel.getId().getValue() + ", has unused input: '" + inputName + "'!"));
      }

      if(service.getDataModel().getStatus() == ServiceStatus.ERROR) {
        modelBuilder.addMessages(
            error(
                taskModel.getRef().getStart(),
                taskModel.getRef().getSource().getValue().length(),
                "Task: " + taskModel.getId().getValue() + ", refers to " + serviceType + ", that is in error state!"));
      }
    }

    // Unused inputs on task
    for(AstFlowInputNode input : unusedInputs.values()) {
      modelBuilder.addMessages(
          warning(
              input.getStart(),
              input.getSource().getValue().length(),
              "Input: " + input.getKeyword() + " is unused!"));
    }
  }



  protected Map<String, TaskInput> getTaskServiceInput(
      ImmutableAstFlow.Builder modelBuilder,
      AstFlowTaskNode taskModel,
      Map<String, TypeDef> allParams,
      AssetService refService) {

    Map<String, TypeDef> serviceTypes = refService.getDataModel().getParams().stream()
        .filter(p -> p.getDirection() == Direction.IN)
        .collect(Collectors.toMap(p -> p.getName(), p -> p));

    Map<String, TaskInput> result = new HashMap<>();
    for(Map.Entry<String, AstFlowNode> entry : taskModel.getRef().getInputs().entrySet()) {

      AstFlowNode node = entry.getValue();
      String mappingName = AstFlowNodesFactory.getStringValue(node);
      if(StringUtils.isEmpty(mappingName)) {
        modelBuilder.addMessages(
            error(
                node.getStart(),
                node.getSource().getValue().length(),
                "Task: " + taskModel.getKeyword() + " mapping: '" + entry.getKey() + "' is missing value!"));


      } else if(!serviceTypes.containsKey(entry.getKey())) {

        modelBuilder.addMessages(
            error(
                node.getStart(),
                node.getSource().getValue().length(),
                "Task: " + taskModel.getKeyword() + ", has unknown input: '" + entry.getKey() + "'!"));


      } else if(allParams.containsKey(mappingName)) {
        result.put(entry.getKey(), new TaskInput(node, allParams.get(mappingName)));
      } else {
        
        result.put(entry.getKey(), new TaskInput(node, serviceTypes.get(entry.getKey())));
      }
    }
    return result;
  }
  
  private FlowAstCommandMessage error(int start, int range, String value) {
    return ImmutableFlowAstCommandMessage.builder()
        .line(start)
        .range(AstFlowNodesFactory.range().build(0, range))
        .type(FlowCommandMessageType.ERROR)
        .value(value)
        .build();
  }

  private FlowAstCommandMessage warning(int start, int range, String value) {
    return ImmutableFlowAstCommandMessage.builder()
        .line(start)
        .range(AstFlowNodesFactory.range().build(0, range))
        .type(FlowCommandMessageType.WARNING)
        .value(value)
        .build();
  }

  protected Map<String, TypeDef> createModelParameterMap(AstFlowRoot node, List<TypeDef> params) {
    Map<String, TypeDef> result = new HashMap<>();
    params.forEach(p -> result.put(p.getName(), p));

    for(AstFlowTaskNode taskModel : node.getTasks().values()) {
      if(taskModel.getRef() == null) {
        continue;
      }
      String taskServiceName = AstFlowNodesFactory.getStringValue(taskModel.getRef().getRef());
      ServiceType serviceType = getServiceType(taskModel);
      if(serviceType == null || StringUtils.isEmpty(taskServiceName)) {
        continue;
      }
      AssetService service = createQuery().type(serviceType).name(taskServiceName).get().orElse(null);
      if(service == null) {
        continue;
      }
      for(TypeDef param : service.getDataModel().getParams()) {
        if(param.getDirection() == Direction.OUT) {
          String name = AstFlowNodesFactory.getStringValue(taskModel.getId()) + "." + param.getName();
          Assert.isTrue(!result.containsKey(name), "Can't have duplicate param: " + name + "!");
          result.put(name, param);
        }
      }
    }

    return Collections.unmodifiableMap(result);
  }

  protected ServiceQuery createQuery() {
    return new GenericServiceQuery(serviceStore);
  }

  protected boolean isTaskServiceCollection(Step taskModel) {
    return taskModel.getBody() != null ? taskModel.getBody().isCollection() : false;
  }
  protected ServiceType getServiceType(AstFlowTaskNode taskModel) {
    if(taskModel.getDecisionTable() != null) {
      return ServiceType.DT;
    } else if(taskModel.getService() != null) {
      return ServiceType.FLOW_TASK;
    }

    return null;
  }

  private static class TaskInput {
    private final AstFlowNode node;
    private final TypeDef dataType;
    public TaskInput(AstFlowNode node, TypeDef dataType) {
      super();
      this.node = node;
      this.dataType = dataType;
    }
    public AstFlowNode getNode() {
      return node;
    }
    public TypeDef getDataType() {
      return dataType;
    }
  }
}
