package io.resys.hdes.servicetask.spi.execution;

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

import java.util.Map;
import java.util.stream.Collectors;

import io.reactivex.Single;
import io.resys.hdes.datatype.api.DataTypeInput;
import io.resys.hdes.datatype.api.DataTypeService;
import io.resys.hdes.datatype.spi.Assert;
import io.resys.hdes.servicetask.api.ImmutableExecutionEntity;
import io.resys.hdes.servicetask.api.ImmutableServiceTaskExecution;
import io.resys.hdes.servicetask.api.ServiceTask;
import io.resys.hdes.servicetask.api.ServiceTask.Input;
import io.resys.hdes.servicetask.api.ServiceTaskAst;
import io.resys.hdes.servicetask.api.ServiceTaskExecution;
import io.resys.hdes.servicetask.api.ServiceTaskService;
import io.resys.hdes.servicetask.api.ServiceTaskService.ExecutionBuilder;

public class GenericServiceTaskExecutionBuilder implements ServiceTaskService.ExecutionBuilder {

  private final DataTypeService dataTypeService;
  
  private ServiceTaskAst ast;
  private Object context;
  private DataTypeInput input;

  public GenericServiceTaskExecutionBuilder(DataTypeService dataTypeService) {
    super();
    this.dataTypeService = dataTypeService;
  }
  
  @Override
  public ExecutionBuilder ast(ServiceTaskAst ast) {
    this.ast = ast;
    return this;
  }

  @Override
  public ExecutionBuilder input(DataTypeInput input) {
    this.input = input;
    return this;
  }

  @Override
  public ExecutionBuilder context(Object context) {
    this.context = context;
    return this;
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public Single<ServiceTaskExecution> build() {
    Assert.notNull(ast, () -> "ast can't be null");
    Assert.notNull(input, () -> "input can't be null");
    return Single.fromCallable(() -> {
      ServiceTask serviceTask = ast.getType();
      Map<String, Object> inputMap = ast.getInputs().stream().collect(Collectors.toMap(d -> d.getName(), d -> input.apply(d)));
      ServiceTask.Input serviceTaskInput = (Input) dataTypeService.converter().input(inputMap).build(serviceTask.getInputType());
      ServiceTask.Output serviceTaskOutput = serviceTask.execute(serviceTaskInput, context);
      Map<String, Object> outputMap = dataTypeService.converter().input(serviceTaskOutput).build(Map.class);
      
      return ImmutableServiceTaskExecution.builder()
          .ast(ast)
          .input(createExecutionEntity(ServiceTask.Input.class).value(serviceTaskInput).map(inputMap).build())
          .output(createExecutionEntity(ServiceTask.Output.class).value(serviceTaskOutput).map(outputMap).build())
          .build();
    });
  }
  
  private <T> ImmutableExecutionEntity.Builder<T> createExecutionEntity(Class<T> entity) {
    return ImmutableExecutionEntity.builder();
  }
}
