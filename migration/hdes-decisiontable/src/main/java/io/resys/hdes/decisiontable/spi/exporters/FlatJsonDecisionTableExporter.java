package io.resys.hdes.decisiontable.spi.exporters;

/*-
 * #%L
 * wrench-assets-bundle
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

import io.resys.hdes.datatype.api.DataTypeService;
import io.resys.hdes.decisiontable.api.DecisionTableAst;
import io.resys.hdes.decisiontable.api.DecisionTableFlatModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlatJsonDecisionTableExporter extends TemplateDecisionTableExporter {

  private final DataTypeService objectMapper;

  public FlatJsonDecisionTableExporter(DataTypeService objectMapper) {
    super();
    this.objectMapper = objectMapper;
  }

  @Override
  public String build() {
    List<DecisionTableAst.RuleType> headers = dt.getTypes();
    DecisionTableFlatModel result = new DecisionTableFlatModel()
        .setName(dt.getId());
    createHeaderModel(headers, result);
    createRow(headers, dt.getNode(), result);
    return objectMapper.write().type(result).build();
  }

  private void createRow(List<DecisionTableAst.RuleType> headers, DecisionTableAst.Node node, DecisionTableFlatModel result) {
    if(node == null) {
      return;
    }

    DecisionTableFlatModel.Entry row = new DecisionTableFlatModel.Entry();
    result.getEntries().add(row);

    Map<String, Object> entries = new HashMap<>();
    node.getInputs().forEach((key, value) -> entries.put(key.getName(), value));
    node.getOutputs().forEach((key, value) -> entries.put(key.getName(), value));

    for(DecisionTableAst.RuleType header : headers) {
      Object value = entries.get(header.getValue().getName());
      row.getValues().add(new DecisionTableFlatModel.Value()
          .setId(header.getOrder())
          .setValue(value == null ? null : String.valueOf(value)));
    }
    createRow(headers, node.getNext(), result);
  }

  private void createHeaderModel(List<DecisionTableAst.RuleType> headers, DecisionTableFlatModel result) {
    for(DecisionTableAst.RuleType dataType : headers) {
      result.getTypes().add(new DecisionTableFlatModel.Type()
          .setDirection(dataType.getValue().getDirection())
          .setType(dataType.getValue().getValueType().name())
          .setName(dataType.getValue().getName()));
    }
  }
}
