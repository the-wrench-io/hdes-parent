package io.resys.wrench.assets.flow.spi.builders;

/*-
 * #%L
 * wrench-component-flow
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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.client.api.model.FlowModel;
import io.resys.wrench.assets.datatype.api.DataTypeRepository;
import io.resys.wrench.assets.flow.api.FlowAstFactory;
import io.resys.wrench.assets.flow.api.FlowRepository;
import io.resys.wrench.assets.flow.api.FlowRepository.FlowModelBuilder;
import io.resys.wrench.assets.flow.spi.expressions.ExpressionFactory;

public class GenericFlowModelBuilder implements FlowRepository.FlowModelBuilder {

  private final ExpressionFactory parser;
  private final DataTypeRepository dataTypeRepository;
  private final FlowAstFactory nodeRepository;
  private final ObjectMapper objectMapper;

  private String input;
  protected boolean dryRun;

  private Optional<String> rename;
  
  public GenericFlowModelBuilder(
      ExpressionFactory parser,
      DataTypeRepository dataTypeRepository,
      FlowAstFactory nodeRepository,
      ObjectMapper objectMapper) {
    this.dataTypeRepository = dataTypeRepository;
    this.parser = parser;
    this.nodeRepository = nodeRepository;
    this.objectMapper = objectMapper;
  }

  @Override
  public FlowModelBuilder stream(InputStream inputStream) {
    try {
      this.input = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    } catch(IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
    return this;
  }
  @Override
  public FlowModelBuilder content(String input) {
    this.input = input;
    return this;
  }
  @Override
  public FlowModelBuilder dryRun() {
    this.dryRun = true;
    return this;
  }
  @Override
  public Map.Entry<String, FlowModel> build() {
    return new CommandFlowModelBuilder(
        nodeRepository, objectMapper, dataTypeRepository, parser, input, 
        rename == null ? Optional.empty() : rename).build();
  }

  @Override
  public FlowModelBuilder rename(Optional<String> rename) {
    this.rename = rename;
    return this;
  }
}
