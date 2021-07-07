package io.resys.wrench.assets.bundle.spi.store;

import java.util.Collection;

/*-
 * #%L
 * wrench-component-assets-persistence
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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.util.Assert;

import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.Service;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceStore;
import io.resys.wrench.assets.bundle.spi.exceptions.AssetErrorCodes;


public class InMemoryAssetStore implements ServiceStore {
  private final Map<String, Service> cachedAssets = new ConcurrentHashMap<>();

  @Override
  public Service save(Service newState) {

    List<String> duplicate = cachedAssets.values().stream()
        .filter(a -> !a.getId().equals(newState.getId()))
        .filter(a -> a.getType() == newState.getType())
        .filter(a -> a.getName().equalsIgnoreCase(newState.getName()))
        .map(a -> a.getPointer())
        .collect(Collectors.toList());
    if(!duplicate.isEmpty()) {
      throw AssetErrorCodes.SERVICE_NAME_NOT_UNIQUE.newException(newState.getPointer(), duplicate);
    }
    cachedAssets.put(newState.getId(), newState);
    return newState;
  }
  @Override
  public Service get(Service service, String commit) {
    return service;
  }
  @Override
  public Service load(Service service) {
    return save(service);
  }
  @Override
  public void remove(String id) {
    cachedAssets.remove(id);
  }
  @Override
  public Service get(String id) {
    Assert.isTrue(cachedAssets.containsKey(id), "No asset with id: " + id + "!");
    return cachedAssets.get(id);
  }
  @Override
  public Collection<Service> list() {
    return Collections.unmodifiableCollection(cachedAssets.values());
  }
  @Override
  public boolean contains(String id) {
    return cachedAssets.containsKey(id);
  }
  @Override
  public List<String> getTags() {
    return Collections.emptyList();
  }
}
