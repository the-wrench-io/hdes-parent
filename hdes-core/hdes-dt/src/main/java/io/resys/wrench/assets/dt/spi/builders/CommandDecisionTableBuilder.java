package io.resys.wrench.assets.dt.spi.builders;

/*-
 * #%L
 * wrench-assets-dt
 * %%
 * Copyright (C) 2016 - 2018 Copyright 2016 ReSys OÜ
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
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import io.resys.hdes.client.api.HdesClient;
import io.resys.hdes.client.api.ast.AstCommand.AstCommandValue;
import io.resys.hdes.client.api.ast.AstDecision;
import io.resys.hdes.client.api.execution.DecisionProgram;
import io.resys.hdes.client.spi.util.HdesAssert;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.DecisionTableBuilder;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.DecisionTableFormat;
import io.resys.wrench.assets.dt.spi.exceptions.DecisionTableException;


public class CommandDecisionTableBuilder implements DecisionTableBuilder {
  private static final Logger LOGGER = LoggerFactory.getLogger(CommandDecisionTableBuilder.class);
  private final ObjectMapper objectMapper;
  private final HdesClient client;

  protected String src;
  protected Optional<String> rename = Optional.empty();
  protected DecisionTableFormat format;

  
  public CommandDecisionTableBuilder(
      ObjectMapper objectMapper,
      HdesClient client) {
    this.objectMapper = objectMapper;
    this.client = client;
  }

  @Override
  public DecisionProgram build() {
    try {
      
      final String src;
      if(rename.isPresent()) {
        ArrayNode array = (ArrayNode) objectMapper.readTree(this.src);
        ObjectNode renameNode = objectMapper.createObjectNode();
        renameNode.set("value", TextNode.valueOf(rename.get()));
        renameNode.set("type", TextNode.valueOf(AstCommandValue.SET_NAME.name()));
        array.add(renameNode);
        
        src = objectMapper.writeValueAsString(array);
      } else {
        src = this.src;
      }
      
      AstDecision ast = client.ast()
          .commands((ArrayNode) objectMapper.readTree(src), null)
          .decision();

      return client.program().ast(ast);

    } catch(IOException e) {
      throw new DecisionTableException(e.getMessage(), e);
    }
  }  
  @Override
  public DecisionTableBuilder src(InputStream inputStream) {
    try {
      if(inputStream != null) {
        this.src = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
      }
    } catch(IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
    return this;
  }
  @Override
  public DecisionTableBuilder src(String src) {
    this.src = src;
    return this;
  }
  @Override
  public DecisionTableBuilder src(JsonNode src) {
    if(src != null) {
      this.src = src.toString();
    }
    return this;
  }
  @Override
  public DecisionTableBuilder format(DecisionTableFormat format) {
    HdesAssert.notNull(format, () -> "format can't be null!");
    this.format = format;
    return this;
  }
  
  @Override 
  public DecisionTableBuilder rename(Optional<String> rename) {
    HdesAssert.notNull(rename, () -> "rename can't be null!");
    this.rename = rename;
    return this;
  }
}
