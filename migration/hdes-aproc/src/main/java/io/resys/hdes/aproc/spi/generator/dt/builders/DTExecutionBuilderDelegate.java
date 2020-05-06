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

import java.util.Arrays;
import java.util.List;

import com.squareup.javapoet.TypeSpec;

import io.resys.hdes.decisiontable.api.DecisionTableModel;
import io.resys.hdes.decisiontable.api.DecisionTableModel.Cell;
import io.resys.hdes.decisiontable.api.DecisionTableModel.Header;
import io.resys.hdes.decisiontable.api.DecisionTableModel.Row;
import io.resys.hdes.storage.api.Changes;

public class DTExecutionBuilderDelegate implements DTExecutionBuilder {

  private final List<DTExecutionBuilder> delegates;
  
  private DTExecutionBuilderDelegate(List<DTExecutionBuilder> delegates) {
    super();
    this.delegates = delegates;
  }
  
  public static DTExecutionBuilder from(DTExecutionBuilder ... delegate) {
    return new DTExecutionBuilderDelegate(Arrays.asList(delegate));
  }

  @Override
  public DTExecutionBuilder model(DecisionTableModel model) {
    delegates.forEach(d -> d.model(model));
    return this;
  }

  @Override
  public DTExecutionBuilder tag(String tag) {
    delegates.forEach(d -> d.tag(tag));
    return this;
  }
  
  @Override
  public DTExecutionBuilder addHeader(Header header) {
    delegates.forEach(d -> d.addHeader(header));
    return this;
  }

  @Override
  public DTExecutionBuilder addRow(Row row) {
    delegates.forEach(d -> d.addRow(row));
    return this;
  }

  @Override
  public DTExecutionBuilder addCell(Row row, Cell cell, Header header) {
    delegates.forEach(d -> d.addCell(row, cell, header));
    return this;
  }

  @Override
  public void build(TypeSpec.Builder typeSpec) {
    delegates.forEach(d -> d.build(typeSpec));
  }

  @Override
  public DTExecutionBuilder changes(Changes changes) {
    delegates.forEach(d -> d.changes(changes));
    return this;
  }
}
