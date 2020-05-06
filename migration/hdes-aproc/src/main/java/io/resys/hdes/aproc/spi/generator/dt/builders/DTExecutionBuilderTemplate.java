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
import io.resys.hdes.decisiontable.api.DecisionTableModel.Cell;
import io.resys.hdes.decisiontable.api.DecisionTableModel.Header;
import io.resys.hdes.decisiontable.api.DecisionTableModel.Row;
import io.resys.hdes.storage.api.Changes;

public class DTExecutionBuilderTemplate implements DTExecutionBuilder {

  
  
  @Override
  public DTExecutionBuilder model(DecisionTableModel model) {
    return this;
  }

  @Override
  public DTExecutionBuilder addHeader(Header header) {
    return this;
  }

  @Override
  public DTExecutionBuilder addRow(Row row) {
    return this;
  }

  @Override
  public DTExecutionBuilder addCell(Row row, Cell cell, Header header) {
    return this;
  }

  @Override
  public void build(TypeSpec.Builder typeSpec) {
  }

  @Override
  public DTExecutionBuilder changes(Changes changes) {
    return this;
  }

  @Override
  public DTExecutionBuilder tag(String tag) {
    return this;
  }
}
