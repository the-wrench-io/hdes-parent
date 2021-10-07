package io.resys.wrench.assets.script.spi.builders;

/*-
 * #%L
 * wrench-component-script
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
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.resys.hdes.client.api.HdesAstTypes;
import io.resys.hdes.client.api.ast.AstService;
import io.resys.hdes.client.api.ast.ImmutableAstService;
import io.resys.hdes.client.api.ast.ImmutableHeaders;
import io.resys.hdes.client.api.execution.Service;
import io.resys.hdes.client.spi.util.Assert;
import io.resys.wrench.assets.script.api.ScriptRepository.ScriptBuilder;
import io.resys.wrench.assets.script.spi.ServiceHistoric;
import io.resys.wrench.assets.script.spi.ServiceTemplate;

public class GenericScriptBuilder implements ScriptBuilder {
  private static final Charset UTF_8 = Charset.forName("utf-8");

  private final HdesAstTypes dataTypeRepository;
  private final ObjectMapper objectMapper;

  private String src;
  private Integer rev;
  private JsonNode jsonNode;

  public GenericScriptBuilder(HdesAstTypes dataTypeRepository, ObjectMapper objectMapper) {
    super();
    this.dataTypeRepository = dataTypeRepository;
    this.objectMapper = objectMapper;
  }

  @Override
  public ScriptBuilder src(String src) {
    this.src = src;
    return this;
  }

  @Override
  public ScriptBuilder src(InputStream src) {
    try {
      this.src = IOUtils.toString(src, UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
    return this;
  }

  @Override
  public ScriptBuilder src(JsonNode jsonNode) {
    this.jsonNode = jsonNode;
    return this;
  }

  @Override
  public ScriptBuilder rev(Integer rev) {
    this.rev = rev;
    return this;
  }

  @Override
  public Service build() {
    Assert.isTrue(src != null || jsonNode != null, () -> "src can't be null!");
    if (src != null) {
      try {
        jsonNode = objectMapper.readTree(src);
      } catch (IOException e) {
        throw new RuntimeException(e.getMessage(), e);
      }
    }

    final ArrayNode sourceCommands = (ArrayNode) (jsonNode.isArray() ? jsonNode : jsonNode.get("commands"));
    final var ast = dataTypeRepository.service().src(sourceCommands).build();
    try {
      final Class<?> beanType = ast.getType();
      return new ServiceTemplate(ast, beanType);
    } catch (Exception e) {
      if (this.rev != null) {
        AstService model = ImmutableAstService.builder()
            .name("historic")
            .src(ast.getSrc())
            .commands(ast.getCommands())
            .rev(ast.getCommands().size())
            .headers(ImmutableHeaders.builder().build())
            .build();
        return new ServiceHistoric(model);
      }
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
