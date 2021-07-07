package io.resys.wrench.assets.flow.spi;

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


import java.io.Serializable;
import java.util.Map;

import io.resys.wrench.assets.flow.api.FlowExecutorRepository;
import io.resys.wrench.assets.flow.api.FlowRepository;
import io.resys.wrench.assets.flow.api.FlowRepository.FlowTaskBuilder;
import io.resys.wrench.assets.flow.api.model.Flow;
import io.resys.wrench.assets.flow.api.model.Flow.FlowTask;

public class GenericFlowTaskBuilder implements FlowRepository.FlowTaskBuilder {

  private final FlowExecutorRepository flowExecutorFactory;
  private Map<String, Serializable> data;

  public GenericFlowTaskBuilder(FlowExecutorRepository flowExecutorFactory) {
    super();
    this.flowExecutorFactory = flowExecutorFactory;
  }

  @Override
  public FlowTask complete(Flow flow, String id) {
    FlowTask task = flow.getContext().getTask(id);
    if(data != null) {
      task.putVariables(data);
    }
    flow.complete(task);

    flowExecutorFactory.createExecutor().execute(flow);
    return task;
  }

  @Override
  public FlowTaskBuilder data(Map<String, Serializable> data) {
    this.data = data;
    return this;
  }
}
