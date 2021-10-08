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

import io.resys.hdes.client.api.ast.AstCommand;
import io.resys.hdes.client.api.ast.AstCommand.AstCommandValue;
import io.resys.hdes.client.api.ast.ImmutableAstCommand;
import io.resys.hdes.client.api.ast.TypeDef.Direction;
import io.resys.hdes.client.api.execution.DecisionProgram.DecisionTableDataType;
import io.resys.hdes.client.api.execution.DecisionProgram.Row;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.DecisionTableExporter;
import io.resys.wrench.assets.dt.spi.exceptions.DecisionTableException;

public class CommandModelDecisionTableExporter extends TemplateDecisionTableExporter implements DecisionTableExporter {

  private final ObjectMapper objectMapper;

  public CommandModelDecisionTableExporter(ObjectMapper objectMapper) {
    super();
    this.objectMapper = objectMapper;
  }

  @Override
  public String build() {
    try {
      return objectMapper.writeValueAsString(buildCommands());
    } catch(IOException e) {
      throw new DecisionTableException(e.getMessage(), e);
    }
  }
  
  public List<AstCommand> buildCommands() {
    List<DecisionTableDataType> headers = dt.getTypes();
    List<AstCommand> result = createHeaderCommands(headers);
    createRow(headers, 1, dt.getRows(), result);
    result.add(ImmutableAstCommand.builder().value(dt.getId()).type(AstCommandValue.SET_NAME).build());
    result.add(ImmutableAstCommand.builder().value(dt.getDescription()).type(AstCommandValue.SET_DESCRIPTION).build());
    result.add(ImmutableAstCommand.builder().value(dt.getHitPolicy().name()).type(AstCommandValue.SET_HIT_POLICY).build());
    return result;
  }

  private void createRow(List<DecisionTableDataType> headers, int rows, Row node, List<AstCommand> result) {
    if(node == null) {
      return;
    }
    int nextId = headers.size() * rows + rows;
    result.add(ImmutableAstCommand.builder().type(AstCommandValue.ADD_ROW).build());

    Map<String, Object> entries = new HashMap<>();
    node.getAccepts().forEach(e -> entries.put(e.getKey().getName(), e.getExpression()));
    node.getReturns().forEach(e -> entries.put(e.getKey().getName(), e.getValue()));

    for(DecisionTableDataType header : headers) {
      Object value = entries.get(header.getExpression().getName());
      result.add(ImmutableAstCommand.builder()
          .id(String.valueOf(nextId++))
          .value(value == null ? null : header.getExpression().getSerializer().serialize(header.getExpression(), value))
          .type(AstCommandValue.SET_CELL_VALUE)
          .build());
    }
    createRow(headers, ++rows, node.getNext(), result);
  }

  private List<AstCommand> createHeaderCommands(List<DecisionTableDataType> headers) {
    List<AstCommand> result = new ArrayList<>();
    int index = 0;
    for(DecisionTableDataType dataType : headers) {
      String id = String.valueOf(index);
      result.add(ImmutableAstCommand.builder().type(dataType.getExpression().getDirection() == Direction.IN ? AstCommandValue.ADD_HEADER_IN : AstCommandValue.ADD_HEADER_OUT).build());
      result.add(ImmutableAstCommand.builder().id(id).value(dataType.getExpression().getName()).type(AstCommandValue.SET_HEADER_REF).build());
      result.add(ImmutableAstCommand.builder().id(id).value(dataType.getScript()).type(AstCommandValue.SET_HEADER_SCRIPT).build());
      result.add(ImmutableAstCommand.builder().id(id).value(dataType.getExpression().getValueType() == null ? null : dataType.getExpression().getValueType().name()).type(AstCommandValue.SET_HEADER_TYPE).build());
      index++;
    }
    return result;
  }
}
