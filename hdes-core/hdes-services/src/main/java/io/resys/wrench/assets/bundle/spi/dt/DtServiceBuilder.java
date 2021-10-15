package io.resys.wrench.assets.bundle.spi.dt;

import java.util.Arrays;
import java.util.Collections;

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

import io.resys.hdes.client.api.HdesClient;
import io.resys.hdes.client.api.ast.AstCommand.AstCommandValue;
import io.resys.hdes.client.api.ast.ImmutableAstCommand;
import io.resys.hdes.client.api.programs.DecisionProgram;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.AssetService;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceBuilder;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceDataModel;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceIdGen;
import io.resys.wrench.assets.bundle.spi.builders.ImmutableServiceBuilder;
import io.resys.wrench.assets.bundle.spi.builders.TemplateServiceBuilder;
import io.resys.wrench.assets.bundle.spi.clock.ClockRepository;

public class DtServiceBuilder extends TemplateServiceBuilder {

  private final ServiceIdGen serviceStore;
  private final HdesClient hdesClient;
  private final ClockRepository clockRepository;
  private final String defaultContent;
  private boolean rename;
  
  public DtServiceBuilder(
      ServiceIdGen serviceStore,
      HdesClient decisionTableRepository,
      ClockRepository clockRepository,
      String defaultContent) {
    super();
    this.serviceStore = serviceStore;
    this.clockRepository = clockRepository;
    this.hdesClient = decisionTableRepository;
    this.defaultContent = defaultContent;
  }

  @Override
  public AssetService build() {
    if(StringUtils.isEmpty(src) ) {
      Assert.isTrue(!StringUtils.isEmpty(name), "Decision table name must be defined!");
    }
    String content = StringUtils.isEmpty(src) ? defaultContent.replace("{{name}}", name) : src;
    
    final var ast = hdesClient.ast()
      .commands(content)
      .commands(rename ? 
          Arrays.asList(ImmutableAstCommand.builder().type(AstCommandValue.SET_NAME).value(name).build()) : 
          Collections.emptyList())
      .decision();
    
    DecisionProgram decisionTable = hdesClient.program().ast(ast);
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
        .setExecution(() -> new DtServiceExecution(hdesClient, decisionTable))
        .build();
  }

  @Override
  public ServiceBuilder rename() {
    rename = true;
    return this;
  }
}
