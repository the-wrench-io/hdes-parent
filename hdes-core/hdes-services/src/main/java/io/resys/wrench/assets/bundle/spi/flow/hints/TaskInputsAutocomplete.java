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

import java.util.Map;

import org.springframework.util.StringUtils;

import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.Service;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceStore;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceType;
import io.resys.wrench.assets.datatype.api.DataTypeRepository.DataType;
import io.resys.wrench.assets.datatype.api.DataTypeRepository.Direction;
import io.resys.wrench.assets.flow.api.FlowAstFactory.NodeFlow;
import io.resys.wrench.assets.flow.api.FlowAstFactory.NodeTask;
import io.resys.wrench.assets.flow.api.model.FlowAst.NodeFlowVisitor;
import io.resys.wrench.assets.flow.api.model.ImmutableFlowAst;
import io.resys.wrench.assets.flow.spi.model.NodeFlowBean;
import io.resys.wrench.assets.flow.spi.support.FlowNodesFactory;
import io.resys.wrench.assets.flow.spi.support.NodeFlowAdapter;

public class TaskInputsAutocomplete extends TemplateAutocomplete implements NodeFlowVisitor {

  public TaskInputsAutocomplete(ServiceStore serviceStore) {
    super(serviceStore);
  }

  @Override
  public void visit(NodeFlow flow, ImmutableFlowAst.Builder modelBuilder) {
    Map<String, NodeTask> tasks = flow.getTasks();
    if(tasks.isEmpty()) {
      return;
    }

    for(NodeTask taskModel : flow.getTasks().values()) {
      if(taskModel.getRef() == null) {
        continue;
      }

      if(taskModel.getRef().get(NodeFlowBean.KEY_INPUTS) != null) {
        continue;
      }

      String taskServiceName = NodeFlowAdapter.getStringValue(taskModel.getRef().getRef());
      ServiceType serviceType = getServiceType(taskModel);
      if(serviceType == null || StringUtils.isEmpty(taskServiceName)) {
        continue;
      }
      Service service = createQuery().type(serviceType).name(taskServiceName).get().orElse(null);
      if(service == null) {
        continue;
      }

      FlowNodesFactory.AcBuilder builder = FlowNodesFactory.ac().addField(8, "inputs");
      for(DataType param : service.getDataModel().getParams()) {
        if(param.getDirection() == Direction.IN) {
          builder.addField(10, param.getName());
        }
      }

      modelBuilder.addAutocomplete(builder
          .id(TaskInputsAutocomplete.class.getSimpleName())
          .addRange(FlowNodesFactory.range().build(taskModel.getRef().getStart(), taskModel.getRef().getEnd(), true))
          .build());
    }
  }
}
