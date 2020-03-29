package io.resys.hdes.aproc.spi.generator.dt.builders;

/*-
 * #%L
 * hdes-aproc
 * %%
 * Copyright (C) 2020 Copyright 2020 ReSys OÜ
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

import com.squareup.javapoet.TypeSpec;

import io.resys.hdes.decisiontable.api.DecisionTableModel;
import io.resys.hdes.storage.api.Changes;

public interface DTExecutionBuilder {
  DTExecutionBuilder changes(Changes changes);
  DTExecutionBuilder model(DecisionTableModel model);
  DTExecutionBuilder tag(String tag);
  DTExecutionBuilder addHeader(DecisionTableModel.Header header);
  DTExecutionBuilder addRow(DecisionTableModel.Row row);
  DTExecutionBuilder addCell(DecisionTableModel.Row row, DecisionTableModel.Cell cell, DecisionTableModel.Header header);
  void build(TypeSpec.Builder typeSpec);
}
