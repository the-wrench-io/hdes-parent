package io.resys.hdes.pm.quarkus.runtime.context;

/*-
 * #%L
 * hdes-projects-quarkus
 * %%
 * Copyright (C) 2020 - 2021 Copyright 2020 ReSys OÜ
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

import org.jboss.resteasy.spi.ReaderException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;

import io.resys.hdes.resource.editor.api.ReRepository;

public class ImmutableHdesResourceEditorContext implements HdesResourceEditorContext {
  private final ObjectMapper objectMapper;
  private final ReRepository repo;
  
  
  public ImmutableHdesResourceEditorContext(ObjectMapper objectMapper, ReRepository repo) {
    super();
    this.objectMapper = objectMapper;
    this.repo = repo;
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
    return new Reader() {
      @Override
      public <T> T build(byte[] body, Class<T> type) {
        try {
          return objectMapper.readValue(body, type);
        } catch(ValueInstantiationException e) {
          throw new ReaderException(e.getMessage(), e);
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
      }

      @Override
      public <T> List<T> list(byte[] body, Class<T> type) {
        try {
          return objectMapper.readValue(body, objectMapper.getTypeFactory().constructCollectionType(List.class, type));
        } catch(ValueInstantiationException e) {
          throw new ReaderException(e.getMessage(), e);
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
      }
    };
  }

  @Override
  public ReRepository repo() {
    return repo;
  }
}
