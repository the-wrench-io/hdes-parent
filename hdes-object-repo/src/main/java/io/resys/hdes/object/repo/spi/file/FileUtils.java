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
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.resys.hdes.object.repo.api.ObjectRepository.IsName;
import io.resys.hdes.object.repo.api.ObjectRepository.IsObject;
import io.resys.hdes.object.repo.spi.file.exceptions.FileCantBeWrittenException;

public class FileUtils {
  private static final Logger LOGGER = LoggerFactory.getLogger(FileObjectRepository.class);
  private static final String REPO_PATH = "repo";
  private static final String REFS_PATH = "refs";
  private static final String HEAD_PATH = "head";
  private static final String OBJECTS_PATH = "objects";
  private static final String TAGS_PATH = "tags";

  @Value.Immutable
  public interface FileSystemConfig {
    File getRoot();

    File getRepo();

    File getHead();

    File getRefs();

    File getObjects();

    File getTags();
  }

  @FunctionalInterface
  public interface FileReader<T> {
    T read(String id, byte[] content);
  }

  public static File mkdir(File src) {
    if (src.exists()) {
      return isWritable(src);
    }
    if (src.mkdir()) {
      return src;
    }
    throw new FileCantBeWrittenException(src);
  }

  public static File mkFile(File src) {
    if (src.exists()) {
      return isWritable(src);
    }
    try {
      if (src.createNewFile()) {
        return src;
      }
    } catch (IOException e) {
      throw new FileCantBeWrittenException(src);  
    }
    throw new FileCantBeWrittenException(src);
  }

  public static File isWritable(File src) {
    if (src.canWrite()) {
      return src;
    }
    throw new FileCantBeWrittenException(src);
  }

  public static String getCanonicalNameOrName(File file) {
    try {
      return file.getCanonicalPath();
    } catch (Exception e) {
      return file.getName();
    }
  }

  public static FileSystemConfig createOrGetRepo(File root) {
    FileUtils.isWritable(root);
    StringBuilder log = new StringBuilder("Using file based storage. ");
    File repo = new File(root, REPO_PATH);
    if (repo.exists()) {
      log.append("Using existing repo: ");
    } else {
      FileUtils.mkdir(repo);
      log.append("No existing repo, init new: ");
    }
    FileUtils.isWritable(repo);
    File refs = FileUtils.mkdir(new File(repo, REFS_PATH));
    File head = FileUtils.mkFile(new File(repo, HEAD_PATH));
    File objects = FileUtils.mkdir(new File(repo, OBJECTS_PATH));
    File tags = FileUtils.mkdir(new File(repo, TAGS_PATH));
    log.append(System.lineSeparator())
        .append("  - ").append(repo.getAbsolutePath()).append(System.lineSeparator())
        .append("  - ").append(head.getAbsolutePath()).append(System.lineSeparator())
        .append("  - ").append(refs.getAbsolutePath()).append(System.lineSeparator())
        .append("  - ").append(objects.getAbsolutePath()).append(System.lineSeparator())
        .append("  - ").append(tags.getAbsolutePath()).append(System.lineSeparator());
    LOGGER.debug(log.toString());
    return ImmutableFileSystemConfig.builder()
        .refs(refs)
        .root(root)
        .repo(repo)
        .objects(objects)
        .tags(tags)
        .head(head)
        .build();
  }

  public static <T> Map<String, T> map(File dir, FileReader<T> consumer) throws IOException {
    Map<String, T> result = new HashMap<>();
    for (File subDir : dir.listFiles()) {
      if (subDir.isDirectory()) {
        for (File resourceFile : subDir.listFiles()) {
          String id = subDir.getName() + resourceFile.getName();
          T object = consumer.read(id, Files.readAllBytes(resourceFile.toPath()));
          result.put(((IsObject) object).getId(), object);
        }
      } else {
        String id = subDir.getName();
        T object = consumer.read(id, Files.readAllBytes(subDir.toPath()));
        result.put(((IsName) object).getName(), object);
      }
    }
    return result;
  }
}
