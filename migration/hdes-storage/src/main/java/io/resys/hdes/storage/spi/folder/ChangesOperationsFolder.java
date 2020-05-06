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

import io.resys.hdes.storage.api.StorageService.AuthorSupplier;
import io.resys.hdes.storage.api.StorageService.ChangesOperations;
import io.resys.hdes.storage.api.StorageService.QueryBuilder;
import io.resys.hdes.storage.api.StorageService.SaveBuilder;
import io.resys.hdes.storage.api.StorageService.TagOperations;
import io.resys.hdes.storage.api.StorageService.TagSupplier;
import io.resys.hdes.storage.api.StorageService.TenantSupplier;
import io.resys.hdes.storage.spi.builders.GenericChangesQueryBuilder;
import io.resys.hdes.storage.spi.builders.GenericChangesSaveBuilder;

public class ChangesOperationsFolder implements ChangesOperations {
  private final TenantSupplier tenantSupplier;
  private final AuthorSupplier authorSupplier;
  private final TagSupplier tagSupplier;
  private final TagOperations tagOperations;
  private final ChangesFolder changes; 
  
  public ChangesOperationsFolder(
      TenantSupplier tenantSupplier, AuthorSupplier authorSupplier, 
      TagSupplier tagSupplier, TagOperations tagOperations, ChangesFolder changes) {
    super();
    this.tenantSupplier = tenantSupplier;
    this.authorSupplier = authorSupplier;
    this.tagSupplier = tagSupplier;
    this.tagOperations = tagOperations;
    this.changes = changes;
  }

  @Override
  public SaveBuilder save() {
    return new GenericChangesSaveBuilder(
        tenantSupplier, authorSupplier, 
        c -> changes.set(c), 
        () -> query());
  }

  @Override
  public QueryBuilder query() {
    return new GenericChangesQueryBuilder(changes.get(), tenantSupplier, tagSupplier, tagOperations);
  }
}
