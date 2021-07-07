package io.resys.wrench.assets.bundle.spi.dt;

import java.util.Collections;

/*-
 * #%L
 * wrench-component-assets-dmn
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

import java.util.List;
import java.util.function.Consumer;

import org.springframework.util.Assert;

import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceResponse;
import io.resys.wrench.assets.dt.api.model.DecisionTableResult;

public class DtServiceResponse implements ServiceResponse {

  private final DecisionTableResult result;

  public DtServiceResponse(DecisionTableResult result) {
    super();
    this.result = result;
  }

  @Override
  public void close() throws Exception {
  }

  @Override
  public void forEach(Consumer<Object> consumer) {
    result.getOutputs().forEach(consumer);
  }

  @Override
  public List<?> list() {
    return Collections.unmodifiableList(result.getOutputs());
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T get() {
    Assert.isTrue(result.getOutputs().size() < 2, "Expected 1 or 0 results but got: " + result.getOutputs().size() + "!");
    if(result.getOutputs().isEmpty()) {
      return null;
    }
    return (T) result.getOutputs().iterator().next();
  }

  @Override
  public <T> T getDebug() {
    return (T) result;
  }
}
