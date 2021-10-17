package io.resys.wrench.assets.bundle.spi.flow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.client.api.HdesClient;
import io.resys.hdes.client.api.ast.AstCommand;
import io.resys.hdes.client.api.ast.AstCommand.AstCommandValue;
import io.resys.hdes.client.api.ast.AstFlow.AstFlowNode;
import io.resys.hdes.client.api.ast.AstFlow.AstFlowRoot;
import io.resys.hdes.client.api.ast.ImmutableAstCommand;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.AssetService;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceBuilder;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceDataModel;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceIdGen;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceStore;
import io.resys.wrench.assets.bundle.spi.builders.ImmutableServiceBuilder;
import io.resys.wrench.assets.bundle.spi.builders.TemplateServiceBuilder;
import io.resys.wrench.assets.bundle.spi.clock.ClockRepository;

public class FlowServiceBuilder extends TemplateServiceBuilder {

  private final ServiceIdGen idGen;
  private final HdesClient hdesClient;
  private final ClockRepository clockRepository;
  private final String defaultContent;
  private final ServiceStore store;
  private final ObjectMapper objectMapper;
  private boolean rename;
  
  public FlowServiceBuilder(
      ObjectMapper objectMapper,
      ServiceIdGen idGen,
      ServiceStore store,
      HdesClient flowRepository,
      ClockRepository clockRepository, 
      String defaultContent) {
    super();
    this.objectMapper = objectMapper;
    this.idGen = idGen;
    this.store = store;
    this.hdesClient = flowRepository;
    this.clockRepository = clockRepository;
    this.defaultContent = defaultContent;
  }

  @Override
  public AssetService build() {
    boolean isDefault = StringUtils.isEmpty(src);
    String content = isDefault ? defaultContent.replace("{{id}}", name): this.src;

    final List<AstCommand> rename = new ArrayList<>();
    if(this.rename && name != null) {
      AstFlowRoot originalModel = hdesClient.ast().commands(content).flow().getSrc();
      AstFlowNode idNode = originalModel.getId();

      rename.add(ImmutableAstCommand.builder()
          .id(idNode.getStart() + "")
          .value("id: " + name)
          .type(AstCommandValue.SET)
          .build());
    }

    final var ast = hdesClient.ast().commands(content).commands(rename).flow();
    final var program = hdesClient.program().ast(ast);
    
    String serviceId = id == null ? idGen.nextId() : id;
    String pointer = serviceId + ".json";

    if(lastModified == null) {
      lastModified = clockRepository.toTimestamp();
    }

    ServiceDataModel dataModel = new FlowServiceDataModelBuilder(store).build(serviceId, program, lastModified);

    try {
      return ImmutableServiceBuilder.newFlow()
          .setId(serviceId)
          .setRev(program.getAst().getRev() + "")
          .setName(program.getId())
          .setDescription(program.getAst().getDescription())
          .setSrc(objectMapper.writeValueAsString(program.getAst().getCommands()))
          .setPointer(pointer)
          .setModel(dataModel)
          .setExecution(() -> new FlowServiceExecution(program, hdesClient))
          .build();
    } catch(IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @Override
  public ServiceBuilder rename() {
    rename = true;
    return this;
  }
}
