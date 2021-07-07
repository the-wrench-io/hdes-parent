package io.resys.wrench.assets.bundle.spi.repositories;

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

import java.util.Map;
import java.util.function.Function;

import org.springframework.util.Assert;

import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository;
import io.resys.wrench.assets.bundle.spi.builders.GenericExportBuilder;
import io.resys.wrench.assets.bundle.spi.builders.GenericServiceQuery;
import io.resys.wrench.assets.bundle.spi.hash.HashBuilder;
import io.resys.wrench.assets.dt.api.DecisionTableRepository;
import io.resys.wrench.assets.flow.api.FlowRepository;
import io.resys.wrench.assets.script.api.ScriptRepository;

public class GenericAssetServiceRepository implements AssetServiceRepository {

  private final Map<ServiceType, Function<ServiceStore, ServiceBuilder>> builders;
  private final DecisionTableRepository decisionTableRepository;
  private final FlowRepository flowRepository;
  private final ScriptRepository scriptRepository;
  private final ServiceStore serviceStore;

  public GenericAssetServiceRepository(
      DecisionTableRepository decisionTableRepository,
      FlowRepository flowRepository,
      ScriptRepository scriptRepository,
      
      Map<ServiceType, Function<ServiceStore, ServiceBuilder>> builders,
      ServiceStore serviceStore) {
    
    super();
    this.decisionTableRepository = decisionTableRepository;
    this.flowRepository = flowRepository;
    this.scriptRepository = scriptRepository;
    
    this.builders = builders;
    this.serviceStore = serviceStore;
  }

  @Override
  public ServiceBuilder createBuilder(ServiceType type) {
    Assert.isTrue(builders.containsKey(type), "No asset service builder for type: " + type + "!");
    return builders.get(type).apply(serviceStore);
  }

  @Override
  public ServiceQuery createQuery() {
    return new GenericServiceQuery(serviceStore);
  }

  @Override
  public ServiceStore createStore() {
    return serviceStore;
  }

  @Override
  public ExportBuilder createExport() {
    return new GenericExportBuilder(() -> decisionTableRepository.createExporter());
  }
  
  @Override
  public String getHash() {
    HashBuilder hashBuilder = new HashBuilder();
    createQuery().list().stream().sorted((s1, s2) -> s1.getId().compareTo(s2.getId())).forEachOrdered(hashBuilder::add);
    return hashBuilder.build();
  }

  @Override
  public DecisionTableRepository getDtRepo() {
    return decisionTableRepository;
  }
  @Override
  public ScriptRepository getStRepo() {
    return scriptRepository;
  }

  @Override
  public FlowRepository getFlRepo() {
    return flowRepository;
  }
}
