package io.resys.wrench.assets.bundle.spi.flowtask;

import javax.lang.model.SourceVersion;

import org.apache.commons.lang3.StringUtils;

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



import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.client.api.HdesClient;
import io.resys.hdes.client.api.ast.AstService;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.AssetService;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceBuilder;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceDataModel;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceIdGen;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceStore;
import io.resys.wrench.assets.bundle.spi.builders.ImmutableServiceBuilder;
import io.resys.wrench.assets.bundle.spi.builders.TemplateServiceBuilder;
import io.resys.wrench.assets.bundle.spi.exceptions.AssetErrorCodes;

public class FlowTaskServiceBuilder extends TemplateServiceBuilder {

  private final ServiceIdGen idGen;
  private final ServiceStore serviceStore;  
  private final HdesClient scriptRepository;
  private final ObjectMapper objectMapper;
  private final String defaultContent;
  private boolean rename;
  
  public FlowTaskServiceBuilder(
      ServiceIdGen idGen,
      ServiceStore serviceStore, 
      HdesClient client,
      ObjectMapper objectMapper, 
      String defaultContent) {
    super();
    this.idGen = idGen;
    this.scriptRepository = client;
    this.serviceStore = serviceStore;
    this.defaultContent = defaultContent;
    this.objectMapper = objectMapper;
  }

  @Override
  public AssetService build() {
    if(name != null && (!SourceVersion.isName(name) || !Character.isUpperCase(name.charAt(0)))) {
      throw AssetErrorCodes.FLOW_TASK_NAME_INVALID.newException("Flow task name must be valid UpperCamel groovy class name!");
    }

    final boolean isDefault = StringUtils.isEmpty(src);
    final String serviceId = id == null ? idGen.nextId() : id;
    final String content = isDefault ? defaultContent.replace("{{id}}", name): this.src;
    
    try {
      final AstService script;
      
      if(rename) {
        final AstService originalScript = scriptRepository.ast().commands(content).service();
        final String originalName = originalScript.getName();
        final String newContent = content.replaceAll(originalName, name);
        
        script = scriptRepository.ast().commands(newContent).service();
      } else {
        script = scriptRepository.ast().commands(content).service();
      }
            
      final String name = script.getName();
      final String pointer = serviceId + ".json";
      final ServiceDataModel dataModel = new FlowTaskServiceDataModelBuilder().build(serviceId, name, script, serviceStore);

      return ImmutableServiceBuilder.newFlowTask()
          .setId(serviceId)
          .setName(name)
          .setDescription(null)
          .setPointer(pointer)
          .setModel(dataModel)
          .setExecution(() -> new FlowTaskServiceExecution(scriptRepository.program().ast(script), scriptRepository))
          .build();

    } catch (Exception e) {
      String name = this.pointer == null ? this.name : this.pointer;
      throw AssetErrorCodes.FLOW_TASK_ERROR.newException(
          e, 
          "AssetResource error in: " + name + System.lineSeparator() + 
          e.getMessage());
    }
  }

  @Override
  public ServiceBuilder rename() {
    rename = true;
    return this;
  }
}
