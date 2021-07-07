package io.resys.wrench.assets.bundle.spi.builders;

/*-
 * #%L
 * wrench-assets-bundle
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

import java.util.function.Supplier;

import org.springframework.util.Assert;

import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.Service;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceDataModel;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceExecution;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceType;
import io.resys.wrench.assets.bundle.spi.beans.ImmutableService;

public class ImmutableServiceBuilder {

  private final ServiceType type;
  private String id;
  private String name;
  private String rev;
  private String description;
  private String src;
  private String metadata;
  private String pointer;
  private ServiceDataModel model;
  private Supplier<ServiceExecution> execution;

  private ImmutableServiceBuilder(ServiceType type) {
    super();
    this.type = type;
  }

  public Service build() {
    Assert.notNull(type, "type can't be null!");
    Assert.notNull(name, "name can't be null!");
    Assert.notNull(src, "src can't be null!");
    Assert.notNull(pointer, "pointer can't be null!");
    Assert.notNull(model, "model can't be null!");
    Assert.notNull(execution, "execution can't be null!");
    Assert.notNull(rev, "rev can't be null!");

    return new ImmutableService(id, pointer, rev,
        type, name, description, src, metadata, model, execution);
  }
  public ImmutableServiceBuilder setId(String id) {
    this.id = id;
    return this;
  }
  public ImmutableServiceBuilder setRev(String rev) {
    this.rev = rev;
    return this;
  }
  public ImmutableServiceBuilder setName(String name) {
    this.name = name;
    return this;
  }
  public ImmutableServiceBuilder setDescription(String description) {
    this.description = description;
    return this;
  }
  public ImmutableServiceBuilder setSrc(String src) {
    this.src = src;
    return this;
  }
  public ImmutableServiceBuilder setPointer(String pointer) {
    this.pointer = pointer;
    return this;
  }
  public ImmutableServiceBuilder setModel(ServiceDataModel model) {
    this.model = model;
    return this;
  }
  public ImmutableServiceBuilder setExecution(Supplier<ServiceExecution> execution) {
    this.execution = execution;
    return this;
  }
  public ImmutableServiceBuilder setMetadata(String metadata) {
    this.metadata = metadata;
    return this;
  }
  public static ImmutableServiceBuilder from(Service service) {
    return new ImmutableServiceBuilder(service.getType())
        .setId(service.getId())
        .setRev(service.getRev())
        .setName(service.getName())
        .setDescription(service.getDescription())
        .setSrc(service.getSrc())
        .setPointer(service.getPointer())
        .setModel(service.getDataModel())
        .setExecution(service.getExecution())
        .setMetadata(service.getMetadata());
  }
  public static ImmutableServiceBuilder newDataType() {
    return new ImmutableServiceBuilder(ServiceType.DATA_TYPE);
  }
  public static ImmutableServiceBuilder newDt() {
    return new ImmutableServiceBuilder(ServiceType.DT);
  }
  public static ImmutableServiceBuilder newFlow() {
    return new ImmutableServiceBuilder(ServiceType.FLOW);
  }
  public static ImmutableServiceBuilder newFlowTask() {
    return new ImmutableServiceBuilder(ServiceType.FLOW_TASK);
  }
  public static ImmutableServiceBuilder newTag() {
    return new ImmutableServiceBuilder(ServiceType.TAG);
  }
}
