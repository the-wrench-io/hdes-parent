package io.resys.hdes.servicetask.api;

/*-
 * #%L
 * hdes-servicetask
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

import java.util.List;

import io.reactivex.Single;
import io.resys.hdes.datatype.api.DataTypeCommand;
import io.resys.hdes.datatype.api.DataTypeInput;

public interface ServiceTaskService {

  ModelBuilder model();
  AstBuilder ast();
  ExecutionBuilder execution();
    
  interface ExecutionBuilder {
    ExecutionBuilder ast(ServiceTaskAst ast);
    ExecutionBuilder context(Object context);
    ExecutionBuilder input(DataTypeInput input);
    Single<ServiceTaskExecution> build();
  }

  interface AstBuilder {
    AstBuilder from(ServiceTaskModel model);
    ServiceTaskAst build();
  }
  
  interface ModelBuilder {
    ModelBuilder context(Class<?> context);
    ModelBuilder src(List<DataTypeCommand> commands);
    ModelBuilder rev(Integer rev);
    ServiceTaskModel build();
  }
}
