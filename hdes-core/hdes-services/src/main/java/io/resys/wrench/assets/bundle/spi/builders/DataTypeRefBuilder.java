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

import org.springframework.util.Assert;

import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceType;

public class DataTypeRefBuilder {

  public static class DataTypeToStringRefBuilder {
    private final ServiceType type;
    private String service;
    private String name;

    public DataTypeToStringRefBuilder(ServiceType type) {
      super();
      this.type = type;
    }
    public DataTypeToStringRefBuilder service(String service) {
      this.service = service;
      return this;
    }
    public DataTypeToStringRefBuilder name(String name) {
      this.name = name;
      return this;
    }
    public DataTypeRef build() {
      Assert.notNull(service, "service can't be null!");
      Assert.notNull(name, "name can't be null!");
      String value = type + "/" + service + "/" + name;
      return new DataTypeRef(type, service, name, value);
    }
  }

  public static class DataTypeRef {
    private final ServiceType type;
    private final String service;
    private final String name;
    private final String value;
    public DataTypeRef(ServiceType type, String service, String name, String value) {
      super();
      this.type = type;
      this.service = service;
      this.name = name;
      this.value = value;
    }
    public ServiceType getType() {
      return type;
    }
    public String getService() {
      return service;
    }
    public String getName() {
      return name;
    }
    public String getValue() {
      return value;
    }
  }

  public static DataTypeToStringRefBuilder of(ServiceType type) {
    Assert.notNull(type, "type can't be null!");
    return new DataTypeToStringRefBuilder(type);
  }

  public static DataTypeRef of(String ref) {
    Assert.notNull(ref, "ref can't be null!");
    String[] segments = ref.split("/");
    Assert.isTrue(segments.length == 3, String.format("ref: %s must consist of serviceType/serviceName/paramName", ref));
    return new DataTypeRef(ServiceType.valueOf(segments[0]), segments[1], segments[2], ref);
  }
}
