package io.resys.wrench.assets.bundle.spi.flowtask;

/*-
 * #%L
 * wrench-component-assets
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceExecution;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceResponse;
import io.resys.wrench.assets.datatype.api.DataTypeRepository.Direction;
import io.resys.wrench.assets.flow.api.FlowTaskExecutorException;
import io.resys.wrench.assets.script.api.ScriptRepository.Script;
import io.resys.wrench.assets.script.api.ScriptRepository.ScriptParameterContextType;
import io.resys.wrench.assets.script.api.ScriptRepository.ScriptParameterModel;

public class FlowTaskServiceExecution implements ServiceExecution {

  private final Script script;
  private final List<Object> facts = new ArrayList<>();
  private final ScriptParameterModel taskInputModel;
  private FlowTaskInput taskInput;

  public FlowTaskServiceExecution(Script script) {
    super();
    this.script = script;
    this.taskInputModel = script.getModel().getMethod().getParameters().stream()
        .filter(p -> p.getType().getDirection() == Direction.IN)
        .filter(p -> p.getContextType() == ScriptParameterContextType.EXTERNAL)
        .findFirst()
        .orElse(null);
  }

  @Override
  public ServiceExecution insert(Serializable bean) {
    if(bean instanceof FlowTaskInput) {
      taskInput = (FlowTaskInput) bean;
    } else {
      facts.add(bean);
    }
    return this;
  }
  @Override
  public ServiceResponse run() {

    if(taskInputModel != null && taskInput != null) {
      Object flowTaskInput = taskInputModel.getType().toValue(taskInput.getValue());
      facts.add(flowTaskInput);
    }

    try {
      Object result = script.execute(facts);
      facts.clear();
      return new ServiceResponse() {
        @Override
        public void close() throws Exception {
        }
        @Override
        public void forEach(Consumer<Object> consumer) {
        }
        @Override
        public List<?> list() {
          return Collections.emptyList();
        }
        @SuppressWarnings("unchecked")
        @Override
        public <T> T get() {
          return (T) result;
        }
        @SuppressWarnings("unchecked")
        @Override
        public <T> T getDebug() {
          return (T) result;
        }
      };
    } catch(Exception e) {
      throw new FlowTaskExecutorException(script.getModel().getSrc(), e.getMessage(), e);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> void run(Consumer<T> serviceType) {
    serviceType.accept((T) script);
  }
}