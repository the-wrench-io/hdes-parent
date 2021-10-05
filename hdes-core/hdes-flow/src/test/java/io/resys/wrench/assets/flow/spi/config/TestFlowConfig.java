package io.resys.wrench.assets.flow.spi.config;

/*-
 * #%L
 * wrench-assets-flow
 * %%
 * Copyright (C) 2016 - 2021 Copyright 2020 ReSys OÜ
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

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.client.api.HdesAstTypes;
import io.resys.hdes.client.api.model.FlowModel.FlowTaskType;
import io.resys.hdes.client.spi.HdesAstTypesImpl;
import io.resys.wrench.assets.flow.api.FlowExecutorRepository;
import io.resys.wrench.assets.flow.api.FlowExecutorRepository.FlowTaskExecutor;
import io.resys.wrench.assets.flow.api.FlowRepository;
import io.resys.wrench.assets.flow.spi.GenericFlowExecutorFactory;
import io.resys.wrench.assets.flow.spi.GenericFlowRepository;
import io.resys.wrench.assets.flow.spi.executors.EmptyFlowTaskExecutor;
import io.resys.wrench.assets.flow.spi.executors.EndFlowTaskExecutor;
import io.resys.wrench.assets.flow.spi.executors.ExclusiveFlowTaskExecutor;
import io.resys.wrench.assets.flow.spi.executors.MergeFlowTaskExecutor;
import io.resys.wrench.assets.flow.spi.executors.ServiceFlowTaskExecutor;
import io.resys.wrench.assets.flow.spi.executors.UserFlowTaskExecutor;
import io.resys.wrench.assets.flow.spi.expressions.SpelExpressionFactory;

public class TestFlowConfig {
  private static ObjectMapper objectMapper = new ObjectMapper();
  private static HdesAstTypes nodeRepository = new HdesAstTypesImpl(objectMapper);
  private static FlowExecutorRepository flowExecutorFactory;
  private static FlowRepository flowRepository;

  public static ObjectMapper objectMapper() {
    return objectMapper;
  }
  
  public static HdesAstTypes nodeRepository() {
    return nodeRepository;
  }

  public static FlowRepository flowRepository() {
    if (flowRepository == null) {
      SpelExpressionFactory parser = new SpelExpressionFactory();

      flowRepository = new GenericFlowRepository(nodeRepository, 
          flowExecutorFactory(), 
          parser, objectMapper, 
          Clock.systemUTC());
    }
    return flowRepository;
  }

  public static FlowExecutorRepository flowExecutorFactory() {
    if (flowExecutorFactory == null) {
      Map<FlowTaskType, FlowTaskExecutor> executors = new HashMap<>();
      executors.put(FlowTaskType.USER_TASK, new UserFlowTaskExecutor());
      executors.put(FlowTaskType.END, new EndFlowTaskExecutor());
      executors.put(FlowTaskType.EXCLUSIVE, new ExclusiveFlowTaskExecutor());
      executors.put(FlowTaskType.MERGE, new MergeFlowTaskExecutor());
      executors.put(FlowTaskType.SERVICE, new ServiceFlowTaskExecutor());
      executors.put(FlowTaskType.EMPTY, new EmptyFlowTaskExecutor());
      flowExecutorFactory = new GenericFlowExecutorFactory(executors);
    }
    return flowExecutorFactory;
  }
}
