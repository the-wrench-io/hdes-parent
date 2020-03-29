package io.resys.hdes.storage.spi.folder;

/*-
 * #%L
 * hdes-storage
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.datatype.spi.Assert;
import io.resys.hdes.storage.api.Changes;
import io.resys.hdes.storage.api.ImmutableChanges;
import io.resys.hdes.storage.spi.ChangesFileCanNotBeReadException;

public class ChangesFolder {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final Logger LOGGER = LoggerFactory.getLogger(ChangesFolder.class);
  private final File source;
  private final String sourceCanonicalPath;
  private final Map<String, Changes> cache = new ConcurrentHashMap<>();

  private ChangesFolder(File source) {
    super();
    this.source = source;
    try {
      sourceCanonicalPath = source.getCanonicalPath();
    } catch (IOException e) {
      throw ChangesFileCanNotBeReadException.builder().file(source).original(e).build();
    }
  }

  public static ChangesFolder from(File source) {
    return new ChangesFolder(source).evict();
  }

  public ChangesFolder evict() {
    StringBuilder log = new StringBuilder("Using file based storage. Evicting cache. ")
        .append(System.lineSeparator()).append("Adding files from: ")
        .append(sourceCanonicalPath);
    cache.clear();
    for (File labelFile : source.listFiles()) {
      for (File assetFile : labelFile.listFiles()) {
        Changes changes = readChanges(assetFile);
        cache.put(changes.getId(), changes);
        log.append(System.lineSeparator())
            .append("   - ").append(labelFile.getName()).append("/").append(assetFile.getName());
      }
    }
    LOGGER.debug(log.toString());
    return this;
  }

  public Collection<Changes> get() {
    return Collections.unmodifiableCollection(cache.values());
  }

  public void set(Changes src) {
    StringBuilder log = new StringBuilder("Using file based storege. Composing file name: ");
    String fileName = sourceCanonicalPath + "/" + src.getLabel() + "/" + src.getId() + ".json";
    log.append(fileName).append(". ");
    File outputFile = new File(fileName);
    boolean exists = outputFile.exists();
    log.append("File exists: ").append(exists).append(". ");
    try {
      if (!exists) {
        outputFile.getParentFile().mkdirs();
        boolean created = outputFile.createNewFile();
        Assert.isTrue(created, () -> "Failed to create new file: " + fileName);
        log.append("Created new file. ");
      } else {
        log.append("File already present. Updating file. ");
      }
      // copy data to file
      FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
      IOUtils.copy(new ByteArrayInputStream(OBJECT_MAPPER.writeValueAsString(src).getBytes(StandardCharsets.UTF_8)), fileOutputStream);
      fileOutputStream.close();
      log.append("Updating file cache id: ").append(src.getId());
      cache.put(src.getId(), src);
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      log.append("Failed to write into file because: ").append(e.getMessage());
      if (!exists && outputFile.exists()) {
        log.append("Deleting created file.");
        outputFile.delete();
      }
      throw ChangesFileCanNotBeReadException.builder().file(outputFile).original(e).build();
    } finally {
      LOGGER.debug(log.toString());
    }
  }

  public void delete(Changes src) {
    String fileName = getFileName(src);
    File outputFile = new File(fileName);
    try {
      if (outputFile.exists()) {
        outputFile.delete();
      }
      cache.remove(src.getId());
    } catch (Exception e) {
      throw ChangesFileCanNotBeReadException.builder().file(outputFile).original(e).build();
    }
  }

  private String getFileName(Changes src) {
    return sourceCanonicalPath + "/" + src.getLabel() + "/" + src.getId() + ".json";
  }

  private static Changes readChanges(File file) {
    try {
      String id = file.getName().substring(0, file.getName().lastIndexOf("."));
      ImmutableChanges original = OBJECT_MAPPER.readValue(file, ImmutableChanges.class);
      return ImmutableChanges.builder().from(original).id(id).build();
    } catch (Exception e) {
      throw ChangesFileCanNotBeReadException.builder().file(file).original(e).build();
    }
  }
}
