package io.resys.hdes.decisiontable.spi.exporters;

/*-
 * #%L
 * wrench-assets-dt
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

import io.resys.hdes.datatype.api.DataTypeService;
import io.resys.hdes.datatype.spi.Assert;

public class DelegateDecisionTableExporter extends TemplateDecisionTableExporter {

  private final DataTypeService objectMapper;

  public DelegateDecisionTableExporter(DataTypeService objectMapper) {
    super();
    this.objectMapper = objectMapper;
  }

  @Override
  public String build() {
    Assert.isTrue(dt != null, () -> "model can't be null!");
    Assert.isTrue(format != null, () -> "type can't be null!");

    TemplateDecisionTableExporter delegate;
    switch(format) {
    case CSV:
      delegate = new CsvDecisionTableExporter();
      break;
    case JSON_COMMAND:
      delegate = new CommandModelDecisionTableExporter(objectMapper);
      break;
      case JSON_FLAT:
      delegate = new FlatJsonDecisionTableExporter(objectMapper);
      break;
    default: throw DecisionTableExporterException.builder().dt(dt).type(format).build();
    }

    return delegate.type(format).from(dt).build();
  }

}
