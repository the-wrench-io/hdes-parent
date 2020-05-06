package io.resys.hdes.datatype.spi;

/*-
 * #%L
 * hdes-datatype
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

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

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

import io.resys.hdes.datatype.api.DataType;
import io.resys.hdes.datatype.api.DataTypeService;
import io.resys.hdes.datatype.api.exceptions.DataTypeWriterException;
import io.resys.hdes.datatype.spi.builders.DataTypeBuilder;
import io.resys.hdes.datatype.spi.converter.GenericDataTypeConverter;
import io.resys.hdes.datatype.spi.expressions.ExpressionFactory;
import io.resys.hdes.datatype.spi.expressions.GenericExpressionFactory;
import io.resys.hdes.datatype.spi.mapper.GenericMapperFactory;
import io.resys.hdes.datatype.spi.mapper.MapperFactory;

public class GenericDataTypeService implements DataTypeService {

  private final MapperFactory mapperFactory;
  private final ObjectMapper objectMapper;
  private final ExpressionFactory expressionFactory;

  public GenericDataTypeService(MapperFactory mapperFactory, ObjectMapper objectMapper, ExpressionFactory expressionFactory) {
    this.mapperFactory = mapperFactory;
    this.objectMapper = objectMapper;
    this.expressionFactory = expressionFactory;
  }
  @Override
  public ExpressionBuilder expression() {
    return expressionFactory.builder();
  }
  @Override
  public ModelBuilder model() {
    return DataTypeBuilder.create();
  }

  @Override
  public Reader read() {
    return new ObjectMapperTypeBuilder(objectMapper);
  }

  @Override
  public Converter converter() {
    return new GenericDataTypeConverter(objectMapper);
  }
  @Override
  public Mapper mapper(DataType.ValueType type) {
    return mapperFactory.getMapper(type);
  }
  
  @Override
  public Writer write() {
    return new Writer() {
      private Object type;
      @Override
      public Writer type(Object type) {
        this.type = type;
        return this;
      }
      @Override
      public String build() {
        if(type == null) {
          return null;
        }
        try {
          return objectMapper.writeValueAsString(type);
        } catch(Exception e) {
          throw DataTypeWriterException.builder().original(e).type(type).build();
        }
      }
    };
  }

  public static Config config(ObjectMapper objectMapper) {
    return new Config(objectMapper);
  }

  public static Config config() {
    ObjectMapper objectMapper = new ObjectMapper();
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
    return new Config(objectMapper);
  }

  public static class Config {
    private final GenericMapperFactory.Config mapperconfig;
    private final ObjectMapper objectMapper;
    private Optional<ExpressionFactory> expression = Optional.empty();
    
    private Config(ObjectMapper objectMapper) {
      this.mapperconfig = GenericMapperFactory.config(objectMapper);
      this.objectMapper = objectMapper;
    }
    public Config valueType(DataType.ValueType valueType, Class<?> type) {
      mapperconfig.valueType(valueType, type);
      return this;
    }
    public Config deserializer(DataType.ValueType valueType, MapperFactory.Deserializer type) {
      mapperconfig.deserializer(valueType, type);
      return this;
    }
    public Config serializer(DataType.ValueType valueType, MapperFactory.Serializer type) {
      mapperconfig.serializer(valueType, type);
      return this;
    }
    public Config expression(ExpressionFactory expression) {
      this.expression = Optional.ofNullable(expression);
      return this;
    }
    public GenericDataTypeService build() {
      return new GenericDataTypeService(
          mapperconfig.build(), objectMapper, 
          expression.orElseGet(() -> new GenericExpressionFactory(objectMapper)));
    }
  }
}
