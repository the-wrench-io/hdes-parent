package io.resys.hdes.backend.api;

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
import java.util.List;

import io.resys.hdes.backend.api.HdesBackend.Def;
import io.resys.hdes.backend.api.HdesBackend.DefError;
import io.resys.hdes.backend.api.HdesBackend.DefType;
import io.resys.hdes.backend.api.HdesBackend.StorageConfig;

public interface HdesBackendStorage {
  
  StorageConfig config();
  
  StorageReader read();
  
  StorageWriter write();
  
  ErrorReader errors();
  
  interface ErrorReader {
    Collection<DefError> build();
  }
  
  interface StorageReader {
    Collection<Def> build(); 
  }
  
  interface StorageWriter {
    StorageWriter simulation(Boolean simulation);
    StorageWriterEntry add();
    StorageWriterEntry update();
    StorageWriterEntry delete();
    List<Def> build();
  }
  
  interface StorageWriterEntry {
    StorageWriterEntry id(String id);
    StorageWriterEntry name(String name);
    StorageWriterEntry type(DefType type);
    StorageWriterEntry value(String value);
    StorageWriter build();
  }
}
