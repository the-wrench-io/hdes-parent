package io.resys.hdes.backend.spi.storage.local;

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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.resys.hdes.backend.api.HdesBackend.Def;
import io.resys.hdes.backend.api.HdesBackendStorage;

public class LocalHdesBackendStorage implements HdesBackendStorage {

  private Map<String, Def> cache = new HashMap<>();
  
  private final File location;
  
  public LocalHdesBackendStorage(File location) {
    super();
    this.location = location;
  }

  @Override
  public StorageReader read() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public StorageWriter write() {
    // TODO Auto-generated method stub
    return null;
  }
  
  
  public static Config config() {
    return new Config();
  }
  
  public static class Config {
    private String location;
    
    public Config setLocation(String location) {
      this.location = location;
      return this;
    }

    public LocalHdesBackendStorage build() {
      File file = new File(location);
      if(!file.exists()) {
        throw new LocalStorageException(LocalStorageException.builder().nonExistingLocation(file));
      }
      if(!file.isDirectory()) {
        throw new LocalStorageException(LocalStorageException.builder().locationIsNotDirectory(file));
      }
      if(!file.canWrite()) {
        throw new LocalStorageException(LocalStorageException.builder().locationCantBeWritten(file));
      }
      
      return new LocalHdesBackendStorage(file);
    }
  }
}
