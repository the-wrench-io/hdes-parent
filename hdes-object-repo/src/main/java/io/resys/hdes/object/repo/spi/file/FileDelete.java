package io.resys.hdes.object.repo.spi.file;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.resys.hdes.object.repo.api.ImmutableObjects;
import io.resys.hdes.object.repo.api.ObjectRepository.IsObject;
import io.resys.hdes.object.repo.api.ObjectRepository.Objects;
import io.resys.hdes.object.repo.api.ObjectRepository.Ref;
import io.resys.hdes.object.repo.api.ObjectRepository.RefStatus;
import io.resys.hdes.object.repo.api.ObjectRepository.Tag;
import io.resys.hdes.object.repo.api.exceptions.RepoException;
import io.resys.hdes.object.repo.spi.file.FileUtils.FileSystemConfig;
import io.resys.hdes.object.repo.spi.mapper.ObjectRepositoryMapper.Delete;

public class FileDelete implements Delete<File> {
  private static final Logger LOGGER = LoggerFactory.getLogger(FileDelete.class);
  private final FileSystemConfig config;
  private final Objects src;
  private final StringBuilder log = new StringBuilder("Writing transaction: ").append(System.lineSeparator());

  public FileDelete(Objects src, FileSystemConfig config) {
    super();
    this.config = config;
    this.src = src;
  }



  @Override
  public Objects build(RefStatus refStatus) {
    Map<String, Ref> refs = new HashMap<>(src.getRefs());
    Map<String, Tag> tags = new HashMap<>(src.getTags());
    Map<String, IsObject> values = new HashMap<>(src.getValues());
    
    Ref ref = refs.get(refStatus.getName());
    File file = new File(config.getRefs(), ref.getName());
    visitRef(file, ref);
    refs.remove(refStatus.getName());
    
    
    LOGGER.debug(log.toString());
    return ImmutableObjects.builder()
        .values(values)
        .refs(refs)
        .tags(tags)
        .build();
  }

  @Override
  public Ref visitRef(File file, Ref ref) {
    log.append("  - deleting: ").append(file.getPath()).append(" - ").append(ref);
    if(!file.delete()) {
      throw new RepoException("Failed to delete file: " + file.getPath() + "!");
    }
    return ref;
  }
}
