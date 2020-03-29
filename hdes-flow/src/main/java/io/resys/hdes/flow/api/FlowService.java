package io.resys.hdes.flow.api;

/*-
 * #%L
 * hdes-flow
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

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import io.reactivex.Single;
import io.resys.hdes.datatype.api.DataTypeCommand;
import io.resys.hdes.datatype.api.DataTypeInput;

public interface FlowService {
  ModelBuilder model();

  AstBuilder ast();

  ExecutionBuilder execution();

  ExecutionChangeBuilder executionChange();

  interface AstBuilder {
    AstBuilder from(FlowModel.Root model);

    FlowAst build();
  }

  interface ExecutionBuilder {
    ExecutionBuilder id(String givenId);

    ExecutionBuilder from(FlowAst ast);

    ExecutionBuilder from(FlowExecution execution);

    ExecutionBuilder input(DataTypeInput input);

    Single<FlowExecution> build();
  }

  interface ExecutionChangeBuilder {
    ExecutionChangeBuilder from(FlowExecution execution);

    ExecutionChangeBuilder addInput(String name, Serializable value);

    ExecutionChangeBuilder addInputs(Map<String, Serializable> inputs);

    ExecutionChangeBuilder endTask(String id);

    FlowExecution build();
  }

  interface ModelBuilder {
    ModelBuilder src(Collection<DataTypeCommand> src);

    ModelBuilder rev(Integer version);

    FlowModel.Root build();
  }
}
