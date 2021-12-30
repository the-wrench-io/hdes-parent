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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.resys.hdes.client.api.ast.AstFlow.AstFlowRefNode;
import io.resys.hdes.client.api.ast.AstFlow.AstFlowRoot;
import io.resys.hdes.client.api.ast.AstFlow.AstFlowTaskNode;
import io.resys.hdes.client.api.ast.AstFlow.FlowAstCommandRange;
import io.resys.hdes.client.api.ast.ImmutableAstFlow;
import io.resys.hdes.client.spi.config.HdesClientConfig.AstFlowNodeVisitor;
import io.resys.hdes.client.spi.flow.ast.AstFlowNodesFactory;
import io.resys.hdes.client.spi.flow.ast.beans.NodeFlowBean;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.AssetService;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceStore;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceType;

public class TaskRefAutocomplete extends TemplateAutocomplete implements AstFlowNodeVisitor {

  public TaskRefAutocomplete(ServiceStore serviceStore) {
    super(serviceStore);
  }

  @Override
  public void visit(AstFlowRoot flow, ImmutableAstFlow.Builder modelBuilder) {
    Map<String, AstFlowTaskNode> tasks = flow.getTasks();
    if(tasks.isEmpty()) {
      return;
    }

    Map<ServiceType, List<String>> services = new HashMap<>();
    for(AssetService service : createQuery().list()) {
      List<String> serviceNames = services.get(service.getType());
      if(serviceNames == null) {
        serviceNames = new ArrayList<>();
        services.put(service.getType(), serviceNames);
      }
      serviceNames.add(service.getName());
    }


    for(AstFlowTaskNode task : tasks.values()) {
      AstFlowRefNode ref = task.getRef();
      if(ref == null) {
        continue;
      }
      ServiceType serviceType = getServiceType(task);
      if(serviceType == null) {
        continue;
      }
      List<String> refs = services.get(serviceType);
      if(refs == null) {
        continue;
      }

      FlowAstCommandRange range;
      if(ref.getRef() == null) {
        range = AstFlowNodesFactory.range().build(ref.getStart(), ref.getEnd(), true);
      } else {
        range = AstFlowNodesFactory.range().build(task.getRef().getStart(), task.getRef().getEnd(), false);
      }

      refs.forEach(r -> modelBuilder.addAutocomplete(AstFlowNodesFactory.ac().id(TaskRefAutocomplete.class.getSimpleName())
          .addField(8, NodeFlowBean.KEY_REF, r)
          .addRange(range)
          .build()));

    }
  }
}