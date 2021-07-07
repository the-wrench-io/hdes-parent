package io.resys.wrench.assets.bundle.spi.postprocessors;

import java.util.Map;
import java.util.function.Function;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.Service;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceAssociation;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceBuilder;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServicePostProcessor;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceStore;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceType;
import io.resys.wrench.assets.bundle.spi.builders.GenericServiceQuery;

public class FlowDependencyServicePostProcessor implements ServicePostProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(FlowDependencyServicePostProcessor.class);

  private final Map<ServiceType, Function<ServiceStore, ServiceBuilder>> builders;

  public FlowDependencyServicePostProcessor(Map<ServiceType, Function<ServiceStore, ServiceBuilder>> builders) {
    super();
    this.builders = builders;
  }

  @Override
  public void process(ServiceStore store, Service oldState, Service newState) {
    Assert.isTrue(newState.getType() != ServiceType.FLOW, "Only flows can rebuild from dependencies, not vica versa!");

    new GenericServiceQuery(store).type(ServiceType.FLOW).list().stream()
    .filter(f -> isDependency(f, oldState) || isDependency(f, newState))
    .forEach(f -> buildFlow(f, store));
  }

  @Override
  public void delete(ServiceStore store, Service state) {
    Assert.isTrue(state.getType() != ServiceType.FLOW, "Only flows can rebuild from dependencies, not vica versa!");

    new GenericServiceQuery(store).type(ServiceType.FLOW).list().stream()
    .filter(f -> isDependency(f, state))
    .forEach(f -> buildFlow(f, store));
  }

  protected void buildFlow(Service flow, ServiceStore store) {
    Assert.isTrue(flow.getType() == ServiceType.FLOW, "Only flows can rebuild from dependencies!");

    try {
      Service newFlowState = builders.get(flow.getType()).apply(store)
      .id(flow.getId())
      .src(flow.getSrc())
      .name(flow.getName())
      .build();
      store.save(newFlowState);
    } catch(Exception e) {
      LOGGER.error("Failed to rebuild flow: " + flow.getName() + "!" + System.lineSeparator() + e.getMessage(), e);
    }
  }

  protected boolean isDependency(Service flow, Service dependency) {
    if(dependency == null) {
      return false;
    }
    return flow.getDataModel().getAssociations().stream()
        .filter(a -> isDependency(a, dependency))
        .findFirst().isPresent();
  }

  protected boolean isDependency(ServiceAssociation assoc, Service dependency) {
    if(assoc.getServiceType() != dependency.getType()) {
      return false;
    }

    return assoc.getName().equals(dependency.getName());
  }
}
