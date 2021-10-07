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

import io.resys.hdes.client.api.HdesAstTypes;
import io.resys.hdes.client.api.ast.AstDataType;
import io.resys.hdes.client.api.ast.AstDataType.Direction;
import io.resys.hdes.client.api.ast.AstDataType.ValueType;
import io.resys.hdes.client.api.ast.FlowAstType.FlowAstCommandMessage;
import io.resys.hdes.client.api.ast.FlowAstType.FlowAstInput;
import io.resys.hdes.client.api.ast.FlowAstType.FlowAstNode;
import io.resys.hdes.client.api.ast.FlowAstType.FlowAstTask;
import io.resys.hdes.client.api.ast.FlowAstType.FlowCommandMessageType;
import io.resys.hdes.client.api.ast.FlowAstType.NodeFlow;
import io.resys.hdes.client.api.ast.FlowAstType.NodeFlowVisitor;
import io.resys.hdes.client.api.ast.ImmutableFlowAstCommandMessage;
import io.resys.hdes.client.api.ast.ImmutableFlowAstType;
import io.resys.hdes.client.api.model.FlowModel.FlowTaskModel;
import io.resys.hdes.client.spi.flow.ast.FlowNodesFactory;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.AssetService;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceQuery;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceStatus;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceStore;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceType;
import io.resys.wrench.assets.bundle.spi.builders.GenericServiceQuery;
import io.resys.wrench.assets.flow.spi.support.NodeFlowAdapter;

public class FlowServiceDataModelValidator implements NodeFlowVisitor {

  private final ServiceStore serviceStore;
  private final HdesAstTypes dataTypeRepository;

  public FlowServiceDataModelValidator(ServiceStore serviceStore, HdesAstTypes dataTypeRepository) {
    this.serviceStore = serviceStore;
    this.dataTypeRepository = dataTypeRepository;
  }

  @Override
  public void visit(NodeFlow node, ImmutableFlowAstType.Builder modelBuilder) {

    List<AstDataType> params = new ArrayList<>(NodeFlowAdapter.getInputs(node, dataTypeRepository));
    Map<String, FlowAstInput> unusedInputs = new HashMap<>(node.getInputs());
    Map<String, AstDataType> allParams = createModelParameterMap(node, params);

    for(FlowAstTask taskModel : node.getTasks().values()) {

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
      String taskServiceName = NodeFlowAdapter.getStringValue(taskModel.getRef().getRef());
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
      for(AstDataType input : service.getDataModel().getParams()) {
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
    for(FlowAstInput input : unusedInputs.values()) {
      modelBuilder.addMessages(
          warning(
              input.getStart(),
              input.getSource().getValue().length(),
              "Input: " + input.getKeyword() + " is unused!"));
    }
  }



  protected Map<String, TaskInput> getTaskServiceInput(
      ImmutableFlowAstType.Builder modelBuilder,
      FlowAstTask taskModel,
      Map<String, AstDataType> allParams,
      AssetService refService) {

    Map<String, AstDataType> serviceTypes = refService.getDataModel().getParams().stream()
        .filter(p -> p.getDirection() == Direction.IN)
        .collect(Collectors.toMap(p -> p.getName(), p -> p));

    Map<String, TaskInput> result = new HashMap<>();
    for(Map.Entry<String, FlowAstNode> entry : taskModel.getRef().getInputs().entrySet()) {

      FlowAstNode node = entry.getValue();
      String mappingName = NodeFlowAdapter.getStringValue(node);
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
        .range(FlowNodesFactory.range().build(0, range))
        .type(FlowCommandMessageType.ERROR)
        .value(value)
        .build();
  }

  private FlowAstCommandMessage warning(int start, int range, String value) {
    return ImmutableFlowAstCommandMessage.builder()
        .line(start)
        .range(FlowNodesFactory.range().build(0, range))
        .type(FlowCommandMessageType.WARNING)
        .value(value)
        .build();
  }

  protected Map<String, AstDataType> createModelParameterMap(NodeFlow node, List<AstDataType> params) {
    Map<String, AstDataType> result = new HashMap<>();
    params.forEach(p -> result.put(p.getName(), p));

    for(FlowAstTask taskModel : node.getTasks().values()) {
      if(taskModel.getRef() == null) {
        continue;
      }
      String taskServiceName = NodeFlowAdapter.getStringValue(taskModel.getRef().getRef());
      ServiceType serviceType = getServiceType(taskModel);
      if(serviceType == null || StringUtils.isEmpty(taskServiceName)) {
        continue;
      }
      AssetService service = createQuery().type(serviceType).name(taskServiceName).get().orElse(null);
      if(service == null) {
        continue;
      }
      for(AstDataType param : service.getDataModel().getParams()) {
        if(param.getDirection() == Direction.OUT) {
          String name = NodeFlowAdapter.getStringValue(taskModel.getId()) + "." + param.getName();
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

  protected boolean isTaskServiceCollection(FlowTaskModel taskModel) {
    return taskModel.getBody() != null ? taskModel.getBody().isCollection() : false;
  }
  protected ServiceType getServiceType(FlowAstTask taskModel) {
    if(taskModel.getDecisionTable() != null) {
      return ServiceType.DT;
    } else if(taskModel.getService() != null) {
      return ServiceType.FLOW_TASK;
    }

    return null;
  }

  private static class TaskInput {
    private final FlowAstNode node;
    private final AstDataType dataType;
    public TaskInput(FlowAstNode node, AstDataType dataType) {
      super();
      this.node = node;
      this.dataType = dataType;
    }
    public FlowAstNode getNode() {
      return node;
    }
    public AstDataType getDataType() {
      return dataType;
    }
  }
}
