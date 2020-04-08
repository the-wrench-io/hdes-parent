package io.resys.hdes.object.repo.spi.file;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.resys.hdes.object.repo.api.ImmutableObjects;
import io.resys.hdes.object.repo.api.ObjectRepository.Head;
import io.resys.hdes.object.repo.api.ObjectRepository.HeadStatus;
import io.resys.hdes.object.repo.api.ObjectRepository.IsObject;
import io.resys.hdes.object.repo.api.ObjectRepository.Objects;
import io.resys.hdes.object.repo.api.ObjectRepository.Tag;
import io.resys.hdes.object.repo.api.exceptions.RepoException;
import io.resys.hdes.object.repo.spi.ObjectRepositoryMapper.Delete;
import io.resys.hdes.object.repo.spi.file.util.FileUtils.FileSystemConfig;

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
  public Objects build(HeadStatus headStatus) {
    Map<String, Head> heads = new HashMap<>(src.getHeads());
    Map<String, Tag> tags = new HashMap<>(src.getTags());
    Map<String, IsObject> values = new HashMap<>(src.getValues());
    
    Head head = heads.get(headStatus.getHead());
    File file = new File(config.getHeads(), head.getName());
    visitHead(file, head);
    heads.remove(headStatus.getHead());
    
    
    LOGGER.debug(log.toString());
    return ImmutableObjects.builder()
        .values(values)
        .heads(heads)
        .tags(tags)
        .build();
  }

  @Override
  public Head visitHead(File file, Head head) {
    log.append("  - deleting: ").append(file.getPath()).append(" - ").append(head);
    if(!file.delete()) {
      throw new RepoException("Failed to delete file: " + file.getPath() + "!");
    }
    return head;
  }
}
