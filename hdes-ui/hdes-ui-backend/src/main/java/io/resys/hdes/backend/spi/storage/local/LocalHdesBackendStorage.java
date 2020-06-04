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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.resys.hdes.ast.api.AstEnvir;
import io.resys.hdes.ast.api.nodes.AstNode.BodyNode;
import io.resys.hdes.ast.api.nodes.AstNode.ErrorNode;
import io.resys.hdes.ast.api.nodes.AstNode.Token;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.DecisionTableBody;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowBody;
import io.resys.hdes.ast.api.nodes.ManualTaskNode.ManualTaskBody;
import io.resys.hdes.ast.spi.ImmutableAstEnvir;
import io.resys.hdes.backend.api.HdesBackend.ConfigType;
import io.resys.hdes.backend.api.HdesBackend.Def;
import io.resys.hdes.backend.api.HdesBackend.DefError;
import io.resys.hdes.backend.api.HdesBackend.DefType;
import io.resys.hdes.backend.api.HdesBackend.LocalStorageConfig;
import io.resys.hdes.backend.api.HdesBackend.StorageConfig;
import io.resys.hdes.backend.api.HdesBackendStorage;
import io.resys.hdes.backend.api.ImmutableDef;
import io.resys.hdes.backend.api.ImmutableDefAst;
import io.resys.hdes.backend.api.ImmutableDefError;
import io.resys.hdes.backend.api.ImmutableLocalStorageConfig;
import io.resys.hdes.backend.spi.storage.GenericStorageWriterEntry;
import io.resys.hdes.backend.spi.storage.HdesResourceBuilder;
import io.resys.hdes.backend.spi.util.Assert;

public class LocalHdesBackendStorage implements HdesBackendStorage {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(LocalHdesBackendStorage.class);
  
  private final Map<String, DefCacheEntry> cache = new HashMap<>();
  private final File location;
  private final LocalStorageConfig storageConfig;
  
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
        if(!cache.isEmpty()) {
          return cache.values().stream().map(e -> e.getDef()).collect(Collectors.toUnmodifiableList());
        }
        
        var log = new StringBuilder()
            .append("Loading .hdes files from '").append(location.getAbsolutePath()).append("':")
            .append(System.lineSeparator());
        var builder = ImmutableAstEnvir.builder().ignoreErrors();
        var keys = new HashMap<String, DefFileKey>();
        
        for(File file : location.listFiles((File dir, String name) -> name.endsWith(".hdes"))) {
          log.append("  - ").append(file.getAbsolutePath()).append(System.lineSeparator());
          
          try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            String src = new String(bytes, StandardCharsets.UTF_8);
            String id = UUID.randomUUID().toString();
            builder.add().externalId(id).src(src);
            keys.put(id, ImmutableDefFileKey.builder().defId(id).fileName(file.getAbsolutePath()).build());
            
          } catch(IOException e) {
            throw new UncheckedIOException(e);
          } 
        }
        
        map(builder.build(), def -> cache.put(def.getId(), 
            ImmutableDefCacheEntry.builder()
            .def(def)
            .key(keys.get(def.getId()))
            .build()));
        LOGGER.debug(log.toString());
        return cache.values().stream().map(e -> e.getDef()).collect(Collectors.toUnmodifiableList());
      }
    };
  }
  
  private void map(AstEnvir astEnvir, Consumer<Def> consumer) {
    for(String id : astEnvir.getBody().keySet()) {
      BodyNode node = astEnvir.getBody(id);
      String src = astEnvir.getSrc(id);
      List<ErrorNode> errors = astEnvir.getErrors(id);
      
      DefType type = null;
      if(node instanceof DecisionTableBody) {
        type = DefType.DT;
      } else if(node instanceof FlowBody) {
        type = DefType.FL;
      } else if(node instanceof ManualTaskBody) {
        type = DefType.MT;
      } else {
        continue;
      }
      
      Def def = ImmutableDef.builder()
          .id(id)
          .value(src)
          .type(type)
          .name(node.getId())
          .errors(errors.stream().map(e -> map(id, node, e)).collect(Collectors.toUnmodifiableList()))
          .ast(ImmutableDefAst.builder().build()).build();  
      
      consumer.accept(def);
    }
  }
  
  private DefError map(String key, BodyNode body, ErrorNode node) {
    Token nodeToken = node.getTarget().getToken();
    return ImmutableDefError.builder()
        .id(key)
        .name(body.getId())
        .message(node.getMessage())
        .token(new StringBuilder()
            .append("(").append(nodeToken.getLine())
            .append(":")
            .append(nodeToken.getCol()).append(")")
            .append(" ").append(nodeToken.getText())
            .toString())
        .build();
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
            String id = UUID.randomUUID().toString();
            toCreate.put(id, HdesResourceBuilder.builder().name(name).type(type).build());
            return writer;
          }
        };
      }

      @Override
      public List<Def> build() {
        var builder = ImmutableAstEnvir.builder().ignoreErrors();
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

        AstEnvir envir = builder.build();

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
            String name = envir.getBody(entry.getKey()).getId();
            
            File file = new File(location, name + ".hdes");
            log.append("  D - ").append(file.getAbsolutePath()).append(System.lineSeparator());
            try {
              file.createNewFile();
              Files.write(file.toPath(), entry.getValue().getBytes(StandardCharsets.UTF_8));
              keys.put(entry.getKey(), ImmutableDefFileKey.builder().defId(entry.getKey()).fileName(file.getAbsolutePath()).build());
            } catch(IOException e) {
              throw new UncheckedIOException(e);
            } 
          }
          
          
          Map<String, DefCacheEntry> newCache = new HashMap<>();
          map(envir, def -> newCache.put(def.getId(), 
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
      if(!file.exists()) {
        throw new LocalStorageException(LocalStorageException.builder().nonExistingLocation(file));
      }
      if(!file.isDirectory()) {
        throw new LocalStorageException(LocalStorageException.builder().locationIsNotDirectory(file));
      }
      if(!file.canWrite()) {
        throw new LocalStorageException(LocalStorageException.builder().locationCantBeWritten(file));
      }
      
      return new LocalHdesBackendStorage(file, ImmutableLocalStorageConfig.builder().type(ConfigType.LOCAL).location(location).build());
    }
  }
}
