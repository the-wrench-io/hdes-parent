package io.resys.wrench.assets.flow.spi.builders;

import java.io.Serializable;
import java.time.Clock;
import java.util.ArrayList;

/*-
 * #%L
 * wrench-component-assets-flow
 * %%
 * Copyright (C) 2016 - 2017 Copyright 2016 ReSys OÜ
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.resys.hdes.client.api.execution.Flow;
import io.resys.hdes.client.api.execution.Flow.FlowStatus;
import io.resys.hdes.client.api.model.FlowModel;
import io.resys.wrench.assets.flow.api.FlowExecutorRepository;
import io.resys.wrench.assets.flow.api.FlowRepository.FlowModelExecutor;
import io.resys.wrench.assets.flow.spi.model.FlowContextBean;
import io.resys.wrench.assets.flow.spi.model.GenericFlow;

public class GenericFlowModelExecutor implements FlowModelExecutor {

  private final FlowExecutorRepository executorRepository;
  private final Clock clock;
  private final Map<String, Serializable> genericVariables = new HashMap<>();

  public GenericFlowModelExecutor(FlowExecutorRepository executorRepository, Clock clock) {
    super();
    this.executorRepository = executorRepository;
    this.clock = clock;

  }

  @Override
  public FlowModelExecutor insert(String name, Serializable value) {
    genericVariables.put(name, value);
    return this;
  }
  @Override
  public Flow run(FlowModel model) {
    FlowContextBean context = new FlowContextBean(
        FlowStatus.CREATED, model.getTask().getId(),
        new ArrayList<>(), new ArrayList<>(), new HashMap<>(genericVariables));

    String id = UUID.randomUUID().toString();
    GenericFlow flowBean = new GenericFlow(id, model, context, clock);
    executorRepository.createExecutor().execute(flowBean);
    return flowBean;
  }
}
