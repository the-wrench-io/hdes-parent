package io.resys.hdes.object.repo.mongodb;

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
import io.resys.hdes.object.repo.spi.GenericObjectRepositoryMapper;
import io.resys.hdes.object.repo.spi.ObjectRepositoryMapper;
import io.resys.hdes.object.repo.spi.ObjectsSerializerAndDeserializer;
import io.resys.hdes.object.repo.spi.RepoAssert;
import io.resys.hdes.object.repo.spi.commands.GenericCheckoutBuilder;
import io.resys.hdes.object.repo.spi.commands.GenericCommitBuilder;
import io.resys.hdes.object.repo.spi.commands.GenericMergeBuilder;
import io.resys.hdes.object.repo.spi.commands.GenericStatusBuilder;
import io.resys.hdes.object.repo.spi.commands.GenericTagBuilder;

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
  public CheckoutBuilder checkout() {
    return new GenericCheckoutBuilder(objects);
  }

  @Override
  public StatusBuilder status() {
    return new GenericStatusBuilder(objects);
  }

  @Override
  public MergeBuilder merge() {
    return new GenericMergeBuilder(objects, () -> commit()) {
      @Override
      protected Objects delete(HeadStatus head) {
        return setObjects(mapper.delete(objects).build(head));
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
            .heads("heads")
            .tags("tags")
            .objects("objects")
            .build();
      }
      ObjectsSerializerAndDeserializer serializer = ObjectsSerializerAndDeserializer.INSTANCE;
      Map<String, Head> heads = new HashMap<>();
      Map<String, Tag> tags = new HashMap<>();
      Map<String, IsObject> values = new HashMap<>();
      Objects objects = ImmutableObjects.builder().values(values).tags(tags).heads(heads).build();
      return new MongoDbObjectRepository(objects,
          new GenericObjectRepositoryMapper<MongoClient>(
              serializer, serializer,
              (v) -> new MongoDbWriter(v, command, config),
              (v) -> new MongoDbDelete(v, command, config)));
    }
  }
}
