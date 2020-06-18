package io.resys.hdes.backend.spi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;

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
import io.resys.hdes.backend.api.HdesBackendStorage.StorageWriter;
import io.resys.hdes.backend.api.ImmutableStatus;
import io.resys.hdes.backend.api.ReaderException;
import io.resys.hdes.compiler.api.HdesCompiler;
import io.resys.hdes.compiler.spi.java.JavaHdesCompiler;

public class GenericHdesBackend implements HdesBackend {
  
  private final ObjectMapper objectMapper;
  private final HdesBackendStorage storage;
  private final HdesCompiler compiler = JavaHdesCompiler.config().build();

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
  public DefDebugBuilder debug() {
    return new GenericDefDebugBuilder(compiler, storage, reader());
  }

  @Override
  public Status status() {
    return ImmutableStatus.builder()
        .storage(storage.config())
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
    return new DefCreateBuilder() {
      private final List<DefCreateEntry> entries = new ArrayList<>();
      @Override
      public List<Def> build() {
        StorageWriter writer = storage.write();
        entries.forEach(e -> writer.add().name(e.getName()).type(e.getType()).build());
        return writer.build();
      }
      @Override
      public DefCreateBuilder add(List<DefCreateEntry> def) {
        entries.addAll(def);
        return this;
      }
      @Override
      public DefCreateBuilder add(DefCreateEntry def) {
        entries.add(def);
        return this;
      }
    };
  }

  @Override
  public DefChangeBuilder change() {
    return new DefChangeBuilder() {
      private final List<DefChangeEntry> entries = new ArrayList<>();
      @Override
      public List<Def> build() {
        StorageWriter writer = storage.write();
        entries.forEach(e -> writer.update().id(e.getId()).value(e.getValue()).build());
        return writer.build();
      }
      @Override
      public DefChangeBuilder add(DefChangeEntry def) {
        entries.add(def);
        return this;
      }
    };
  }

  @Override
  public DefDeleteBuilder delete() {
    return new DefDeleteBuilder() {
      private final List<String> entries = new ArrayList<>();
      private boolean simulation;
      @Override
      public List<Def> build() {
        StorageWriter writer = storage.write().simulation(simulation);
        entries.forEach(e -> writer.delete().id(e).build());
        return writer.build();
      }
      @Override
      public DefDeleteBuilder simulation(boolean simulation) {
        this.simulation = simulation;
        return this;
      }
      @Override
      public DefDeleteBuilder entry(DefDeleteEntry entry) {
        this.simulation(Boolean.TRUE.equals(entry.getSimulation()));
        entries.addAll(entry.getId());
        return this;
      }
      @Override
      public DefDeleteBuilder add(String defId) {
        this.entries.add(defId);
        return this;
      }
    };
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
}
