package io.resys.wrench.assets.dt.spi.export;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.wrench.assets.datatype.spi.util.Assert;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.DecisionTableExporter;
import io.resys.wrench.assets.dt.spi.exceptions.DecisionTableException;

public class DelegateDecisionTableExporter extends TemplateDecisionTableExporter implements DecisionTableExporter {

  private final ObjectMapper objectMapper;

  public DelegateDecisionTableExporter(ObjectMapper objectMapper) {
    super();
    this.objectMapper = objectMapper;
  }

  @Override
  public String build() {
    Assert.isTrue(dt != null, () -> "decision table can't be null!");
    Assert.isTrue(format != null, () -> "format can't be null!");

    DecisionTableExporter delegate;
    switch(format) {
    case CSV:
      delegate = new CsvDecisionTableExporter();
      break;
    case JSON:
      delegate = new CommandModelDecisionTableExporter(objectMapper);
      break;
    default: throw new DecisionTableException("Unknown export format: " + format + "!");
    }

    return delegate.format(format).src(dt).build();
  }

}
