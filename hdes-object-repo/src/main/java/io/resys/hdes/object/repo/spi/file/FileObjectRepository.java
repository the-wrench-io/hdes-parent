package io.resys.hdes.object.repo.spi.file;

/*-
 * #%L
 * hdes-object-repo
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
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.resys.hdes.object.repo.api.ImmutableObjects;
import io.resys.hdes.object.repo.api.ObjectRepository;
import io.resys.hdes.object.repo.api.ObjectRepository.Commands;
import io.resys.hdes.object.repo.spi.RepoAssert;
import io.resys.hdes.object.repo.spi.commands.GenericCheckoutBuilder;
import io.resys.hdes.object.repo.spi.commands.GenericCommitBuilder;
import io.resys.hdes.object.repo.spi.commands.GenericMergeBuilder;
import io.resys.hdes.object.repo.spi.commands.GenericPullCommand;
import io.resys.hdes.object.repo.spi.commands.GenericSnapshotBuilder;
import io.resys.hdes.object.repo.spi.commands.GenericStatusBuilder;
import io.resys.hdes.object.repo.spi.commands.GenericTagBuilder;
import io.resys.hdes.object.repo.spi.file.FileUtils.FileSystemConfig;
import io.resys.hdes.object.repo.spi.mapper.GenericObjectRepositoryMapper;
import io.resys.hdes.object.repo.spi.mapper.ObjectRepositoryMapper;

public class FileObjectRepository implements Commands, ObjectRepository {
  private final ObjectRepositoryMapper<File> mapper;
  private final FileSystemConfig config;
  private Objects objects;

  public FileObjectRepository(
      FileSystemConfig config,
      ObjectRepositoryMapper<File> mapper) {
    this.config = config;
    this.objects = ImmutableObjects.builder().build();
    this.mapper = mapper;
  }

  @Override
  public CommitBuilder commit() {
    return new GenericCommitBuilder(objects, mapper) {
      @Override
      protected Objects save(List<Object> input) {
        return setObjects(mapper.writer(objects).build(input));
      }
    };
  }

  @Override
  public TagBuilder tag() {
    return new GenericTagBuilder(objects) {
      @Override
      public Tag build() {
        Tag result = super.build();
        setObjects(mapper.writer(objects).build(Arrays.asList(result)));
        return result;
      }
    };
  }

  @Override
  public PullCommand pull() {
    return new GenericPullCommand(objects) {
      @Override
      protected Objects fetch() {
        try {
          FileObjectsSerializerAndDeserializer serializer = FileObjectsSerializerAndDeserializer.INSTANCE;
          Map<String, Ref> refs = FileUtils.map(config.getRefs(), (id, v) -> serializer.visitRef(id, v));
          Map<String, Tag> tags = FileUtils.map(config.getTags(), (id, v) -> serializer.visitTag(id, v));
          Map<String, IsObject> values = FileUtils.map(config.getObjects(), (id, v) -> serializer.visitObject(id, v));
          Objects objects = ImmutableObjects.builder().values(values).tags(tags).refs(refs).build();
          return setObjects(objects);
        } catch (IOException e) {
          throw new RuntimeException(e.getMessage(), e);
        }
      }
    };
  }

  @Override
  public SnapshotBuilder snapshot() {
    return new GenericSnapshotBuilder(objects);
  }

  @Override
  public StatusBuilder status() {
    return new GenericStatusBuilder(objects);
  }

  @Override
  public MergeBuilder merge() {
    return new GenericMergeBuilder(objects, () -> commit()) {
      @Override
      protected Objects delete(RefStatus ref) {
        return setObjects(mapper.delete(objects).build(ref));
      }
    };
  }

  @Override
  public HistoryBuilder history() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public CheckoutBuilder checkout() {
    return new GenericCheckoutBuilder(objects) {
      @Override
      protected Objects save(List<Object> newObjects) {
        return setObjects(mapper.writer(objects).build(newObjects));
      }
    };
  }

  @Override
  public Objects objects() {
    return objects;
  }

  @Override
  public Commands commands() {
    return this;
  }

  private Objects setObjects(Objects objects) {
    this.objects = objects;
    return objects;
  }

  public static Config config() {
    return new Config();
  }

  public static class Config {
    private File directory;
    private boolean sync;

    public Config directory(File directory) {
      this.directory = directory;
      return this;
    }

    // perform sync on checked out branch on startup
    public Config sync() {
      this.sync = true;
      return this;
    }

    public ObjectRepository build() {
      RepoAssert.notNull(directory, () -> "directory must be defined!");
      FileSystemConfig fileSystem = FileUtils.createOrGetRepo(directory);
      FileObjectsSerializerAndDeserializer serializer = FileObjectsSerializerAndDeserializer.INSTANCE;
      ObjectRepository result = new FileObjectRepository(fileSystem,
          new GenericObjectRepositoryMapper<File>(
              serializer, serializer,
              (v) -> new FileWriter(v, fileSystem, serializer),
              (v) -> new FileDelete(v, fileSystem)));
      
      // Load files
      result.commands().pull();
      
      // sync against checkout
      if(sync) {
        
      }
      
      return result;
    }
  }
}
