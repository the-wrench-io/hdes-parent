package io.resys.wrench.assets.bundle.spi.flow;

import java.util.Arrays;
import java.util.List;

/*-
 * #%L
 * wrench-component-assets-activiti
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

import java.util.function.Consumer;

import io.resys.hdes.client.api.execution.Flow;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceResponse;

public class FlowServiceResponse implements ServiceResponse {

  private final Flow flow;

  public FlowServiceResponse(Flow flow) {
    super();
    this.flow = flow;
  }

  @Override
  public void forEach(Consumer<Object> consumer) {
    consumer.accept(flow);
  }

  @Override
  public void close() {
  }

  @Override
  public List<?> list() {
    return Arrays.asList(flow);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T get() {
    return (T) flow;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getDebug() {
    return (T) flow;
  }

}
