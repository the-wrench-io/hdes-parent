package io.resys.wrench.assets.bundle.spi.exporters;

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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceType;

public class NameToGIDExporter {
  private final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
  
  public void build() {
    build("src/main/resources");
  }

  public void build(String location) {
    try {
      Arrays.asList(ServiceType.DT, ServiceType.FLOW, ServiceType.FLOW_TASK).stream()
      .forEach(type ->
        list(getTypePattern("classpath*:assets/", type))
        .forEach(asset -> convertAsset(location, type, asset))
      );

    } catch(Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  private void convertAsset(String location, ServiceType type, org.springframework.core.io.Resource resource) {

    try {

      String path = location + "/assets/" + getAssetSubFolder(type) + "/";
      String newFileName = path + UUID.randomUUID() + ".json";

      System.out.println(newFileName);
      resource.getFile().renameTo(new File(newFileName));
    } catch(IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  private String getAssetSubFolder(ServiceType type) {
    if(type == ServiceType.FLOW) {
      return "flow";
    } else if (type == ServiceType.DT) {
      return "dt";
    } else if (type == ServiceType.FLOW_TASK) {
      return "flowtask";
    }
    throw new IllegalArgumentException("Unknown service type: " + type + "!");
  }

  private String getTypePattern(String location, ServiceType type) {
    String assetSubFolder = getAssetSubFolder(type);
    String extension = null;
    if(type == ServiceType.FLOW) {
      extension = ".yaml";
    } else if (type == ServiceType.DT) {
      extension = ".json";
    } else if (type == ServiceType.FLOW_TASK) {
      extension = ".groovy";
    }
    return location + assetSubFolder + "/*" + extension;
  }

  private List<org.springframework.core.io.Resource> list(String location) {
    try {
      System.out.println(location);
      return Arrays.asList(resolver.getResources(location));
    } catch(IOException e) {
      throw new RuntimeException("Failed to load asset from: " + location + "!" + e.getMessage(), e);
    }
  }
}
