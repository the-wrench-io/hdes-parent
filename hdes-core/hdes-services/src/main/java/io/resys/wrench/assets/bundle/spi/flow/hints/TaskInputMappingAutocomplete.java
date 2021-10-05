package io.resys.wrench.assets.bundle.spi.flow.hints;

/*-
 * #%L
 * wrench-assets-bundle
 * %%
 * Copyright (C) 2016 - 2019 Copyright 2016 ReSys OÜ
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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import io.resys.hdes.client.api.ast.AstType.Direction;
import io.resys.hdes.client.api.ast.FlowAstType.FlowAstNode;
import io.resys.hdes.client.api.ast.FlowAstType.NodeFlow;
import io.resys.hdes.client.api.ast.FlowAstType.NodeFlowVisitor;
import io.resys.hdes.client.api.ast.FlowAstType.FlowAstRef;
import io.resys.hdes.client.api.ast.FlowAstType.FlowAstTask;
import io.resys.hdes.client.api.ast.AstDataType;
import io.resys.hdes.client.api.ast.ImmutableFlowAstType;
import io.resys.hdes.client.spi.flow.ast.FlowNodesFactory;
import io.resys.hdes.client.spi.flow.ast.beans.NodeFlowBean;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.AssetService;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceStore;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceType;
import io.resys.wrench.assets.flow.spi.support.NodeFlowAdapter;

public class TaskInputMappingAutocomplete extends TemplateAutocomplete implements NodeFlowVisitor {


  public TaskInputMappingAutocomplete(ServiceStore serviceStore) {
    super(serviceStore);
  }

  @Override
  public void visit(NodeFlow flow, ImmutableFlowAstType.Builder modelBuilder) {
    Map<String, FlowAstTask> tasks = flow.getTasks();
    if(tasks.isEmpty()) {
      return;
    }

    Map<String, String> inputsByNameAndType = getInputs(flow);
    for(FlowAstTask task : tasks.values()) {
      FlowAstRef ref = task.getDecisionTable() != null ? task.getDecisionTable() : task.getService();
      if(ref == null) {
        continue;
      }

      if(ref.get(NodeFlowBean.KEY_INPUTS) == null) {
        continue;
      }

      String taskServiceName = NodeFlowAdapter.getStringValue(ref.getRef());
      String taskServiceId = NodeFlowAdapter.getStringValue(task.getId());
      ServiceType serviceType = getServiceType(task);
      if(serviceType == null || StringUtils.isEmpty(taskServiceName)) {
        continue;
      }
      AssetService service = createQuery().type(serviceType).name(taskServiceName).get().orElse(null);
      if(service == null) {
        continue;
      }

      Map<String, String> serviceParamByNameAndType = service.getDataModel().getParams().stream()
          .filter(param -> param.getDirection() == Direction.IN)
          .collect(Collectors.toMap(p -> p.getName(), p -> p.getValueType().name()));

      for(Map.Entry<String, FlowAstNode> entry : ref.getInputsNode().getChildren().entrySet()) {
        String type = serviceParamByNameAndType.get(entry.getKey());
        if(type == null) {
          continue;
        }

        List<String> possibleInputs = inputsByNameAndType.entrySet().stream()
            .filter(e -> type.equals(e.getValue()))
            .filter(e -> !e.getKey().startsWith(taskServiceId + "."))
            .map(e -> e.getKey()).collect(Collectors.toList());

        if(possibleInputs.isEmpty()) {
          continue;
        }
        modelBuilder.addAutocomplete(
            FlowNodesFactory.ac()
            .id(TaskInputMappingAutocomplete.class.getSimpleName())
            .addRange(FlowNodesFactory.range().build(entry.getValue().getStart(), entry.getValue().getEnd(), false, 11 + entry.getKey().length()))
            .addValue(possibleInputs)
            .build());
      }
    }
  }

  private Map<String, String> getInputs(NodeFlow flow) {
    Map<String, String> result = new HashMap<>();

    for(final var e : flow.getInputs().entrySet()) {
      final var key = e.getKey();
      final var value = NodeFlowAdapter.getStringValue(e.getValue().getType());
      if(value != null) {
        result.put(key, value);
      }
    }
    
    for(FlowAstTask taskModel : flow.getTasks().values()) {
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
          result.put(name, param.getValueType().name());
        }
      }
    }

    return Collections.unmodifiableMap(result);
  }
}
