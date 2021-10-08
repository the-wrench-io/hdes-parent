package io.resys.hdes.client.spi.serializers;

/*-
 * #%L
 * stencil-persistence
 * %%
 * Copyright (C) 2021 Copyright 2021 ReSys OÜ
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

import java.io.IOException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.client.api.HdesStore.Entity;
import io.resys.hdes.client.api.HdesStore.EntityType;
import io.resys.hdes.client.api.ast.AstType;
import io.resys.hdes.client.api.ast.DecisionAstType;
import io.resys.hdes.client.api.ast.FlowAstType;
import io.resys.hdes.client.api.ast.ServiceAstType;
import io.resys.hdes.client.spi.store.PersistenceConfig;


public class ZoeDeserializer implements PersistenceConfig.Deserializer {

  private ObjectMapper objectMapper;
  
  public ZoeDeserializer(ObjectMapper objectMapper) {
    super();
    this.objectMapper = objectMapper;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends AstType> Entity<T> fromString(EntityType entityType, String value) {
    try {
      switch(entityType) {
        case DT: {
          return (Entity<T>) objectMapper.readValue(value, new TypeReference<Entity<DecisionAstType>>() {});
        }
        case FLOW: {
          return (Entity<T>) objectMapper.readValue(value, new TypeReference<Entity<FlowAstType>>() {});
        }
        case FLOW_TASK: {
          return (Entity<T>) objectMapper.readValue(value, new TypeReference<Entity<ServiceAstType>>() {});
        }
        default: throw new RuntimeException("can't map: " + entityType);
      }
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @Override
  public Entity<?> fromString(String value) {
    try {
      JsonNode node = objectMapper.readValue(value, JsonNode.class);
      final EntityType type = EntityType.valueOf(node.get("type").textValue());

      switch (type) {
      case DT: {
        return objectMapper.convertValue(node, new TypeReference<Entity<DecisionAstType>>() {});
      }
      case FLOW: {
        return objectMapper.convertValue(node, new TypeReference<Entity<FlowAstType>>() {});
      }
      case FLOW_TASK: {
        return objectMapper.convertValue(node, new TypeReference<Entity<ServiceAstType>>() {});
      }
      default:
        throw new RuntimeException("can't map: " + node);
      }

    } catch (Exception e) {
      throw new RuntimeException(e.getMessage() + System.lineSeparator() + value, e);
    }
  }
}
