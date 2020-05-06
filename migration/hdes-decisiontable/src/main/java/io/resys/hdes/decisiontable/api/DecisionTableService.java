package io.resys.hdes.decisiontable.api;

/*-
 * #%L
 * hdes-decisiontable
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

import java.util.Collection;

import io.reactivex.Single;
import io.resys.hdes.datatype.api.DataTypeCommand;
import io.resys.hdes.datatype.api.DataTypeInput;
import io.resys.hdes.execution.HdesService;

public interface DecisionTableService {

  ExecutionBuilder execution();
  ModelBuilder model();
  AstBuilder ast();
  ExportBuilder export();

  interface ExportBuilder {
    ExportBuilder from(DecisionTableAst dt);
    ExportBuilder type(ExportType format);
    String build();
  }

  interface AstBuilder {
    AstBuilder from(DecisionTableModel model);
    AstBuilder from(DecisionTableFlatModel model);
    DecisionTableAst build();
  }
  
  interface ExecutionBuilder {
    ExecutionBuilder ast(DecisionTableAst ast);
    ExecutionBuilder input(DataTypeInput input);
    Single<HdesService.Execution<DecisionTableExecution>> build();
  }

  interface ModelBuilder {
    ModelBuilder src(Collection<DataTypeCommand> src);
    ModelBuilder rev(Integer version);
    DecisionTableModel build();
  }

  enum ExportType {
    JSON_FLAT,
    JSON_COMMAND,
    CSV
  }
}
