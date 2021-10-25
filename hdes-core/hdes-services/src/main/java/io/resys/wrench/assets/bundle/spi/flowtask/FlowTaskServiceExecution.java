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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import io.resys.hdes.client.api.HdesClient;
import io.resys.hdes.client.api.HdesClient.ExecutorBuilder;
import io.resys.hdes.client.api.programs.Program;
import io.resys.hdes.client.api.programs.ServiceProgram;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceExecution;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceResponse;

public class FlowTaskServiceExecution implements ServiceExecution {

  private final ServiceProgram script;
  private final ExecutorBuilder builder;
  public FlowTaskServiceExecution(ServiceProgram script, HdesClient client) {
    super();
    this.script = script;
    this.builder = client.executor();
  }

  @Override
  public ServiceExecution insert(Serializable bean) {
    if(bean instanceof FlowTaskInput) {
      builder.inputMap(new HashMap<>(((FlowTaskInput) bean).getValue()));
    } else {
      builder.inputEntity(bean);
    }
    return this;
  }
  @Override
  public ServiceResponse run() {
    try {

      
      Object result = builder.service(script).andGetBody().getValue();
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
      throw new FlowTaskExecutorException(script.getAst().getSource(), e.getMessage(), e);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> void run(Consumer<T> serviceType) {
    serviceType.accept((T) script);
  }

  @Override
  public <T extends Program<?>> T unwrap() {
    return (T) script;
  }
}
