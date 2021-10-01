package io.resys.wrench.assets.script.spi.builders;

/*-
 * #%L
 * wrench-assets-script
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.resys.hdes.client.api.ast.AstType.AstCommandType;
import io.resys.hdes.client.api.ast.AstType.AstCommandType.AstCommandValue;
import io.resys.hdes.client.api.ast.ImmutableAstCommandType;

public class GroovyScriptParser {

  private final ObjectMapper objectMapper;

  public GroovyScriptParser(ObjectMapper objectMapper) {
    super();
    this.objectMapper = objectMapper;
  }

  public Map.Entry<String, List<AstCommandType>> parse(JsonNode node, Integer rev) {
    try {
      GrooovyCommandBuilder commandBuilder = new GrooovyCommandBuilder();
      List<AstCommandType> commands = new ArrayList<>();
      ArrayNode sourceCommands = (ArrayNode)  (node.isArray() ? node : node.get("commands"));

      if(rev != null) {
        int runningVersion = 0;
        for(JsonNode command : sourceCommands) {
          if(runningVersion++ > rev) {
            break;
          }
          create(commands, (ObjectNode) command, commandBuilder);
        }
      } else {
        sourceCommands
        .forEach(command -> create(commands, (ObjectNode) command, commandBuilder));
      }

      String src = commandBuilder.build();
      return new AbstractMap.SimpleImmutableEntry<String, List<AstCommandType>>(src, commands);
    } catch(Exception e) {
      throw new RuntimeException("Incorrect script format, for format conversions use: new FlowTaskFlatToCommandExporter().build(\"src/main/resources\")!" + System.lineSeparator() + e.getMessage(), e);
    }
  }

  public Map.Entry<String, List<AstCommandType>> parse(String input, Integer rev) {
    try {



      JsonNode node = objectMapper.readTree(input);
      return parse(node, rev);
    } catch(IOException e) {
      throw new RuntimeException("Incorrect script format, for format conversions use: new FlowTaskFlatToCommandExporter().build(\"src/main/resources\")!" + System.lineSeparator() + e.getMessage(), e);
    }
  }

  private void create(List<AstCommandType> commands, ObjectNode src, GrooovyCommandBuilder builder) {
    int line = src.get("id").asInt();
    AstCommandValue type = AstCommandValue.valueOf(src.get("type").asText());
    JsonNode valueNode = src.hasNonNull("value") ? src.get("value") : null;
    String value = valueNode == null ? null : valueNode.asText();
    AstCommandType command = ImmutableAstCommandType.builder().id(String.valueOf(line)).value(value).type(type).build();
    builder.add(command);
    commands.add(command);
  }
}
