package io.resys.wrench.assets.bundle.spi.flow.hints;

import io.resys.hdes.client.api.ast.FlowAstType.NodeTask;

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

import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceQuery;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceStore;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceType;
import io.resys.wrench.assets.bundle.spi.builders.GenericServiceQuery;

public class TemplateAutocomplete {

  private final ServiceStore serviceStore;

  public TemplateAutocomplete(ServiceStore serviceStore) {
    super();
    this.serviceStore = serviceStore;
  }

  protected ServiceType getServiceType(NodeTask taskModel) {
    if(taskModel.getDecisionTable() != null) {
      return ServiceType.DT;
    } else if(taskModel.getService() != null) {
      return ServiceType.FLOW_TASK;
    }

    return null;
  }

  protected ServiceQuery createQuery() {
    return new GenericServiceQuery(serviceStore);
  }
}
