package io.resys.wrench.assets.bundle.spi.beans;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;

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

import io.resys.hdes.client.api.model.DataType;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceAssociation;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceDataModel;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceError;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceStatus;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceType;

public class ImmutableServiceDataModel implements ServiceDataModel {

  private final String id;
  private final Class<?> beanType;
  private final String name;
  private final String description;
  private final Timestamp created;
  private final Timestamp modified;
  private final ServiceType type;
  private final ServiceStatus status;
  private final List<ServiceError> errors;
  private final List<DataType> params;
  private final List<ServiceAssociation> associations;

  public ImmutableServiceDataModel(
      String id, String name, String description, ServiceType type, Class<?> beanType,
      ServiceStatus status, List<ServiceError> errors, List<DataType> params, List<ServiceAssociation> associations) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.type = type;
    this.beanType = beanType;
    this.status = status;
    this.errors = errors;
    this.params = params;
    this.associations = associations;
    this.created = new Timestamp(System.currentTimeMillis());
    this.modified = this.created;
  }
  public ImmutableServiceDataModel(
      String id, String name, String description, ServiceType type, Class<?> beanType,
      ServiceStatus status, List<ServiceError> errors, List<DataType> params, List<ServiceAssociation> associations, 
      Timestamp created, Timestamp modified) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.type = type;
    this.beanType = beanType;
    this.status = status;
    this.errors = errors;
    this.params = params;
    this.associations = associations;
    this.created = created;
    this.modified = modified;
  }
  @Override
  public Timestamp getCreated() {
    return created;
  }
  @Override
  public Timestamp getModified() {
    return modified;
  }
  @Override
  public String getId() {
    return id;
  }
  @Override
  public String getName() {
    return name;
  }
  @Override
  public ServiceType getType() {
    return type;
  }
  @Override
  public ServiceStatus getStatus() {
    return status;
  }
  @Override
  public List<ServiceError> getErrors() {
    return errors;
  }
  @Override
  public List<DataType> getParams() {
    return params;
  }
  @Override
  public List<ServiceAssociation> getAssociations() {
    return associations;
  }
  @Override
  public Class<?> getBeanType() {
    return beanType;
  }
  @Override
  public String getDescription() {
    return description;
  }
  @Override
  public ServiceDataModel withTimestamps(Timestamp created, Timestamp modified) {
    return new ImmutableServiceDataModel(id, name, description, type, beanType, status, errors, params, associations, created, modified);
  }
  @Override
  public ServiceDataModel withErrors(List<ServiceError> errors) {
    return new ImmutableServiceDataModel(id, name, description, type, beanType, status, Collections.unmodifiableList(new ArrayList<>(errors)), params, associations, created, modified);
  }
}
