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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.resys.hdes.object.repo.api.ObjectRepository;
import io.resys.hdes.object.repo.api.ObjectRepository.CommitBuilder;
import io.resys.hdes.object.repo.api.ObjectRepository.Objects;
import io.resys.hdes.object.repo.api.ObjectRepository.Snapshot;
import io.resys.hdes.object.repo.api.ObjectRepository.TreeEntry;
import io.resys.hdes.object.repo.api.exceptions.EmptyCommitException;
import io.resys.hdes.object.repo.spi.file.FileUtils.FileSystemConfig;

public class FileSyncCommand {
  private static final Logger LOGGER = LoggerFactory.getLogger(FileObjectRepository.class);
  private final FileSystemConfig config;
  private final Objects objects;
  private final ObjectRepository objectRepository;

  public FileSyncCommand(FileSystemConfig config, Objects objects, ObjectRepository objectRepository) {
    super();
    this.config = config;
    this.objects = objects;
    this.objectRepository = objectRepository;
  }

  public void build() {
    if (!objects.getHead().isPresent() ||
        !objects.getRefs().containsKey(objects.getHead().get().getValue())) {
      return;
    }
    Snapshot snapshot = objectRepository.commands().snapshot().from(objects.getHead().get().getValue()).build();
    CommitBuilder builder = objectRepository.commands().commit()
        .author(FileSyncCommand.class.getName())
        .comment("syncing local files");
    try {
      List<String> files = new ArrayList<>();
      for (File file : config.getRoot().listFiles()) {
        if (file.isDirectory()) {
          continue;
        }
        files.add(file.getName());
        builder.add(file.getName(), new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8));
      }
      
      for(TreeEntry entry : snapshot.getTree().getValues().values()) {
        if(!files.contains(entry.getName())) {
          builder.delete(entry.getName());
        }
      }
      
      builder.build();
    } catch (EmptyCommitException empty) {
      LOGGER.debug("No changes locally!");
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
