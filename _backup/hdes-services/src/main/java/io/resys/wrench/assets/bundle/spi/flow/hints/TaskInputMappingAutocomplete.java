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

import io.resys.hdes.client.api.ast.AstFlow.AstFlowNode;
import io.resys.hdes.client.api.ast.AstFlow.AstFlowRefNode;
import io.resys.hdes.client.api.ast.AstFlow.AstFlowRoot;
import io.resys.hdes.client.api.ast.AstFlow.AstFlowTaskNode;
import io.resys.hdes.client.api.ast.ImmutableAstFlow;
import io.resys.hdes.client.api.ast.TypeDef;
import io.resys.hdes.client.api.ast.TypeDef.Direction;
import io.resys.hdes.client.spi.config.HdesClientConfig.AstFlowNodeVisitor;
import io.resys.hdes.client.spi.flow.ast.AstFlowNodesFactory;
import io.resys.hdes.client.spi.flow.ast.beans.NodeFlowBean;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.AssetService;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceStore;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceType;

public class TaskInputMappingAutocomplete extends TemplateAutocomplete implements AstFlowNodeVisitor {


  public TaskInputMappingAutocomplete(ServiceStore serviceStore) {
    super(serviceStore);
  }

  @Override
  public void visit(AstFlowRoot flow, ImmutableAstFlow.Builder modelBuilder) {
    Map<String, AstFlowTaskNode> tasks = flow.getTasks();
    if(tasks.isEmpty()) {
      return;
    }

    Map<String, String> inputsByNameAndType = getInputs(flow);
    for(AstFlowTaskNode task : tasks.values()) {
      AstFlowRefNode ref = task.getDecisionTable() != null ? task.getDecisionTable() : task.getService();
      if(ref == null) {
        continue;
      }

      if(ref.get(NodeFlowBean.KEY_INPUTS) == null) {
        continue;
      }

      String taskServiceName = AstFlowNodesFactory.getStringValue(ref.getRef());
      String taskServiceId = AstFlowNodesFactory.getStringValue(task.getId());
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

      for(Map.Entry<String, AstFlowNode> entry : ref.getInputsNode().getChildren().entrySet()) {
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
            AstFlowNodesFactory.ac()
            .id(TaskInputMappingAutocomplete.class.getSimpleName())
            .addRange(AstFlowNodesFactory.range().build(entry.getValue().getStart(), entry.getValue().getEnd(), false, 11 + entry.getKey().length()))
            .addValue(possibleInputs)
            .build());
      }
    }
  }

  private Map<String, String> getInputs(AstFlowRoot flow) {
    Map<String, String> result = new HashMap<>();

    for(final var e : flow.getInputs().entrySet()) {
      final var key = e.getKey();
      final var value = AstFlowNodesFactory.getStringValue(e.getValue().getType());
      if(value != null) {
        result.put(key, value);
      }
    }
    
    for(AstFlowTaskNode taskModel : flow.getTasks().values()) {
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
          result.put(name, param.getValueType().name());
        }
      }
    }

    return Collections.unmodifiableMap(result);
  }
}
