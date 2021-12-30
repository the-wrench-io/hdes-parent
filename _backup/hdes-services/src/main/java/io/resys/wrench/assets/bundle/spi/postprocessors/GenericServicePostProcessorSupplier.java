package io.resys.wrench.assets.bundle.spi.postprocessors;

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

import java.util.Map;

import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServicePostProcessor;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServicePostProcessorSupplier;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceType;

public class GenericServicePostProcessorSupplier implements ServicePostProcessorSupplier {

  private final Map<ServiceType, ServicePostProcessor> postProcessors;
  private final ServicePostProcessor doNothingProcessor = new DoNothingPostProcessor();

  public GenericServicePostProcessorSupplier(Map<ServiceType, ServicePostProcessor> postProcessors) {
    this.postProcessors = postProcessors;
  }

  @Override
  public ServicePostProcessor get(ServiceType type) {
    if(!postProcessors.containsKey(type)) {
      return doNothingProcessor;
    }
    return postProcessors.get(type);
  }
}
