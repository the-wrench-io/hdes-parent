package io.resys.wrench.assets.bundle.spi.store;

/*-
 * #%L
 * wrench-assets-services
 * %%
 * Copyright (C) 2016 - 2019 Copyright 2016 ReSys OÜ
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

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.AssetService;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServicePostProcessorSupplier;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceStore;

public class PostProcessingServiceStore implements ServiceStore {
  private static final Logger LOGGER = LoggerFactory.getLogger(PostProcessingServiceStore.class);
  private final ServicePostProcessorSupplier postProcessors;
  private final ServiceStore delegate;
  
  public PostProcessingServiceStore(ServiceStore delegate, ServicePostProcessorSupplier postProcessors) {
    super();
    this.delegate = delegate;
    this.postProcessors = postProcessors;
  }

  @Override
  public AssetService get(AssetService service, String rev) {
    return delegate.get(service, rev);
  }

  @Override
  public Collection<AssetService> list() {
    return delegate.list();
  }

  @Override
  public AssetService get(String id) {
    return delegate.get(id);
  }
  
  @Override
  public AssetService save(AssetService newState) {
    final AssetService oldState = delegate.contains(newState.getId()) ? delegate.get(newState.getId()) : null;
    final AssetService result = delegate.save(newState);
    
    try {
      postProcessors.get(newState.getType()).process(delegate, oldState, result);
    } catch(Exception e) {
      LOGGER.error("Failed to post process, after resource " + newState.getPointer() + "!" + System.lineSeparator() + e.getMessage(), e);
    }
    return result;
  }

  @Override
  public AssetService load(AssetService service) {
    final AssetService result = delegate.load(service);
    
    try {
      postProcessors.get(service.getType()).process(delegate, null, result);
    } catch(Exception e) {
      LOGGER.error("Failed to post process, after resource " + service.getId() + "!" + System.lineSeparator() + e.getMessage(), e);
    }
    
    return result;
  }

  @Override
  public void remove(String id) {
    final AssetService service = delegate.get(id);
    delegate.remove(id);
    
    try {
      postProcessors.get(service.getType()).delete(delegate, service);
    } catch(Exception e) {
      LOGGER.error("Failed to post process, after resource " + id + "!" + System.lineSeparator() + e.getMessage(), e);
    }
  }

  @Override
  public boolean contains(String id) {
    return delegate.contains(id);
  }

  @Override
  public List<String> getTags() {
    return delegate.getTags();
  }
}
