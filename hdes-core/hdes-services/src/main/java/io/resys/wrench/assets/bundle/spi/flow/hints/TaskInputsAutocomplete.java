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

import io.resys.hdes.client.api.ast.AstType.Direction;
import io.resys.hdes.client.api.ast.FlowAstType.NodeFlow;
import io.resys.hdes.client.api.ast.FlowAstType.NodeFlowVisitor;
import io.resys.hdes.client.api.ast.FlowAstType.FlowAstTask;
import io.resys.hdes.client.api.ast.AstDataType;
import io.resys.hdes.client.api.ast.ImmutableFlowAstType;
import io.resys.hdes.client.spi.flow.ast.FlowNodesFactory;
import io.resys.hdes.client.spi.flow.ast.beans.NodeFlowBean;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.AssetService;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceStore;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceType;
import io.resys.wrench.assets.flow.spi.support.NodeFlowAdapter;

public class TaskInputsAutocomplete extends TemplateAutocomplete implements NodeFlowVisitor {

  public TaskInputsAutocomplete(ServiceStore serviceStore) {
    super(serviceStore);
  }

  @Override
  public void visit(NodeFlow flow, ImmutableFlowAstType.Builder modelBuilder) {
    Map<String, FlowAstTask> tasks = flow.getTasks();
    if(tasks.isEmpty()) {
      return;
    }

    for(FlowAstTask taskModel : flow.getTasks().values()) {
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
      AssetService service = createQuery().type(serviceType).name(taskServiceName).get().orElse(null);
      if(service == null) {
        continue;
      }

      FlowNodesFactory.AcBuilder builder = FlowNodesFactory.ac().addField(8, "inputs");
      for(AstDataType param : service.getDataModel().getParams()) {
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
