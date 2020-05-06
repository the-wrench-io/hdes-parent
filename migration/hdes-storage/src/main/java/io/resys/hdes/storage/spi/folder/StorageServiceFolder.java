package io.resys.hdes.storage.spi.folder;

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

import io.resys.hdes.datatype.spi.Assert;
import io.resys.hdes.storage.api.StorageService;

public class StorageServiceFolder implements StorageService {
  private final ChangesOperations changes;
  private final TagOperations tag;
  private final ChangesFolder changesFolder;

  public StorageServiceFolder(ChangesOperations changes, TagOperations tag, ChangesFolder changesFolder) {
    super();
    this.changes = changes;
    this.tag = tag;
    this.changesFolder = changesFolder;
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

    public StorageServiceFolder build() {
      Assert.notNull(source, () -> "source directory can't be null");
      
      
      TenantSupplier tenantSupplier = new DefaultTenantSupplier();
      AuthorSupplier authorSupplier = new DefaultAuthorSupplier();
      TagSupplier tagSupplier = () -> null;
      TagOperations tagOperation = null;
      ChangesFolder changesFolder = ChangesFolder.from(source);
      ChangesOperations changesOperation = new ChangesOperationsFolder(tenantSupplier, authorSupplier, tagSupplier, tagOperation, changesFolder);
      StorageServiceFolder result = new StorageServiceFolder(changesOperation, tagOperation, changesFolder);
      return result;
    }
  }
}
