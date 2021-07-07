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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.Service;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceStore;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceType;
import io.resys.wrench.assets.bundle.spi.beans.ImmutableService;
import io.resys.wrench.assets.bundle.spi.builders.ImmutableServiceBuilder;
import io.resys.wrench.assets.bundle.spi.exceptions.AssetErrorCodes;
import io.resys.wrench.assets.bundle.spi.exceptions.DataException;
import io.resys.wrench.assets.bundle.spi.store.git.GitRepository;
import io.resys.wrench.assets.bundle.spi.store.git.GitRepository.ContentTimestamps;


public class GitAssetStore implements ServiceStore {

  private static final Logger LOGGER = LoggerFactory.getLogger(GitAssetStore.class);
  private final Map<String, Service> cachedAssets = new ConcurrentHashMap<>();

  private final GitRepository gitRepository;
  private final AssetLocation location;

  public GitAssetStore(
      GitRepository gitRepository,
      AssetLocation location) {

    super();
    this.gitRepository = gitRepository;
    this.location = location;
  }

  @Override
  public Service save(Service service) {
    try {
      Optional<Service> duplicate = cachedAssets.values().stream()
          .filter(a -> !a.getId().equals(service.getId()))
          .filter(a -> a.getType() == service.getType())
          .filter(a -> a.getName().equalsIgnoreCase(service.getName()))
          .findFirst();
      if(duplicate.isPresent()) {
        throw AssetErrorCodes.SERVICE_NAME_NOT_UNIQUE.newException(duplicate.get().getId());
      }

      if(service.getType() == ServiceType.TAG) {
        LOGGER.debug("Creating tag into git");
        gitRepository.pushTag(service.getName(), "release");
      } else {
        LOGGER.debug("Saving assets into git: " + this.location.getValue() + "");
        String resourceName = location.getResourceFullName(service.getType(), service.getPointer());
        final String assetName;
        if(resourceName.startsWith("file:")) {
          assetName = resourceName.substring(5);
        } else {
          assetName = resourceName;
        }
        File outputFile = new File(assetName);
        if(!outputFile.exists()) {
          outputFile.getParentFile().mkdirs();
          boolean created = outputFile.createNewFile();
          Assert.isTrue(created, () -> "Failed to create new file: " + assetName);
          LOGGER.debug("Created new file: " + outputFile.getCanonicalPath());
        }
  
        // copy data to file
        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
        IOUtils.copy(new ByteArrayInputStream(service.getSrc().getBytes(StandardCharsets.UTF_8)), fileOutputStream);
        fileOutputStream.close();
  
        // push changes
        gitRepository.push();        
      }
      
      final Service oldService = cachedAssets.get(service.getId());
      final Service result;
      if(oldService == null) {
        result = ImmutableService.of(service);
      } else {
        result = ImmutableService.of(service, oldService.getDataModel().getCreated(), new Timestamp(System.currentTimeMillis()));
      }
      
      cachedAssets.put(service.getId(), result);
      return result;
    } catch(DataException e) {
      throw e;
    } catch(Exception e) {
      LOGGER.error("Failed to save assets into git: " + this.location.getValue() + "!" + System.lineSeparator() + e.getMessage(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @Override
  public Service load(Service service) {
    try {
      Optional<Service> duplicate = cachedAssets.values().stream()
          .filter(a -> !a.getId().equals(service.getId()))
          .filter(a -> a.getType() == service.getType())
          .filter(a -> a.getName().equalsIgnoreCase(service.getName()))
          .findFirst();
      if(duplicate.isPresent()) {
        throw AssetErrorCodes.SERVICE_NAME_NOT_UNIQUE.newException(
            service.getId() + " / " + duplicate.get().getId(), 
            duplicate.get().getName());
      }
      
      final boolean isTag = service.getType() == ServiceType.TAG;
      final String resourceName = isTag ? service.getPointer() : location.getResourceFullName(service.getType(), service.getPointer());
      final ContentTimestamps timestamps = this.gitRepository.getTimestamps(isTag ? service.getName() : resourceName, isTag);
      Service result = ImmutableService.of(service, timestamps.getCreated(), timestamps.getModified());
      cachedAssets.put(service.getId(), result);

      return result;
    } catch(DataException e) {
      throw e;
    } catch(Exception e) {
      LOGGER.error("Failed to save assets into git: " + this.location.getValue() + "!" + System.lineSeparator() + e.getMessage(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @Override
  public Service get(Service service, String commit) {
    String resourceName = location.getResourceName(service.getType(), service.getPointer());
    String src = gitRepository.getContent(commit, resourceName);
    return ImmutableServiceBuilder.from(service).setSrc(src).build();
  }

  @Override
  public void remove(String id) {
    Service service = null;
    try {
      
      service = cachedAssets.get(id);
      String resourceName = location.getResourceFullName(service.getType(), service.getPointer());
      
      if(service.getType() == ServiceType.TAG) {
        LOGGER.debug("Removing assets from git: " + resourceName + "");
        gitRepository.deleteTag(service.getName());
        cachedAssets.remove(id);
        return;
      }
      
      LOGGER.debug("Removing assets from git: " + resourceName + "");
      File file = new File(URI.create(resourceName));
      
      boolean deleted = file.delete();
      if(!deleted) {
        LOGGER.debug("Cant delete assets from git: " + resourceName + "");
      }
      
      // push changes
      gitRepository.push();
      cachedAssets.remove(id);

    } catch(DataException e) {
      throw e;
    } catch(Exception e) {
      LOGGER.error("Failed to save assets into git: " + this.location.getValue() + "!" + System.lineSeparator() + e.getMessage(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }
  

  @Override
  public List<String> getTags() {
    return gitRepository.getTags();
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
}
