package io.resys.hdes.storage.spi.inmemory;

/*-
 * #%L
 * hdes-storage
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

import io.resys.hdes.storage.api.Changes;
import io.resys.hdes.storage.api.StorageService;
import io.resys.hdes.storage.spi.folder.ChangesFolder;

public class StorageServiceInMemory implements StorageService {
  private final ChangesOperations changes;
  private final TagOperations tag;

  public StorageServiceInMemory(ChangesOperations changes, TagOperations tag) {
    super();
    this.changes = changes;
    this.tag = tag;
  }

  @Override
  public ChangesOperations changes() {
    return changes;
  }

  @Override
  public TagOperations tag() {
    return tag;
  }

  public static Config config() {
    return new Config();
  }

  public static class Config {
    private File source;

    public Config source(File sourceDirectory) {
      this.source = sourceDirectory;
      return this;
    }

    public StorageServiceInMemory build() {
      TenantSupplier tenantSupplier = new DefaultTenantSupplier();
      AuthorSupplier authorSupplier = new DefaultAuthorSupplier();
      TagSupplier tagSupplier = () -> null;
      TagOperations tagOperation = null;
      ChangesOperations changesOperation = new ChangesOperationsInMemory(tenantSupplier, authorSupplier, tagSupplier, tagOperation);
      StorageServiceInMemory result = new StorageServiceInMemory(changesOperation, tagOperation);
      if (source != null) {
        
        for (Changes changes : ChangesFolder.from(source).get()) {
          result.changes().save()
              .id(changes.getId())
              .label(changes.getLabel())
              .tenant(changes.getTenant())
              .changes(changes.getValues())
              .revision(changes.getValues().size())
              .build();
        }
      }
      return result;
    }
  }
}
