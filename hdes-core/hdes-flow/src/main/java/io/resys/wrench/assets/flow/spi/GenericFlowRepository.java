package io.resys.wrench.assets.flow.spi;

import java.time.Clock;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.client.api.HdesAstTypes;
import io.resys.wrench.assets.flow.api.FlowExecutorRepository;

/*-
 * #%L
 * wrench-assets-flow
 * %%
 * Copyright (C) 2016 - 2018 Copyright 2016 ReSys OÜ
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

import io.resys.wrench.assets.flow.api.FlowRepository;
import io.resys.wrench.assets.flow.spi.builders.GenericFlowModelBuilder;
import io.resys.wrench.assets.flow.spi.builders.GenericFlowModelExecutor;
import io.resys.wrench.assets.flow.spi.expressions.ExpressionFactory;

public class GenericFlowRepository implements FlowRepository {

  private final ExpressionFactory parser;
  private final Clock clock;
  private final FlowExecutorRepository executorRepository;
  private final HdesAstTypes dataTypeRepository;
  private final ObjectMapper objectMapper;

  public GenericFlowRepository(
      HdesAstTypes dataTypeRepository,
      FlowExecutorRepository executorRepository,
      ExpressionFactory parser,
      ObjectMapper objectMapper,
      Clock clock) {
    super();
    this.executorRepository = executorRepository;
    this.parser = parser;
    this.dataTypeRepository = dataTypeRepository;
    this.objectMapper = objectMapper;
    this.clock = clock;
  }

  @Override
  public FlowModelBuilder createModel() {
    return new GenericFlowModelBuilder(parser, dataTypeRepository, objectMapper);
  }
  @Override
  public FlowModelExporter createExporter() {
    return null;
  }

  @Override
  public FlowModelExecutor createExecutor() {
    return new GenericFlowModelExecutor(executorRepository, clock);
  }

  @Override
  public FlowTaskBuilder createTaskBuilder() {
    return new GenericFlowTaskBuilder(executorRepository);
  }
}
