package io.resys.wrench.assets.bundle.spi.beans;

import io.resys.hdes.client.api.ast.TypeDef.Direction;

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

import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceAssociation;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceAssociationType;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceType;

public class ImmutableServiceAssociation implements ServiceAssociation {

  private final String id;
  private final String name;
  private final ServiceType serviceType;
  private final Direction direction;
  private final ServiceAssociationType associationType;

  public ImmutableServiceAssociation(String id, String name, ServiceType serviceType, ServiceAssociationType associationType, Direction direction) {
    super();
    this.id = id;
    this.name = name;
    this.serviceType = serviceType;
    this.associationType = associationType;
    this.direction = direction;
  }

  @Override
  public String getName() {
    return name;
  }
  @Override
  public ServiceType getServiceType() {
    return serviceType;
  }
  @Override
  public ServiceAssociationType getAssociationType() {
    return associationType;
  }
  @Override
  public Direction getDirection() {
    return direction;
  }
  @Override
  public String getId() {
    return id;
  }
}
