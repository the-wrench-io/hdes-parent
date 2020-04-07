package io.resys.hdes.object.repo.spi.file;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import io.resys.hdes.object.repo.api.ImmutableObjects;
import io.resys.hdes.object.repo.api.ObjectRepository;
import io.resys.hdes.object.repo.api.ObjectRepository.Commands;
import io.resys.hdes.object.repo.spi.GenericObjectRepositoryMapper;
import io.resys.hdes.object.repo.spi.ObjectRepositoryMapper;
import io.resys.hdes.object.repo.spi.ObjectsSerializerAndDeserializer;
import io.resys.hdes.object.repo.spi.commands.GenericCommitBuilder;
import io.resys.hdes.object.repo.spi.file.util.FileUtils;
import io.resys.hdes.object.repo.spi.file.util.FileUtils.FileSystemConfig;

public class FileObjectRepository implements Commands, ObjectRepository {
  private final ObjectRepositoryMapper mapper;
  private Objects objects;
  
  public FileObjectRepository(Objects objects, ObjectRepositoryMapper mapper) {
    this.objects = objects;
    this.mapper = mapper;
  }

  @Override
  public PullBuilder pull() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public StatusBuilder status() {
    // TODO Auto-generated method stub
    return null;
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
  public SnapshotBuilder snapshot() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public HistoryBuilder history() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public TagBuilder tag() {
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
    
    private File directory;

    public Config directory(File directory) {
      this.directory = directory;
      return this;
    }

    public ObjectRepository build() {
      try {
        RepoAssert.notNull(directory, () -> "directory must be defined!");
        FileSystemConfig fileSystem = FileUtils.createOrGetRepo(directory);
        
        ObjectsSerializerAndDeserializer serializer = ObjectsSerializerAndDeserializer.INSTANCE;
        
        Map<String, Head> heads = FileUtils.map(fileSystem.getHeads(), (id, v) -> serializer.visitHead(id, v));
        Map<String, Tag> tags = FileUtils.map(fileSystem.getTags(), (id, v) -> serializer.visitTag(id, v));
        Map<String, IsObject> values = FileUtils.map(fileSystem.getObjects(), (id, v) -> serializer.visitObject(id, v));
        
        Objects objects = ImmutableObjects.builder()
        .values(values)
        .tags(tags)
        .heads(heads)
        .build();
        
        return new FileObjectRepository(objects, 
            new GenericObjectRepositoryMapper(
                serializer, serializer,
                (v) -> new FileWriter(v, fileSystem, serializer)));
      } catch (IOException e) {
        throw new RuntimeException(e.getMessage(), e);
      }
    }
  }
}