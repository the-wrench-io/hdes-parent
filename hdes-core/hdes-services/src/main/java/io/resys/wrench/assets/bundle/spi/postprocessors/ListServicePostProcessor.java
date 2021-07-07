package io.resys.wrench.assets.bundle.spi.postprocessors;

/*-
 * #%L
 * wrench-assets-bundle
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

import java.util.Arrays;
import java.util.List;

import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.Service;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServicePostProcessor;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceStore;

public class ListServicePostProcessor implements ServicePostProcessor {

  private final List<ServicePostProcessor> processors;

  public ListServicePostProcessor(List<ServicePostProcessor> processors) {
    this.processors = processors;
  }

  public ListServicePostProcessor(ServicePostProcessor ... processors) {
    this(Arrays.asList(processors));
  }

  @Override
  public void process(ServiceStore store, Service oldState, Service newState) {
    processors.forEach(p -> p.process(store, oldState, newState));
  }

  @Override
  public void delete(ServiceStore store, Service state) {
    processors.forEach(p -> p.delete(store, state));
  }
}
