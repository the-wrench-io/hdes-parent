package io.resys.wrench.assets.dt.spi.export;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.wrench.assets.datatype.api.DataTypeRepository.Direction;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.DecisionTableExporter;
import io.resys.wrench.assets.dt.api.model.DecisionTable.DecisionTableDataType;
import io.resys.wrench.assets.dt.api.model.DecisionTable.DecisionTableNode;
import io.resys.wrench.assets.dt.api.model.DecisionTableAst.Command;
import io.resys.wrench.assets.dt.api.model.DecisionTableAst.CommandType;
import io.resys.wrench.assets.dt.api.model.ImmutableCommand;
import io.resys.wrench.assets.dt.spi.exceptions.DecisionTableException;

public class CommandModelDecisionTableExporter extends TemplateDecisionTableExporter implements DecisionTableExporter {

  private final ObjectMapper objectMapper;

  public CommandModelDecisionTableExporter(ObjectMapper objectMapper) {
    super();
    this.objectMapper = objectMapper;
  }

  @Override
  public String build() {
    List<DecisionTableDataType> headers = dt.getTypes();
    List<Command> result = createHeaderCommands(headers);
    createRow(headers, 1, dt.getNode(), result);
    result.add(ImmutableCommand.builder().value(dt.getId()).type(CommandType.SET_NAME).build());
    result.add(ImmutableCommand.builder().value(dt.getDescription()).type(CommandType.SET_DESCRIPTION).build());

    try {
      return objectMapper.writeValueAsString(result);
    } catch(IOException e) {
      throw new DecisionTableException(e.getMessage(), e);
    }
  }

  private void createRow(List<DecisionTableDataType> headers, int rows, DecisionTableNode node, List<Command> result) {
    if(node == null) {
      return;
    }
    int nextId = headers.size() * rows + rows;
    result.add(ImmutableCommand.builder().type(CommandType.ADD_ROW).build());

    Map<String, Object> entries = new HashMap<>();
    node.getInputs().forEach(e -> entries.put(e.getKey().getName(), e.getValue()));
    node.getOutputs().forEach(e -> entries.put(e.getKey().getName(), e.getValue()));

    for(DecisionTableDataType header : headers) {
      Object value = entries.get(header.getValue().getName());
      result.add(ImmutableCommand.builder()
          .id(String.valueOf(nextId++))
          .value(value == null ? null : header.getValue().getSerializer().serialize(header.getValue(), value))
          .type(CommandType.SET_CELL_VALUE)
          .build());
    }
    createRow(headers, ++rows, node.getNext(), result);
  }

  private List<Command> createHeaderCommands(List<DecisionTableDataType> headers) {
    List<Command> result = new ArrayList<>();
    int index = 0;
    for(DecisionTableDataType dataType : headers) {
      String id = String.valueOf(index);
      result.add(ImmutableCommand.builder().type(dataType.getValue().getDirection() == Direction.IN ? CommandType.ADD_HEADER_IN : CommandType.ADD_HEADER_OUT).build());
      result.add(ImmutableCommand.builder().id(id).value(dataType.getValue().getName()).type(CommandType.SET_HEADER_REF).build());
      result.add(ImmutableCommand.builder().id(id).value(dataType.getScript()).type(CommandType.SET_HEADER_SCRIPT).build());
      result.add(ImmutableCommand.builder().id(id).value(dataType.getValue().getValueType() == null ? null : dataType.getValue().getValueType().name()).type(CommandType.SET_HEADER_TYPE).build());
      index++;
    }
    return result;
  }
}
