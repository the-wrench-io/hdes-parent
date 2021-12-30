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

import java.sql.Timestamp;

import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceBuilder;

public abstract class TemplateServiceBuilder implements ServiceBuilder {

  protected String id;
  protected String name;
  protected String src;
  protected String pointer;
  protected Timestamp lastModified;
  protected Timestamp created;
  protected boolean ignoreErrors;

  @Override
  public ServiceBuilder id(String id) {
    this.id = id;
    return this;
  }
  @Override
  public ServiceBuilder name(String name) {
    this.name = name;
    return this;
  }
  @Override
  public ServiceBuilder src(String src) {
    this.src = src;
    return this;
  }
  @Override
  public ServiceBuilder pointer(String pointer) {
    this.pointer = pointer;
    return this;
  }
  @Override
  public ServiceBuilder lastModified(Timestamp lastModified) {
    this.lastModified = lastModified;
    return this;
  }
  @Override
  public ServiceBuilder created(Timestamp created) {
    this.created = created;
    return this;
  }
  @Override
  public ServiceBuilder ignoreErrors() {
    this.ignoreErrors = true;
    return this;
  }
}
