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

import io.resys.hdes.client.api.ast.TypeDef;
import io.resys.hdes.client.api.execution.Service;
import io.resys.hdes.client.api.execution.Service.ServiceInit;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceExecution;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceResponse;
import io.resys.wrench.assets.flow.api.FlowTaskExecutorException;

public class FlowTaskServiceExecution implements ServiceExecution {

  private final Service script;
  private final ServiceInit init;
  private final List<Object> facts = new ArrayList<>();
  private final TypeDef taskInputModel;
  private FlowTaskInput taskInput;

  public FlowTaskServiceExecution(Service script, ServiceInit init) {
    super();
    this.script = script;
    this.init = init;
    this.taskInputModel = script.getModel().getHeaders().getAcceptDefs().stream()
        .filter(p -> p.getData())
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
      Object flowTaskInput = taskInputModel.toValue(taskInput.getValue());
      facts.add(flowTaskInput);
    }

    try {
      Object result = script.execute(facts, init);
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
