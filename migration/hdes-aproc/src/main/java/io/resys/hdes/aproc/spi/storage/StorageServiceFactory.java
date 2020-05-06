package io.resys.hdes.aproc.spi.storage;

/*-
 * #%L
 * hdes-aproc
 * %%
 * Copyright (C) 2020 Copyright 2020 ReSys OÜ
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
import java.nio.file.Path;

import javax.lang.model.element.Element;

import org.apache.commons.lang3.StringUtils;

import io.resys.hdes.aproc.spi.exceptions.DataTypeFactorySourceNotSupported;
import io.resys.hdes.datatype.api.DataTypeService;
import io.resys.hdes.datatype.spi.Assert;
import io.resys.hdes.storage.api.StorageService;
import io.resys.hdes.storage.spi.inmemory.StorageServiceInMemory;

public class StorageServiceFactory {
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private static final String LOCAL = "src/main/resources/assets";
    private static final StorageServiceInMemory EMPTY = StorageServiceInMemory.config().build();
    private Element annotatedElement;
    private Path path;

    public Builder annotatedElement(Element annotatedElement) {
      this.annotatedElement = annotatedElement;
      return this;
    }

    public Builder path(Path path) {
      this.path = path;
      return this;
    }

    public StorageService build() {
      Assert.notNull(path, () -> "path must be defined!");
      Assert.notNull(annotatedElement, () -> "annotatedElement must be defined!");
      
      DataTypeService.DataTypeFactory annotation = annotatedElement.getAnnotation(DataTypeService.DataTypeFactory.class);
      if (annotation == null) {
        return StorageServiceInMemory.config().build();
      }
      
      if (annotation.source() == DataTypeService.DataTypeFactorySource.LOCAL) {
        File assets = getAssets(path, annotation.url());
        if (!assets.exists()) {
          File assetsFallback = getAssets(path.getParent(), annotation.url());
          if(assetsFallback.exists()) {
            assets = assetsFallback;
          } else {
            return EMPTY;
          }
        }
        
        return StorageServiceInMemory.config().source(assets).build();
      }
      
      throw DataTypeFactorySourceNotSupported.builder().factory(annotation).build();
    }
    
    private File getAssets(Path path, String annotationUrl) {
      String dir = StringUtils.isEmpty(annotationUrl) ? path.getParent() + "/" + LOCAL : path + "/" + annotationUrl; 
      return new File(dir);
    }
  }
}
