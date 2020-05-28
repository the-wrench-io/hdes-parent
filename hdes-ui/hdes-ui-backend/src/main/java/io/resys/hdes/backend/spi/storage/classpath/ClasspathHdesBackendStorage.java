package io.resys.hdes.backend.spi.storage.classpath;

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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import io.resys.hdes.backend.api.HdesBackend.Def;
import io.resys.hdes.backend.api.HdesBackendStorage;

public class ClasspathHdesBackendStorage implements HdesBackendStorage {
  
  private Map<String, Def> cache = new HashMap<>();

  @Override
  public StorageReader read() {
    return new StorageReader() {
      @Override
      public Collection<Def> build() {
        return cache.values();
      }
    };
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

    public ClasspathHdesBackendStorage build() {
      return new ClasspathHdesBackendStorage();
    }
  }
}
