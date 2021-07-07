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

import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.Service;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceStore;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceType;
import io.resys.wrench.assets.flow.api.FlowAstFactory.NodeFlow;
import io.resys.wrench.assets.flow.api.FlowAstFactory.NodeRef;
import io.resys.wrench.assets.flow.api.FlowAstFactory.NodeTask;
import io.resys.wrench.assets.flow.api.model.FlowAst.FlowCommandRange;
import io.resys.wrench.assets.flow.api.model.FlowAst.NodeFlowVisitor;
import io.resys.wrench.assets.flow.api.model.ImmutableFlowAst;
import io.resys.wrench.assets.flow.spi.model.NodeFlowBean;
import io.resys.wrench.assets.flow.spi.support.FlowNodesFactory;

public class TaskRefAutocomplete extends TemplateAutocomplete implements NodeFlowVisitor {

  public TaskRefAutocomplete(ServiceStore serviceStore) {
    super(serviceStore);
  }

  @Override
  public void visit(NodeFlow flow, ImmutableFlowAst.Builder modelBuilder) {
    Map<String, NodeTask> tasks = flow.getTasks();
    if(tasks.isEmpty()) {
      return;
    }

    Map<ServiceType, List<String>> services = new HashMap<>();
    for(Service service : createQuery().list()) {
      List<String> serviceNames = services.get(service.getType());
      if(serviceNames == null) {
        serviceNames = new ArrayList<>();
        services.put(service.getType(), serviceNames);
      }
      serviceNames.add(service.getName());
    }


    for(NodeTask task : tasks.values()) {
      NodeRef ref = task.getRef();
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

      FlowCommandRange range;
      if(ref.getRef() == null) {
        range = FlowNodesFactory.range().build(ref.getStart(), ref.getEnd(), true);
      } else {
        range = FlowNodesFactory.range().build(task.getRef().getStart(), task.getRef().getEnd(), false);
      }

      refs.forEach(r -> modelBuilder.addAutocomplete(FlowNodesFactory.ac().id(TaskRefAutocomplete.class.getSimpleName())
          .addField(8, NodeFlowBean.KEY_REF, r)
          .addRange(range)
          .build()));

    }
  }
}
