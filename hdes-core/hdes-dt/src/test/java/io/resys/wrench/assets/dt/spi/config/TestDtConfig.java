package io.resys.wrench.assets.dt.spi.config;

/*-
 * #%L
 * wrench-assets-dt
 * %%
 * Copyright (C) 2016 - 2019 Copyright 2016 ReSys OÜ
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.function.Supplier;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

import io.resys.wrench.assets.datatype.api.DataTypeRepository;
import io.resys.wrench.assets.datatype.spi.GenericDataTypeRepository;
import io.resys.wrench.assets.dt.api.DecisionTableRepository;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.DecisionTableExpressionBuilder;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.NodeExpressionExecutor;
import io.resys.wrench.assets.dt.spi.GenericDecisionTableRepository;
import io.resys.wrench.assets.dt.spi.expression.GenericDecisionTableExpressionBuilder;
import io.resys.wrench.assets.dt.spi.expression.GenericExpressionExecutor;
import io.resys.wrench.assets.dt.spi.expression.SpringDynamicValueExpressionExecutor;

public class TestDtConfig {
  private static ObjectMapper objectMapper;
  private static DecisionTableRepository decisionTableRepository;

  public static ObjectMapper objectMapper() {
    if (objectMapper == null) {
      objectMapper = new ObjectMapper();
      JsonDeserializer<LocalDateTime> localDateTime = new JsonDeserializer<LocalDateTime>() {
        @Override
        public LocalDateTime deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
          JsonNode node = parser.getCodec().readTree(parser);
          return LocalDateTime.ofInstant(ZonedDateTime.parse(node.asText()).toInstant(), ZoneId.systemDefault());
        }
      };
      JsonSerializer<LocalDate> localDate = new JsonSerializer<LocalDate>() {
        @Override
        public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
          gen.writeString(value.toString());
        }
      };
      SimpleModule jsonDeserializerModule = new SimpleModule();
      jsonDeserializerModule.addDeserializer(LocalDateTime.class, localDateTime);
      jsonDeserializerModule.addSerializer(LocalDate.class, localDate);
      objectMapper.registerModule(jsonDeserializerModule);
    }
    return objectMapper;
  }

  public static DecisionTableRepository decisionTableRepository() {
    if(decisionTableRepository == null) {
      SpringDynamicValueExpressionExecutor springDynamicValueExpressionExecutor = new SpringDynamicValueExpressionExecutor();
      Supplier<DecisionTableExpressionBuilder> springExpressionBuilder = () -> new GenericDecisionTableExpressionBuilder(objectMapper());
      NodeExpressionExecutor expressionExecutor = new GenericExpressionExecutor(springExpressionBuilder);
      DataTypeRepository dataTypeRepository = new GenericDataTypeRepository(objectMapper());
      decisionTableRepository = new GenericDecisionTableRepository(objectMapper(), dataTypeRepository,
          expressionExecutor,
          () -> springDynamicValueExpressionExecutor, springExpressionBuilder);
    }
    return decisionTableRepository;
  }
}