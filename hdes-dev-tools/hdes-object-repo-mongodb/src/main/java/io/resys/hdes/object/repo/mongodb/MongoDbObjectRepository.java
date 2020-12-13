package io.resys.hdes.object.repo.mongodb;

/*-
 * #%L
 * hdes-object-repo-mongodb
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.client.MongoClient;

import io.resys.hdes.object.repo.api.ImmutableObjects;
import io.resys.hdes.object.repo.api.ObjectRepository;
import io.resys.hdes.object.repo.api.ObjectRepository.Commands;
import io.resys.hdes.object.repo.mongodb.MongoCommand.MongoDbConfig;
import io.resys.hdes.object.repo.mongodb.writers.MongoDbDelete;
import io.resys.hdes.object.repo.mongodb.writers.MongoDbWriter;
import io.resys.hdes.object.repo.spi.RepoAssert;
import io.resys.hdes.object.repo.spi.commands.GenericCommitBuilder;
import io.resys.hdes.object.repo.spi.commands.GenericMergeBuilder;
import io.resys.hdes.object.repo.spi.commands.GenericSnapshotBuilder;
import io.resys.hdes.object.repo.spi.commands.GenericStatusBuilder;
import io.resys.hdes.object.repo.spi.commands.GenericTagBuilder;
import io.resys.hdes.object.repo.spi.file.FileObjectsSerializerAndDeserializer;
import io.resys.hdes.object.repo.spi.mapper.GenericObjectRepositoryMapper;
import io.resys.hdes.object.repo.spi.mapper.ObjectRepositoryMapper;

public class MongoDbObjectRepository implements Commands, ObjectRepository {
  private final ObjectRepositoryMapper<MongoClient> mapper;
  private Objects objects;

  public MongoDbObjectRepository(Objects objects, ObjectRepositoryMapper<MongoClient> mapper) {
    this.objects = objects;
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
    private MongoCommand<Objects> command;
    private MongoDbConfig config;

    public Config command(MongoCommand<Objects> command) {
      this.command = command;
      return this;
    }

    public Config config(MongoDbConfig config) {
      this.config = config;
      return this;
    }

    public ObjectRepository build() {
      RepoAssert.notNull(command, () -> "command must be defined!");
      if (config == null) {
        config = ImmutableMongoDbConfig.builder()
            .db("repo")
            .refs("refs")
            .tags("tags")
            .objects("objects")
            .build();
      }
      FileObjectsSerializerAndDeserializer serializer = FileObjectsSerializerAndDeserializer.INSTANCE;
      Map<String, Ref> refs = new HashMap<>();
      Map<String, Tag> tags = new HashMap<>();
      Map<String, IsObject> values = new HashMap<>();
      Objects objects = ImmutableObjects.builder().values(values).tags(tags).refs(refs).build();
      
      return new MongoDbObjectRepository(objects,
          new GenericObjectRepositoryMapper<MongoClient>(
              serializer, serializer,
              (v) -> new MongoDbWriter(v, command, config),
              (v) -> new MongoDbDelete(v, command, config)));
    }
  }

  @Override
  public CheckoutBuilder checkout() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public PullCommand pull() {
    // TODO Auto-generated method stub
    return null;
  }
}
