package io.resys.hdes.decisiontable.spi.exporters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import io.resys.hdes.datatype.api.DataType;
import io.resys.hdes.datatype.api.DataTypeCommand;
import io.resys.hdes.datatype.api.DataTypeService;
import io.resys.hdes.datatype.api.ImmutableDataTypeCommand;
import io.resys.hdes.decisiontable.api.DecisionTableAst;
import io.resys.hdes.decisiontable.api.DecisionTableCommandType;

public class CommandModelDecisionTableExporter extends TemplateDecisionTableExporter {

  private final DataTypeService objectMapper;

  public CommandModelDecisionTableExporter(DataTypeService objectMapper) {
    super();
    this.objectMapper = objectMapper;
  }

  @Override
  public String build() {
    List<DecisionTableAst.RuleType> headers = dt.getTypes();
    List<DataTypeCommand> result = createHeaderCommands(headers);
    createRow(headers, 1, dt.getNode(), result);
    result.add(ImmutableDataTypeCommand.builder().value(dt.getId()).type(DecisionTableCommandType.SET_NAME.toString()).build());
    return objectMapper.write().type(result).build();
  }

  private void createRow(List<DecisionTableAst.RuleType> headers, int rows,
                         DecisionTableAst.Node node, List<DataTypeCommand> result) {
    if(node == null) {
      return;
    }
    int nextId = headers.size() * rows + rows;
    result.add(ImmutableDataTypeCommand.builder().type(DecisionTableCommandType.ADD_ROW.toString()).build());

    Map<String, Object> entries = new HashMap<>();
    node.getInputs().forEach((key, value) -> entries.put(key.getName(), value));
    node.getOutputs().forEach((key, value) -> entries.put(key.getName(), value));

    for(DecisionTableAst.RuleType header : headers) {
      Object value = entries.get(header.getValue().getName());
      result.add(ImmutableDataTypeCommand.builder()
        .id(nextId++)
        .value(value == null ? null : objectMapper.mapper(header.getValue().getValueType()).toString(value, header.getValue()))
        .type(DecisionTableCommandType.SET_CELL_VALUE.toString()).build());
    }
    createRow(headers, ++rows, node.getNext(), result);
  }

  private List<DataTypeCommand> createHeaderCommands(List<DecisionTableAst.RuleType> headers) {
    List<DataTypeCommand> result = new ArrayList<>();
    int index = 0;
    for(DecisionTableAst.RuleType dataType : headers) {
      int id = index;

      result.add(ImmutableDataTypeCommand.builder().type((
        dataType.getValue().getDirection() == DataType.Direction.IN ?
        DecisionTableCommandType.ADD_HEADER_IN : DecisionTableCommandType.ADD_HEADER_OUT).toString()).build());

      result.add(ImmutableDataTypeCommand.builder()
        .id(id)
        .value(dataType.getValue().getName())
        .type(DecisionTableCommandType.SET_HEADER_REF.toString()).build());

      result.add(ImmutableDataTypeCommand.builder()
        .id(id)
        .value(dataType.getValue().getValueType() == null ? null : dataType.getValue().getValueType().name())
        .type(DecisionTableCommandType.SET_HEADER_TYPE.toString()).build());
      index++;
    }
    return result;
  }
}
