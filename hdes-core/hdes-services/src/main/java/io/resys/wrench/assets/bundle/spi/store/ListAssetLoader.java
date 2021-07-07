package io.resys.wrench.assets.bundle.spi.store;

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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.Service;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceType;
import io.resys.wrench.assets.bundle.spi.beans.ImmutableService;
import io.resys.wrench.assets.bundle.spi.beans.ImmutableServiceError;
import io.resys.wrench.assets.bundle.spi.exceptions.AssetErrorCodes;
import io.resys.wrench.assets.bundle.spi.exceptions.DataException;
import io.resys.wrench.assets.bundle.spi.exceptions.Message;

public class ListAssetLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(ListAssetLoader.class);

  private final AssetServiceRepository assetRepository;
  private final AssetLocation location;
  private final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

  public ListAssetLoader(AssetServiceRepository assetRepository, AssetLocation location) {
    super();
    this.assetRepository = assetRepository;
    this.location = location;
  }

  public void createDuplicateErrorService(Resource resource, Service s1, DataException e) {
    try {
      Optional<Message> duplicate = e.getError(AssetErrorCodes.SERVICE_NAME_NOT_UNIQUE.getCode());
      if (location.isGit() && duplicate.isPresent() && s1 != null) {
        String tempName = s1.getName() + "-" + System.currentTimeMillis();
        ImmutableServiceError error = new ImmutableServiceError(duplicate.get().getCode(), duplicate.get().getValue());
        Service s2 = ImmutableService.of(s1, tempName, Arrays.asList(error));
        assetRepository.createStore().load(s2);
        LOGGER.error("Failed to load asset content from: " + resource.getFilename() + "!" + e.getMessage(), e);
        return;
      }
    } catch (Exception e1) {
      throw new RuntimeException("Failed to load asset content from: " + resource.getFilename() + "!" + e.getMessage(), e1);
    }
    
    throw new RuntimeException("Failed to load asset content from: " + resource.getFilename() + "!" + e.getMessage(), e);
  }

  public void load() {
    // Decision tables
    list(location.getDtRegex()).stream().forEach(r -> {
      Service s = null;
      try {
        s = assetRepository.createBuilder(ServiceType.DT)
            .id(location.getResourceId(ServiceType.DT, r.getFilename())).src(getContent(r)).pointer(r.getFilename())
            .build();
        assetRepository.createStore().load(s);
      } catch (DataException e) {
        createDuplicateErrorService(r, s, e);
      } catch (Exception e) {
        throw new RuntimeException("Failed to load asset content from: " + r.getFilename() + "!" + e.getMessage(), e);
      }
      
    });

    // Flow tasks
    list(location.getFlowTaskRegex()).stream().forEach(r -> {
      Service s = null;
      try {
        s = assetRepository.createBuilder(ServiceType.FLOW_TASK)
            .id(location.getResourceId(ServiceType.FLOW_TASK, r.getFilename())).src(getContent(r))
            .pointer(r.getFilename()).build();
        assetRepository.createStore().load(s);
      } catch (DataException e) {
        createDuplicateErrorService(r, s, e);
      } catch (Exception e) {
        throw new RuntimeException("Failed to load asset content from: " + r.getFilename() + "!" + e.getMessage(), e);
      }
    });

    // Flow
    list(location.getFlowRegex()).stream().forEach(r -> {
      Service s = null;
      try {
        s = assetRepository.createBuilder(ServiceType.FLOW)
            .id(location.getResourceId(ServiceType.FLOW, r.getFilename())).src(getContent(r)).pointer(r.getFilename())
            .build();
        assetRepository.createStore().load(s);
      } catch (DataException e) {
        createDuplicateErrorService(r, s, e);
      } catch (Exception e) {
        throw new RuntimeException("Failed to load asset content from: " + r.getFilename() + "!" + e.getMessage(), e);
      }
    });
    
    
    LOGGER.debug("Assets hash: " + assetRepository.getHash() + "!");

    // Tags
    assetRepository.createStore().getTags().forEach(tagName -> {
      try {
        Service service = assetRepository.createBuilder(ServiceType.TAG).name(tagName).src("").ignoreErrors().build();
        assetRepository.createStore().load(service);

      } catch (DataException e) {
        LOGGER.error("Assets skipping tag: " + tagName + "!", e.getMessage());
      }
    });
  }

  protected String getContent(Resource entry) {
    try {
      return IOUtils.toString(entry.getInputStream(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load asset content from: " + entry.getFilename() + "!" + e.getMessage(), e);
    }
  }

  protected List<Resource> list(String location) {
    try {
      LOGGER.debug("Loading assets from: " + location + "!");

      List<Resource> files = new ArrayList<>();
      for (Resource resource : resolver.getResources(location)) {
        files.add(resource);
      }
      return files;
    } catch (Exception e) {
      throw new RuntimeException("Failed to load asset from: " + location + "!" + e.getMessage(), e);
    }
  }
}
