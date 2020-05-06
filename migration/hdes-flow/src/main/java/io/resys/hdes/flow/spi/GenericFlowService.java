package io.resys.hdes.flow.spi;

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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.resys.hdes.datatype.api.DataType;
import io.resys.hdes.datatype.api.DataTypeService;
import io.resys.hdes.datatype.spi.GenericDataTypeService;
import io.resys.hdes.flow.api.FlowAst.FlowTaskType;
import io.resys.hdes.flow.api.FlowModel;
import io.resys.hdes.flow.api.FlowService;
import io.resys.hdes.flow.api.ImmutableInputType;
import io.resys.hdes.flow.spi.ast.GenericFlowAstBuilder;
import io.resys.hdes.flow.spi.execution.FlowTaskExecutorFactory;
import io.resys.hdes.flow.spi.execution.FlowTaskExecutorFactory.Executor;
import io.resys.hdes.flow.spi.execution.GenericExecutionChangeBuilder;
import io.resys.hdes.flow.spi.execution.GenericFlowExecutionBuilder;
import io.resys.hdes.flow.spi.execution.task.GenericFlowTaskExecutorFactory;
import io.resys.hdes.flow.spi.model.GenericFlowModelBuilder;
import io.resys.hdes.flow.spi.model.beans.FlowModelRootBean;

public class GenericFlowService implements FlowService {

  private final DataTypeService dataTypeService;
  private final ObjectMapper yamlMapper;
  private final Collection<FlowModel.InputType> inputTypes;
  private final FlowTaskExecutorFactory taskExecutorFactory;

  public GenericFlowService(DataTypeService dataTypeService, FlowTaskExecutorFactory taskExecutorFactory, ObjectMapper yamlMapper) {
    this.dataTypeService = dataTypeService;
    this.yamlMapper = yamlMapper;
    this.taskExecutorFactory = taskExecutorFactory;
    this.inputTypes = Collections.unmodifiableList(Arrays.asList(
        DataType.ValueType.STRING,
        DataType.ValueType.ARRAY,
        DataType.ValueType.BOOLEAN,
        DataType.ValueType.INTEGER,
        DataType.ValueType.LONG,
        DataType.ValueType.DECIMAL,
        DataType.ValueType.DATE,
        DataType.ValueType.DATE_TIME).stream()
      .map(v -> ImmutableInputType.builder().name(v.name()).value(v.name()).build())
      .collect(Collectors.toList()));
  }


  @Override
  public ModelBuilder model() {
    return new GenericFlowModelBuilder(yamlMapper, new FlowModelRootBean(inputTypes));
  }

  @Override
  public AstBuilder ast() {
    return new GenericFlowAstBuilder(dataTypeService);
  }

  @Override
  public ExecutionBuilder execution() {
    return new GenericFlowExecutionBuilder(dataTypeService, taskExecutorFactory);
  }

  @Override
  public ExecutionChangeBuilder executionChange() {
    return new GenericExecutionChangeBuilder();
  }

  public static Config config() {
    return new Config();
  }

  public static class Config {

    private Optional<DataTypeService> dataTypeService = Optional.empty();
    private Optional<FlowTaskExecutorFactory> taskExecutorFactory = Optional.empty();
    private Optional<ObjectMapper> yamlMapper = Optional.empty();
    private final Map<FlowTaskType, Executor> executors = new HashMap<>();
    
    public Config dataType(DataTypeService dataTypeService) {
      this.dataTypeService = Optional.of(dataTypeService);
      return this;
    }
    public Config taskExecutor(FlowTaskExecutorFactory taskExecutor) {
      this.taskExecutorFactory = Optional.of(taskExecutor);
      return this;
    }
    public Config taskExecutor(FlowTaskType type, Executor executor) {
      this.executors.put(type, executor);
      return this;
    }
    public Config yamlMapper(ObjectMapper yamlMapper) {
      this.yamlMapper = Optional.of(yamlMapper);
      return this;
    }
    public GenericFlowService build() {
      DataTypeService dataTypeService = this.dataTypeService.orElseGet(() -> GenericDataTypeService.config().build());
      FlowTaskExecutorFactory taskExecutorFactory = this.taskExecutorFactory.orElseGet(() -> GenericFlowTaskExecutorFactory.config().taskExecutor(executors).build());
      ObjectMapper yamlMapper = this.yamlMapper.orElseGet(() -> new ObjectMapper(new YAMLFactory()));
      return new GenericFlowService(dataTypeService, taskExecutorFactory, yamlMapper);
    }
  }
}
