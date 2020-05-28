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

import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.resys.hdes.ast.api.AstEnvir;
import io.resys.hdes.ast.api.nodes.AstNode.BodyNode;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.DecisionTableBody;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowBody;
import io.resys.hdes.ast.api.nodes.ManualTaskNode.ManualTaskBody;
import io.resys.hdes.ast.spi.ImmutableAstEnvir;
import io.resys.hdes.backend.api.HdesBackend.Def;
import io.resys.hdes.backend.api.HdesBackend.DefType;
import io.resys.hdes.backend.api.HdesBackendStorage;
import io.resys.hdes.backend.api.ImmutableDef;
import io.resys.hdes.backend.api.ImmutableDefAst;

public class LocalHdesBackendStorage implements HdesBackendStorage {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(LocalHdesBackendStorage.class);

  private Map<String, Def> cache = new HashMap<>();
  
  private final File location;
  
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
  
  public LocalHdesBackendStorage(File location) {
    super();
    this.location = location;
  }

  @Override
  public StorageReader read() {
    return new StorageReader() {
      @Override
      public Collection<Def> build() {
        StringBuilder log = new StringBuilder()
            .append("Loading .hdes files from '").append(location.getAbsolutePath()).append("':").append(System.lineSeparator());
        AstEnvir.Builder builder = ImmutableAstEnvir.builder().ignoreErrors();
        
        List<DefFileKey> keys = new ArrayList<>();
        for(File file : location.listFiles((File dir, String name) -> name.endsWith(".hdes"))) {
          log.append("  - ").append(file.getAbsolutePath()).append(System.lineSeparator());
          
          try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            String src = new String(bytes, StandardCharsets.UTF_8);
            String id = UUID.randomUUID().toString();
            builder.add().externalId(id).src(src);
            keys.add(ImmutableDefFileKey.builder().defId(id).fileName(file.getAbsolutePath()).build());
            
          } catch(IOException e) {
            throw new UncheckedIOException(e);
          } 
        }
        
        AstEnvir astEnvir = builder.build();
        for(DefFileKey key : keys) {
          BodyNode node = astEnvir.getBody(key.getDefId());
          String src = astEnvir.getSrc(key.getDefId());
          
          DefType type = null;
          if(node instanceof DecisionTableBody) {
            type = DefType.DT;
          } else if(node instanceof FlowBody) {
            type = DefType.FW;
          } else if(node instanceof ManualTaskBody) {
            type = DefType.MT;
          } else {
            continue;
          }
          
          Def def = ImmutableDef.builder()
              .id(key.getDefId())
              .value(src)
              .type(type)
              .name(node.getId())
              .ast(ImmutableDefAst.builder().build()).build();  
          
          cache.put(def.getId(), def);
        }
        
        LOGGER.debug(log.toString());
        return cache.values();
      }
    };
  }

  @Override
  public StorageWriter write() {
    // TODO Auto-generated method stub
    return null;
  }
  
  
  public static Config config() {
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
      
      return new LocalHdesBackendStorage(file);
    }
  }
}
