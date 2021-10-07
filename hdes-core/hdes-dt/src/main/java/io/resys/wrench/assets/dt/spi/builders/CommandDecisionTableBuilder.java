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
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import io.resys.hdes.client.api.HdesAstTypes;
import io.resys.hdes.client.api.ast.AstCommand.AstCommandValue;
import io.resys.hdes.client.api.ast.TypeDef;
import io.resys.hdes.client.api.ast.TypeDef.Direction;
import io.resys.hdes.client.api.ast.AstDecision;
import io.resys.hdes.client.api.ast.AstDecision.Cell;
import io.resys.hdes.client.api.ast.AstDecision.Row;
import io.resys.hdes.client.api.model.DecisionTableModel;
import io.resys.hdes.client.api.model.DecisionTableModel.DecisionTableDataType;
import io.resys.hdes.client.api.model.DecisionTableModel.DecisionTableNode;
import io.resys.hdes.client.spi.util.Assert;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.DecisionTableBuilder;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.DecisionTableFormat;
import io.resys.wrench.assets.dt.spi.beans.ImmutableDecisionTable;
import io.resys.wrench.assets.dt.spi.beans.ImmutableDecisionTableDataType;
import io.resys.wrench.assets.dt.spi.beans.ImmutableDecisionTableNode;
import io.resys.wrench.assets.dt.spi.exceptions.DecisionTableException;


public class CommandDecisionTableBuilder implements DecisionTableBuilder {
  private static final Logger LOGGER = LoggerFactory.getLogger(CommandDecisionTableBuilder.class);
  private final ObjectMapper objectMapper;
  private final Supplier<HdesAstTypes> commandBuilder;
  private final List<String> errors = new ArrayList<>();

  protected String src;
  protected Optional<String> rename = Optional.empty();
  protected DecisionTableFormat format;

  
  public CommandDecisionTableBuilder(
      ObjectMapper objectMapper,
      Supplier<HdesAstTypes> commandBuilder) {
    this.objectMapper = objectMapper;
    this.commandBuilder = commandBuilder;
  }

  @Override
  public DecisionTableModel build() {
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
      
      AstDecision commandModel = commandBuilder.get().decision()
          .src(objectMapper.readTree(src))
          .build();

      List<DecisionTableDataType> types = createTypes(commandModel);
      Map<Integer, TypeDef> typesById = types.stream().collect(Collectors.toMap(t -> t.getOrder(), t -> t.getValue()));

      DecisionTableNode first = null;
      ImmutableDecisionTableNode previous = null;
      for(Row row : commandModel.getRows()) {
        int id = previous == null ? 0 : previous.getId() + 1;
        ImmutableDecisionTableNode current = new ImmutableDecisionTableNode(id, row.getOrder(), getInputs(typesById, row), getOutputs(typesById, row), previous);
        if(first == null) {
          first = current;
        }
        if(previous != null) {
          previous.setNext(current);
        }
        previous = current;
      }

      if(!errors.isEmpty()) {
        LOGGER.error(new StringBuilder()
            .append("Error in DT: ").append(commandModel.getName())
            .append(System.lineSeparator())
            .append(String.join(System.lineSeparator(), errors))
            .toString());
      }
      
      return new ImmutableDecisionTable(
          commandModel.getName(), String.valueOf(commandModel.getRev()), 
          src,
          commandModel.getDescription(), commandModel.getHitPolicy(), types, first);

    } catch(IOException e) {
      throw new DecisionTableException(e.getMessage(), e);
    }
  }

  protected List<DecisionTableDataType> createTypes(AstDecision data) {
    List<DecisionTableDataType> result = new ArrayList<>();
    int index = 0;
    
    List<TypeDef> allHeaders = new ArrayList<>();
    allHeaders.addAll(data.getHeaders().getAcceptDefs());
    allHeaders.addAll(data.getHeaders().getReturnDefs());
    
    for(TypeDef header : allHeaders) {
      result.add(new ImmutableDecisionTableDataType(
          index++, header.getScript(), header));
    }
    Collections.sort(result);
    return Collections.unmodifiableList(result);
  }

  protected Map<TypeDef, String> getInputs(Map<Integer, TypeDef> typesById, Row entry) {
    Map<TypeDef, String> result = new HashMap<>();
    int index = 0;
    for(Cell value : entry.getCells()) {
      TypeDef type = typesById.get(index++);
      if(type.getDirection() == Direction.IN) {
        result.put(type, value.getValue());
      }

    }
    return Collections.unmodifiableMap(result);
  }

  protected Map<TypeDef, Serializable> getOutputs(Map<Integer, TypeDef> typesById, Row entry) {
    Map<TypeDef, Serializable> result = new HashMap<>();
    int index = 0;
    for(Cell value : entry.getCells()) {
      TypeDef type = typesById.get(index++);
      if(type.getDirection() == Direction.OUT) {
        try {
          result.put(type, type.toValue(value.getValue()));
        } catch (Exception e) {
          result.put(type, null);
          errors.add("Error in output column type parsing: " + type.getName());
        }
      }
    }
    return Collections.unmodifiableMap(result);
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
    Assert.notNull(format, () -> "format can't be null!");
    this.format = format;
    return this;
  }
  
  @Override 
  public DecisionTableBuilder rename(Optional<String> rename) {
    Assert.notNull(rename, () -> "rename can't be null!");
    this.rename = rename;
    return this;
  }
}
