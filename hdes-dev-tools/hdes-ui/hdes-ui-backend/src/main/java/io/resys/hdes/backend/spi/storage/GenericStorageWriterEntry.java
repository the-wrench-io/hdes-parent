package io.resys.hdes.backend.spi.storage;

/*-
 * #%L
 * hdes-ui-backend
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

import io.resys.hdes.backend.api.HdesBackend.DefType;
import io.resys.hdes.backend.api.HdesBackendStorage.StorageWriterEntry;

public abstract class GenericStorageWriterEntry implements StorageWriterEntry {

  protected String id;
  protected String name;
  protected DefType type;
  protected String value;
  
  @Override
  public StorageWriterEntry id(String id) {
    this.id = id;
    return this;
  }

  @Override
  public StorageWriterEntry name(String name) {
    this.name = name;
    return this;
  }

  @Override
  public StorageWriterEntry type(DefType type) {
    this.type = type;
    return this;
  }

  @Override
  public StorageWriterEntry value(String value) {
    this.value = value;
    return this;
  }

}
