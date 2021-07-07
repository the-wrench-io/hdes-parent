package io.resys.wrench.assets.bundle.spi.beans;

import java.sql.Timestamp;
import java.util.List;

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

import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.Service;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceDataModel;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceError;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceExecution;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceType;

public class ImmutableService implements Service {

  private final String id;
  private final String rev;
  private final ServiceType type;
  private final String name;
  private final String description;
  private final String src;
  private final String metadata;
  private final String pointer;
  private final ServiceDataModel dataModel;
  private final Supplier<ServiceExecution> execution;
  
  public ImmutableService(
      String id, String pointer, String rev,
      ServiceType type, String name, String description, String src, String metadata,
      ServiceDataModel dataModel, Supplier<ServiceExecution> execution) {
    super();
    this.id = id;
    this.rev = rev;
    this.type = type;
    this.name = name;
    this.description = description;
    this.src = src;
    this.metadata = metadata;
    this.pointer = pointer;
    this.dataModel = dataModel;
    this.execution = execution;
  }

  public static ImmutableService of(Service service) {
    return new ImmutableService(
        service.getId(),
        service.getPointer(),
        service.getRev(),
        service.getType(),
        service.getName(),
        service.getDescription(),
        service.getSrc(),
        service.getMetadata(),
        service.getDataModel(),
        service.getExecution());
  }

  public static ImmutableService of(Service service, String newName, List<ServiceError> errors) {
    return new ImmutableService(
        service.getId(),
        service.getPointer(),
        service.getRev(),
        service.getType(),
        newName,
        service.getDescription(),
        service.getSrc(),
        service.getMetadata(),
        service.getDataModel().withErrors(errors),
        service.getExecution());
  }
  
  public static ImmutableService of(Service service, Timestamp created, Timestamp modified) {
    return new ImmutableService(
        service.getId(),
        service.getPointer(),
        service.getRev(),
        service.getType(),
        service.getName(),
        service.getDescription(),
        service.getSrc(),
        service.getMetadata(),
        service.getDataModel().withTimestamps(created, modified),
        service.getExecution());
  }
  @Override
  public String getId() {
    return id;
  }
  @Override
  public ServiceType getType() {
    return type;
  }
  @Override
  public String getName() {
    return name;
  }
  @Override
  public String getDescription() {
    return description;
  }
  @Override
  public String getSrc() {
    return src;
  }
  @Override
  public String getPointer() {
    return pointer;
  }
  @Override
  public ServiceExecution newExecution() {
    return this.execution.get();
  }
  @Override
  public ServiceDataModel getDataModel() {
    return dataModel;
  }

  @Override
  public String toString() {
    return "[id=" + id + ", type=" + type + ", name=" + name + "]";
  }

  @Override
  public Supplier<ServiceExecution> getExecution() {
    return execution;
  }

  @Override
  public String getMetadata() {
    return metadata;
  }

  @Override
  public String getRev() {
    return rev;
  }
}
