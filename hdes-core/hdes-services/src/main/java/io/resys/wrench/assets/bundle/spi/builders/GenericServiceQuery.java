package io.resys.wrench.assets.bundle.spi.builders;

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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.Service;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceQuery;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceStore;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceType;

public class GenericServiceQuery implements ServiceQuery {

  private final ServiceStore assetStore;

  private String id;
  private ServiceType type;
  private String name;
  private String rev;

  public GenericServiceQuery(ServiceStore store) {
    super();
    this.assetStore = store;
  }

  @Override
  public Service tag(String name) {
    return name(name).type(ServiceType.TAG).get().get();
  }
  @Override
  public Service flowTask(String name) {
    return name(name).type(ServiceType.FLOW_TASK).get().get();
  }
  @Override
  public Service dt(String name) {
    return name(name).type(ServiceType.DT).get().get();
  }
  @Override
  public Service flow(String name) {
    return name(name).type(ServiceType.FLOW).get().get();
  }
  @Override
  public Service dataType(String name) {
    return name(name).type(ServiceType.DATA_TYPE).get().get();
  }
  @Override
  public ServiceQuery rev(String rev) {
    this.rev = rev;
    return this;
  }
  @Override
  public ServiceQuery name(String name) {
    this.name = name;
    return this;
  }
  @Override
  public ServiceQuery type(ServiceType type) {
    this.type = type;
    return this;
  }
  @Override
  public ServiceQuery id(String id) {
    this.id = id;
    return this;
  }
  @Override
  public List<Service> list() {
    Stream<Service> result = assetStore.list().stream();

    if(id != null) {
      result = result.filter(s -> s.getId().equals(id));
    }
    if(name != null) {
      result = result.filter(s -> s.getName().equals(name));
    }
    if(type != null) {
      result = result.filter(s -> s.getType().equals(type));
    }
    if(!StringUtils.isEmpty(rev)) {
      result = result.map(s -> assetStore.get(s, rev));
    }
    return result.collect(Collectors.toList());
  }
  @Override
  public Optional<Service> get() {
    List<Service> services = list();
    if(services.isEmpty()) {
      return Optional.empty();
    }
    Assert.isTrue(services.size() == 1, "Expecting 1 service with name: " + name + ", type: " + type + " but found: " + services.size() + "!");
    return Optional.of(services.iterator().next());
  }
}
