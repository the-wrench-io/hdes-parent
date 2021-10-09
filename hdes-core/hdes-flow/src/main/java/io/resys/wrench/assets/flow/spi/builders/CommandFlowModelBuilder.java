package io.resys.wrench.assets.flow.spi.builders;

/*-
 * #%L
 * wrench-assets-flow
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

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import io.resys.hdes.client.api.HdesClient;
import io.resys.hdes.client.api.ast.AstCommand.AstCommandValue;
import io.resys.hdes.client.api.ast.AstFlow;
import io.resys.hdes.client.api.ast.AstFlow.AstFlowNode;
import io.resys.hdes.client.api.ast.AstFlow.AstFlowRoot;
import io.resys.hdes.client.api.execution.FlowProgram;

public class CommandFlowModelBuilder {
  private final HdesClient client;
  private final ObjectMapper objectMapper;
  private final String input;
  private Optional<String> rename;

  public CommandFlowModelBuilder(
      HdesClient client,
      ObjectMapper objectMapper,
      String input, Optional<String> rename) {
    super();
    this.client = client;
    this.objectMapper = objectMapper;
    this.input = input;
    this.rename = rename;
  }

  public Map.Entry<String, FlowProgram> build() {
    try {
      final AstFlow data;
      final ArrayNode src;
      final String input;
      
      if(rename.isPresent()) {
        
        ArrayNode original = (ArrayNode) objectMapper.readTree(this.input);
        

        AstFlowRoot originalModel = client.ast().commands(original).flow().getSrc();
        AstFlowNode idNode = originalModel.getId();
        
        ObjectNode renameNode = objectMapper.createObjectNode();
        renameNode.set("id", IntNode.valueOf(idNode.getStart()));
        renameNode.set("type", TextNode.valueOf(AstCommandValue.SET.name()));
        renameNode.set("value", TextNode.valueOf("id: " + rename.get()));
        original.add(renameNode);
        
        input = objectMapper.writeValueAsString(original);
        src = (ArrayNode) objectMapper.readTree(input);
        data = client.ast().commands(src).flow();        
      } else {
        input = this.input;
        src = (ArrayNode) objectMapper.readTree(input);
        data = client.ast().commands(src).flow();
      }
      
      
      return new AbstractMap.SimpleEntry<String, FlowProgram>(input, client.program().ast(data));
    } catch(IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
