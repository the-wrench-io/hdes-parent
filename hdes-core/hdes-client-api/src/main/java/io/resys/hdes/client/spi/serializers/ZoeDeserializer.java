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

import io.resys.hdes.client.api.HdesStore.StoreEntity;
import io.resys.hdes.client.api.ImmutableStoreEntity;
import io.resys.hdes.client.api.ast.AstBody;
import io.resys.hdes.client.api.ast.AstBody.AstBodyType;
import io.resys.hdes.client.spi.store.thena.PersistenceConfig;


public class ZoeDeserializer implements PersistenceConfig.Deserializer {

  private ObjectMapper objectMapper;
  
  public ZoeDeserializer(ObjectMapper objectMapper) {
    super();
    this.objectMapper = objectMapper;
  }

  @Override
  public <T extends AstBody> StoreEntity fromString(AstBodyType entityType, String value) {
    try {
      return objectMapper.readValue(value, new TypeReference<StoreEntity>() {});
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @Override
  public StoreEntity fromString(String value) {
    try {
      JsonNode node = objectMapper.readValue(value, JsonNode.class);
      return objectMapper.convertValue(node, new TypeReference<ImmutableStoreEntity>() {});
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage() + System.lineSeparator() + value, e);
    }
  }
}
