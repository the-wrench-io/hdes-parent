package io.resys.wrench.assets.dt.spi.export;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import io.resys.hdes.client.api.ast.AstDataType;
import io.resys.hdes.client.api.model.DecisionTableModel.DecisionTableDataType;
import io.resys.hdes.client.api.model.DecisionTableModel.DecisionTableNode;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.DecisionTableExporter;

public class CsvDecisionTableExporter extends TemplateDecisionTableExporter implements DecisionTableExporter {

  @Override
  public String build() {
    StringBuilder stringBuilder = new StringBuilder();
    List<AstDataType> headers = new ArrayList<>();
    List<String> headerNames = new ArrayList<>();
    for(DecisionTableDataType dataType : dt.getTypes()) {
      headers.add(dataType.getValue());
      headerNames.add(dataType.getValue().getName());
    }

    try {
      CSVPrinter csvPrinter = new CSVPrinter(stringBuilder, CSVFormat.DEFAULT.withDelimiter(';').withHeader(headerNames.toArray(new String[] {})));
      print(csvPrinter, dt.getNode(), headerNames);
      return stringBuilder.toString();
    } catch(IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  protected void print(CSVPrinter csvPrinter, DecisionTableNode node, List<String> headerNames) throws IOException {
    if(node == null) {
      return;
    }

    Map<String, Object> entries = new HashMap<>();
    node.getInputs().forEach(e -> entries.put(e.getKey().getName(), e.getValue()));
    node.getOutputs().forEach(e -> entries.put(e.getKey().getName(), e.getValue()));

    List<Object> values = new ArrayList<>();
    for(String name : headerNames) {
      Object value = entries.get(name);
      values.add(value);
    }
    csvPrinter.printRecord(values);
    print(csvPrinter, node.getNext(), headerNames);
  }
}
