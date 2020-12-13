package io.resys.hdes.backend.spi.storage.local;

/*-
 * #%L
 * hdes-ui-backend
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
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Hex;
import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.resys.hdes.ast.api.RootNodeFactory;
import io.resys.hdes.ast.api.nodes.RootNode;
import io.resys.hdes.backend.api.HdesBackend.ConfigType;
import io.resys.hdes.backend.api.HdesBackend.Def;
import io.resys.hdes.backend.api.HdesBackend.DefError;
import io.resys.hdes.backend.api.HdesBackend.LocalStorageConfig;
import io.resys.hdes.backend.api.HdesBackend.StorageConfig;
import io.resys.hdes.backend.api.HdesBackendStorage;
import io.resys.hdes.backend.api.ImmutableLocalStorageConfig;
import io.resys.hdes.backend.spi.GenericDefBuilder;
import io.resys.hdes.backend.spi.storage.GenericStorageWriterEntry;
import io.resys.hdes.backend.spi.storage.HdesResourceBuilder;
import io.resys.hdes.backend.spi.util.Assert;

public class LocalHdesBackendStorage implements HdesBackendStorage {
  private static final Logger LOGGER = LoggerFactory.getLogger(LocalHdesBackendStorage.class);
  private final File location;
  private final LocalStorageConfig storageConfig;
  private final Map<String, DefCacheEntry> cache = new HashMap<>();

  @Value.Immutable
  public interface DefFileKey extends Comparable<DefFileKey> {
    String getDefId();

    String getFileName();

    @Override
    default int compareTo(DefFileKey o) {
      String o1 = getDefId() + "@" + getFileName();
      String o2 = o.getDefId() + "@" + o.getFileName();
      return o1.compareTo(o2);
    }
  }

  @Value.Immutable
  public interface DefCacheEntry {
    DefFileKey getKey();

    Def getDef();
  }

  public LocalHdesBackendStorage(File location, LocalStorageConfig storageConfig) {
    super();
    this.location = location;
    this.storageConfig = storageConfig;
  }

  @Override
  public StorageConfig config() {
    return storageConfig;
  }

  @Override
  public ErrorReader errors() {
    return new ErrorReader() {
      @Override
      public Collection<DefError> build() {
        var errors = new ArrayList<DefError>();
        cache.values().forEach(v -> errors.addAll(v.getDef().getErrors()));
        return errors;
      }
    };
  }

  @Override
  public StorageReader read() {
    return new StorageReader() {
      @Override
      public Collection<Def> build() {
        cache.clear();
        var log = new StringBuilder()
            .append("Loading .hdes files from '").append(location.getAbsolutePath()).append("':")
            .append(System.lineSeparator());
        var builder = RootNodeFactory.builder().ignoreErrors();
        var keys = new HashMap<String, DefFileKey>();
        for (File file : location.listFiles((File dir, String name) -> name.endsWith(".hdes"))) {
          
          try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            String src = new String(bytes, StandardCharsets.UTF_8);
            String id = LocalHdesBackendStorage.id(file);
            builder.add().externalId(id).src(src);
            keys.put(id, ImmutableDefFileKey.builder().defId(id).fileName(file.getAbsolutePath()).build());
            log.append("  ").append(id).append(" - ").append(file.getAbsolutePath()).append(System.lineSeparator());
          } catch (IOException e) {
            throw new UncheckedIOException(e);
          }
        }
        
        GenericDefBuilder.create().from(builder.build(), def -> cache.put(def.getId(),
            ImmutableDefCacheEntry.builder()
                .def(def)
                .key(keys.get(def.getId()))
                .build()));
        LOGGER.debug(log.toString());
        return cache.values().stream().map(e -> e.getDef()).collect(Collectors.toUnmodifiableList());
      }
    };
  }


  @Override
  public StorageWriter write() {
    return new StorageWriter() {
      private boolean simulation;
      private final StorageWriter writer = this;
      private final Map<String, String> toUpdate = new HashMap<>(); 
      private final List<String> toDelete = new ArrayList<>();
      private final Map<String, String> toCreate = new HashMap<>();
      
      @Override
      public StorageWriter simulation(Boolean simulation) {
        this.simulation = Boolean.TRUE.equals(simulation);
        return this;
      }
      @Override
      public StorageWriterEntry update() {
        return new GenericStorageWriterEntry() {
          @Override
          public StorageWriter build() {
            Assert.notNull(id, () -> "id can't be null!");
            Assert.notEmpty(value, () -> "value can't be empty!");
            toUpdate.put(id, value);
            return writer;
          }
        };
      }
      @Override
      public StorageWriterEntry delete() {
        return new GenericStorageWriterEntry() {
          @Override
          public StorageWriter build() {
            Assert.notNull(id, () -> "id can't be null!");
            toDelete.add(id);
            return writer;
          }
        };
      }
      @Override
      public StorageWriterEntry add() {
        return new GenericStorageWriterEntry() {
          @Override
          public StorageWriter build() {
            String id = LocalHdesBackendStorage.id(new File(location, name + ".hdes"));
            toCreate.put(id, HdesResourceBuilder.builder().name(name).type(type).build());
            return writer;
          }
        };
      }

      @Override
      public List<Def> build() {
        var builder = RootNodeFactory.builder().ignoreErrors();
        var keys = new HashMap<String, DefFileKey>();

        // add existing 
        for(DefCacheEntry entry : cache.values()) {
          if( toDelete.contains(entry.getKey().getDefId()) || 
              toUpdate.containsKey(entry.getKey().getDefId())) {
            
            continue;
          }
          keys.put(entry.getKey().getDefId(), entry.getKey());
          builder.add().externalId(entry.getKey().getDefId()).src(entry.getDef().getValue());
        }
        
        // add new 
        for(Map.Entry<String, String> entry : toCreate.entrySet()) {
          builder.add().externalId(entry.getKey()).src(entry.getValue());
        }
        
        // add to update 
        for(Map.Entry<String, String> entry : toUpdate.entrySet()) {
          keys.put(entry.getKey(), cache.get(entry.getKey()).getKey());
          builder.add().externalId(entry.getKey()).src(entry.getValue());
        }

        RootNode envir = builder.build();

        // write down
        if(!simulation) {
          var log = new StringBuilder()
              .append("Writing .hdes files to '").append(location.getAbsolutePath()).append("':")
              .append(System.lineSeparator());

          // update
          for(String key : toUpdate.keySet()) {
            File file = new File(cache.get(key).getKey().getFileName());
            log.append("  U - ").append(file.getAbsolutePath()).append(System.lineSeparator());
            try {
              Files.write(file.toPath(), toUpdate.get(key).getBytes(StandardCharsets.UTF_8));
              
            } catch(IOException e) {
              throw new UncheckedIOException(e);
            } 
          }

          // delete
          for(String key : toDelete) {
            File file = new File(cache.get(key).getKey().getFileName());
            log.append("  D - ").append(file.getAbsolutePath()).append(System.lineSeparator());
            file.delete();
          }
          
          // create
          for(Map.Entry<String, String> entry : toCreate.entrySet()) {
            String name = envir.getBody(entry.getKey()).getId().getValue();
            Optional<DefCacheEntry> duplicate = cache.values().stream().filter(e -> e.getDef().getName().equals(name)).findFirst();
            if(duplicate.isPresent()) {
              throw new LocalStorageException(LocalStorageException.builder().sameResoureInFile(name, new File(duplicate.get().getKey().getFileName())));
            }
            
            File file = new File(location, name + ".hdes");
            log.append("  D - ").append(file.getAbsolutePath()).append(System.lineSeparator());
            try {
              if(file.exists()) {
                throw new LocalStorageException(LocalStorageException.builder().fileAlreadyExists(file));
              }
              file.createNewFile();
              Files.write(file.toPath(), entry.getValue().getBytes(StandardCharsets.UTF_8));
              keys.put(entry.getKey(), ImmutableDefFileKey.builder().defId(entry.getKey()).fileName(file.getAbsolutePath()).build());
            } catch(IOException e) {
              throw new UncheckedIOException(e);
            } 
          }
          
          
          Map<String, DefCacheEntry> newCache = new HashMap<>();
          GenericDefBuilder.create().from(envir, def -> newCache.put(def.getId(), 
              ImmutableDefCacheEntry.builder()
              .def(def)
              .key(keys.get(def.getId()))
              .build()));
          cache.clear();
          cache.putAll(newCache);
          LOGGER.debug(log.toString());
        }

        return cache.values().stream().map(e -> e.getDef()).collect(Collectors.toUnmodifiableList());
      }
      
    };
  }

  private static String id(File file) {
    try {
      String msg = file.getAbsolutePath();
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      md5.reset();
      md5.update(msg.getBytes(Charset.forName("UTF-8")));
      byte[] digest = md5.digest();
      return Hex.encodeHexString(digest);
    } catch (NoSuchAlgorithmException ex) {
      // Fall back to just hex timestamp in this improbable situation
      LOGGER.warn("MD5 Digester not found, falling back to timestamp hash", ex);
      long timestamp = System.currentTimeMillis();
      return Long.toHexString(timestamp);
    }
  }

  public static Config create() {
    return new Config();
  }

  public static class Config {
    private String location;

    public Config setLocation(String location) {
      this.location = location;
      return this;
    }

    public LocalHdesBackendStorage build() {
      File file = new File(location);
      // dev mode, partial path
      if(!file.exists()) {
        File parent = file.getAbsoluteFile();
        while((parent = parent.getParentFile()) != null) {
          if(parent.exists() && parent.isDirectory() && parent.getName().equals("target")) {
            File possibleLocation = new File(parent.getParentFile(), location);
            if(possibleLocation.exists()) {
              file = possibleLocation;
            }
            break;
          }
        }
      }
      
      if (!file.exists()) {
        throw new LocalStorageException(LocalStorageException.builder().nonExistingLocation(file));
      }
      if (!file.isDirectory()) {
        throw new LocalStorageException(LocalStorageException.builder().locationIsNotDirectory(file));
      }
      if (!file.canWrite()) {
        throw new LocalStorageException(LocalStorageException.builder().locationCantBeWritten(file));
      }
      return new LocalHdesBackendStorage(file, ImmutableLocalStorageConfig.builder().type(ConfigType.LOCAL).location(file.getAbsolutePath()).build());
    }
  }
}
