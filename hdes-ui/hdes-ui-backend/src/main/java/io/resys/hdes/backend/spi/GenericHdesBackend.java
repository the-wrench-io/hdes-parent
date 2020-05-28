package io.resys.hdes.backend.spi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;
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

import io.resys.hdes.backend.api.HdesBackend;
import io.resys.hdes.backend.api.HdesBackendStorage;
import io.resys.hdes.backend.api.ImmutableStatus;

public class GenericHdesBackend implements HdesBackend {

  private final ObjectMapper objectMapper;
  private final HdesBackendStorage storage;
  
  public GenericHdesBackend(ObjectMapper objectMapper, HdesBackendStorage storage) {
    super();
    this.objectMapper = objectMapper;
    this.storage = storage;
  }

  @Override
  public List<Hierarchy> hierarchy() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Search> search() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Status status() {
    return ImmutableStatus.builder()
        .storage(storage.getConfig())
        .errors(storage.errors().build())
        .build();
  }

  @Override
  public DefQueryBuilder query() {
    return new DefQueryBuilder() {
      @Override
      public Collection<Def> find() {
        return storage.read().build();
      }
    };
  }

  @Override
  public DefCreateBuilder builder() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public DefChangeBuilder change() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Writer writer() {
    return new Writer() {
      @Override
      public byte[] build(Object value) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
          objectMapper.writeValue(out, value);
          return out.toByteArray();
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
      }
    };
  }

  @Override
  public Reader reader() {
    // TODO Auto-generated method stub
    return null;
  }

}
