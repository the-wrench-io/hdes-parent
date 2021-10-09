package io.resys.wrench.assets.bundle.spi.dt;

import java.util.Optional;

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

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import io.resys.hdes.client.api.execution.DecisionProgram;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.AssetService;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceBuilder;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceDataModel;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceIdGen;
import io.resys.wrench.assets.bundle.spi.builders.ImmutableServiceBuilder;
import io.resys.wrench.assets.bundle.spi.builders.TemplateServiceBuilder;
import io.resys.wrench.assets.bundle.spi.clock.ClockRepository;
import io.resys.wrench.assets.dt.api.DecisionTableRepository;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.DecisionTableFormat;

public class DtServiceBuilder extends TemplateServiceBuilder {

  private final ServiceIdGen serviceStore;
  private final DecisionTableRepository decisionTableRepository;
  private final ClockRepository clockRepository;
  private final String defaultContent;
  private boolean rename;
  
  public DtServiceBuilder(
      ServiceIdGen serviceStore,
      DecisionTableRepository decisionTableRepository,
      ClockRepository clockRepository,
      String defaultContent) {
    super();
    this.serviceStore = serviceStore;
    this.clockRepository = clockRepository;
    this.decisionTableRepository = decisionTableRepository;
    this.defaultContent = defaultContent;
  }

  @Override
  public AssetService build() {
    if(StringUtils.isEmpty(src) ) {
      Assert.isTrue(!StringUtils.isEmpty(name), "Decision table name must be defined!");
    }
    String content = StringUtils.isEmpty(src) ? defaultContent.replace("{{name}}", name) : src;
    DecisionProgram decisionTable = decisionTableRepository.createBuilder()
        .format(DecisionTableFormat.JSON)
        .src(content)
        .rename(rename ? Optional.of(name) : Optional.empty())
        .build();

    String serviceId = id == null ? serviceStore.nextId() : id;
    ServiceDataModel dataModel = new DtServiceDataModelBuilder().build(serviceId, decisionTable);
    String pointer = serviceId + ".json";

    return ImmutableServiceBuilder.newDt()
        .setId(serviceId)
        .setRev(decisionTable.getAst().getRev() + "")
        .setName(decisionTable.getId())
        .setDescription(decisionTable.getAst().getDescription())
        .setSrc(decisionTable.getAst().getSource())
        .setPointer(pointer)
        .setModel(dataModel)
        .setExecution(() -> new DtServiceExecution(decisionTableRepository, decisionTable))
        .build();
  }

  @Override
  public ServiceBuilder rename() {
    rename = true;
    return this;
  }
}
