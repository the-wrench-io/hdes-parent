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

import io.resys.wrench.assets.datatype.api.DataTypeRepository.DataType;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.DecisionTableExporter;
import io.resys.wrench.assets.dt.api.model.DecisionTable.DecisionTableDataType;
import io.resys.wrench.assets.dt.api.model.DecisionTable.DecisionTableNode;

public class CsvDecisionTableExporter extends TemplateDecisionTableExporter implements DecisionTableExporter {

  @Override
  public String build() {
    StringBuilder stringBuilder = new StringBuilder();
    List<DataType> headers = new ArrayList<>();
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
    node.getInputs().forEach((key, value) -> entries.put(key.getName(), value));
    node.getOutputs().forEach((key, value) -> entries.put(key.getName(), value));

    List<Object> values = new ArrayList<>();
    for(String name : headerNames) {
      Object value = entries.get(name);
      values.add(value);
    }
    csvPrinter.printRecord(values);
    print(csvPrinter, node.getNext(), headerNames);
  }
}
