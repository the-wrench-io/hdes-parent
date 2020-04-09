package io.resys.hdes.object.repo.spi.file;

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
import io.resys.hdes.object.repo.spi.commands.GenericSnapshotBuilder;
import io.resys.hdes.object.repo.spi.commands.GenericStatusBuilder;
import io.resys.hdes.object.repo.spi.commands.GenericTagBuilder;
import io.resys.hdes.object.repo.spi.file.FileUtils.FileSystemConfig;
import io.resys.hdes.object.repo.spi.mapper.GenericObjectRepositoryMapper;
import io.resys.hdes.object.repo.spi.mapper.ObjectRepositoryMapper;

public class FileObjectRepository implements Commands, ObjectRepository {
  private final ObjectRepositoryMapper<File> mapper;
  private Objects objects;
  
  public FileObjectRepository(Objects objects, ObjectRepositoryMapper<File> mapper) {
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
  public PullBuilder pull() {
    // TODO Auto-generated method stub
    return null;
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

    public Config directory(File directory) {
      this.directory = directory;
      return this;
    }

    public ObjectRepository build() {
      try {
        RepoAssert.notNull(directory, () -> "directory must be defined!");
        FileSystemConfig fileSystem = FileUtils.createOrGetRepo(directory);
        
        FileObjectsSerializerAndDeserializer serializer = FileObjectsSerializerAndDeserializer.INSTANCE;
        
        Map<String, Ref> refs = FileUtils.map(fileSystem.getRefs(), (id, v) -> serializer.visitRef(id, v));
        Map<String, Tag> tags = FileUtils.map(fileSystem.getTags(), (id, v) -> serializer.visitTag(id, v));
        Map<String, IsObject> values = FileUtils.map(fileSystem.getObjects(), (id, v) -> serializer.visitObject(id, v));
        
        Objects objects = ImmutableObjects.builder()
        .values(values)
        .tags(tags)
        .refs(refs)
        .build();
        
        return new FileObjectRepository(objects, 
            new GenericObjectRepositoryMapper<File>(
                serializer, serializer,
                (v) -> new FileWriter(v, fileSystem, serializer),
                (v) -> new FileDelete(v, fileSystem)));
      } catch (IOException e) {
        throw new RuntimeException(e.getMessage(), e);
      }
    }
  }
}
