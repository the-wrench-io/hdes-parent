package io.resys.hdes.backend.spi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

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

import io.resys.hdes.backend.api.HdesUIBackend;

public class GenericHdesUIBackend implements HdesUIBackend {
  private final ObjectMapper objectMapper;

  public GenericHdesUIBackend(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public DefsQuery defs() {
    return new DefsQuery() {
      @Override
      public List<Def> find() {
        return Collections.emptyList();
      }
    };
  }

  @Override
  public Writer write() {
    return new Writer() {
      private Object value;

      @Override
      public Writer from(List<Def> defs) {
        this.value = defs;
        return this;
      }

      @Override
      public void build(ByteArrayOutputStream out) {
        try {
          objectMapper.writeValue(out, value);
        } catch (IOException e) {
          throw new RuntimeException(e.getMessage(), e);
        }
      }
    };
  }
}
