package io.resys.hdes.ui.quarkus.runtime;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.arc.DefaultBean;
import io.resys.hdes.backend.api.HdesBackend;
import io.resys.hdes.backend.api.HdesBackendStorage;
import io.resys.hdes.backend.spi.GenericHdesBackend;
import io.resys.hdes.backend.spi.storage.classpath.ClasspathHdesBackendStorage;
import io.resys.hdes.backend.spi.storage.local.LocalHdesBackendStorage;

/*-
 * #%L
 * hdes-ui-quarkus
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

@ApplicationScoped
public class HdesBackendProducer {
  
  private Optional<String> local;
  
  public HdesBackendProducer setLocal(Optional<String> local) {
    this.local = local;
    return this;
  }

  @Produces
  @Singleton
  @DefaultBean
  public HdesBackend hdesUIBackend() {
    HdesBackendStorage storage = null;
    if(!local.isEmpty()) {
      storage = LocalHdesBackendStorage.create().setLocation(local.get()).build();
    } else {
      storage = ClasspathHdesBackendStorage.create().build();
    }
    
    ObjectMapper objectMapper = new ObjectMapper();
    return new GenericHdesBackend(objectMapper, storage);
  }
  
}