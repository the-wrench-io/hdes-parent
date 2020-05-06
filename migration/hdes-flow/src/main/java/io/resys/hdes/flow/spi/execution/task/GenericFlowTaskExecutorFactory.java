package io.resys.hdes.flow.spi.execution.task;

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

import java.util.HashMap;
import java.util.Map;

import io.resys.hdes.flow.api.FlowAst;
import io.resys.hdes.flow.api.FlowAst.FlowTaskType;
import io.resys.hdes.flow.api.FlowAst.Task;
import io.resys.hdes.flow.spi.execution.FlowTaskExecutorFactory;

public class GenericFlowTaskExecutorFactory implements FlowTaskExecutorFactory {

  private Map<FlowAst.FlowTaskType, Executor> executors;
  
  public GenericFlowTaskExecutorFactory(Map<FlowTaskType, Executor> executors) {
    super();
    this.executors = executors;
  }

  @Override
  public Executor create(Task task) {
    return executors.get(task.getType());
  }
  

  public static Config config() {
    return new Config();
  }
  
  public static class Config {
    private final Map<FlowTaskType, Executor> executors = new HashMap<>();
    
    private Config() {
      executors.put(FlowTaskType.USER_TASK, new UserFlowTaskExecutor());
      executors.put(FlowTaskType.END, new EndFlowTaskExecutor());
      executors.put(FlowTaskType.EXCLUSIVE, new ExclusiveFlowTaskExecutor());
      executors.put(FlowTaskType.MERGE, new MergeFlowTaskExecutor());
      executors.put(FlowTaskType.SERVICE, new ServiceFlowTaskExecutor());
      executors.put(FlowTaskType.EMPTY, new EmptyFlowTaskExecutor());
    }
    
    public Config taskExecutor(FlowTaskType type, Executor executor) {
      this.executors.put(type, executor);
      return this;
    }
    public Config taskExecutor( Map<FlowTaskType, Executor> executors) {
      this.executors.putAll(executors);
      return this;
    }
    public GenericFlowTaskExecutorFactory build() {
      return new GenericFlowTaskExecutorFactory(executors);
    }
  }
}
