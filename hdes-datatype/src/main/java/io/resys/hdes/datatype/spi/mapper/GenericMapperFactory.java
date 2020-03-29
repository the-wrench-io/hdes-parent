package io.resys.hdes.datatype.spi.mapper;

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

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.datatype.api.DataType;
import io.resys.hdes.datatype.api.DataType.ValueType;
import io.resys.hdes.datatype.api.DataTypeService.Mapper;
import io.resys.hdes.datatype.spi.Assert;
import io.resys.hdes.datatype.spi.mapper.deserializers.DateDataTypeDeserializer;
import io.resys.hdes.datatype.spi.mapper.deserializers.DateTimeDataTypeDeserializer;
import io.resys.hdes.datatype.spi.mapper.deserializers.GenericDataTypeDeserializer;
import io.resys.hdes.datatype.spi.mapper.deserializers.JsonObjectDataTypeDeserializer;
import io.resys.hdes.datatype.spi.mapper.deserializers.TimeDataTypeDeserializer;
import io.resys.hdes.datatype.spi.mapper.serializers.GenericDataTypeSerializer;

public class GenericMapperFactory implements MapperFactory {
  private final Map<Class<?>, DataType.ValueType> valueTypes;
  private final Map<DataType.ValueType, Class<?>> beanTypes;
  private final Map<DataType.ValueType, Mapper> mappers;
  
  public GenericMapperFactory(Map<DataType.ValueType, Mapper> mappers,
                              Map<Class<?>, DataType.ValueType> valueTypes) {
    this.mappers = mappers;
    this.valueTypes = valueTypes;
    this.beanTypes = Collections.unmodifiableMap(valueTypes.entrySet().stream()
        .collect(Collectors.toMap(e -> e.getValue(), e -> e.getKey())));
  }

  @Override
  public DataType.ValueType getValueType(Class<?> type) {
    return valueTypes.containsKey(type) ? valueTypes.get(type) : DataType.ValueType.OBJECT;
  }

  @Override
  public Mapper getMapper(DataType.ValueType type) {
    Assert.isTrue(mappers.containsKey(type), () -> String.format("no mapper for: %s!", type));
    return mappers.get(type);
  }
  
  @Override
  public Class<?> getBeanType(ValueType type) {
    Assert.isTrue(beanTypes.containsKey(type), () -> String.format("no bean type for: %s!", type));
    return beanTypes.get(type);
  }
  
  public static Config config(ObjectMapper objectMapper) {
    return new Config(objectMapper);
  }

  public static class Config {
    private final Map<DataType.ValueType, MapperFactory.Deserializer> deserializers = new HashMap<>();
    private final Map<DataType.ValueType, MapperFactory.Serializer> serializers = new HashMap<>();
    private final Map<Class<?>, DataType.ValueType> valueTypes = new HashMap<>();

    private Config(ObjectMapper objectMapper) {
      MapperFactory.Serializer dataTypeSerializer = new GenericDataTypeSerializer(objectMapper);
      serializers.put(DataType.ValueType.ARRAY, dataTypeSerializer);
      serializers.put(DataType.ValueType.OBJECT, dataTypeSerializer);
      serializers.put(DataType.ValueType.DURATION, dataTypeSerializer);
      serializers.put(DataType.ValueType.PERIOD, dataTypeSerializer);
      serializers.put(DataType.ValueType.TIME, dataTypeSerializer);
      serializers.put(DataType.ValueType.STRING, dataTypeSerializer);
      serializers.put(DataType.ValueType.BOOLEAN, dataTypeSerializer);
      serializers.put(DataType.ValueType.DECIMAL, dataTypeSerializer);
      serializers.put(DataType.ValueType.INTEGER, dataTypeSerializer);
      serializers.put(DataType.ValueType.LONG, dataTypeSerializer);
      serializers.put(DataType.ValueType.PERCENT, dataTypeSerializer);
      serializers.put(DataType.ValueType.DATE, dataTypeSerializer);
      serializers.put(DataType.ValueType.DATE_TIME, dataTypeSerializer);

      deserializers.put(DataType.ValueType.ARRAY, new GenericDataTypeDeserializer(objectMapper, List.class));
      deserializers.put(DataType.ValueType.DURATION, null);
      deserializers.put(DataType.ValueType.PERIOD, null);
      deserializers.put(DataType.ValueType.TIME, new TimeDataTypeDeserializer(objectMapper));
      deserializers.put(DataType.ValueType.OBJECT, new JsonObjectDataTypeDeserializer(objectMapper));
      deserializers.put(DataType.ValueType.STRING, new GenericDataTypeDeserializer(objectMapper, String.class));
      deserializers.put(DataType.ValueType.BOOLEAN, new GenericDataTypeDeserializer(objectMapper, Boolean.class));
      deserializers.put(DataType.ValueType.DECIMAL, new GenericDataTypeDeserializer(objectMapper, BigDecimal.class));
      deserializers.put(DataType.ValueType.INTEGER, new GenericDataTypeDeserializer(objectMapper, Integer.class));
      deserializers.put(DataType.ValueType.LONG, new GenericDataTypeDeserializer(objectMapper, Long.class));
      deserializers.put(DataType.ValueType.PERCENT, new GenericDataTypeDeserializer(objectMapper, BigDecimal.class));
      deserializers.put(DataType.ValueType.DATE, new DateDataTypeDeserializer(objectMapper));
      deserializers.put(DataType.ValueType.DATE_TIME, new DateTimeDataTypeDeserializer(objectMapper));

      valueTypes.put(List.class, DataType.ValueType.ARRAY);
      valueTypes.put(Duration.class, DataType.ValueType.DURATION);
      valueTypes.put(Period.class, DataType.ValueType.PERIOD);
      valueTypes.put(LocalTime.class, DataType.ValueType.TIME);
      valueTypes.put(String.class, DataType.ValueType.STRING);
      valueTypes.put(Boolean.class, DataType.ValueType.BOOLEAN);
      valueTypes.put(BigDecimal.class, DataType.ValueType.DECIMAL);
      valueTypes.put(Integer.class, DataType.ValueType.INTEGER);
      valueTypes.put(Long.class, DataType.ValueType.LONG);
      valueTypes.put(LocalDate.class, DataType.ValueType.DATE);
      valueTypes.put(LocalDateTime.class, DataType.ValueType.DATE_TIME);
    }
    public Config valueType(DataType.ValueType valueType, Class<?> type) {
      valueTypes.put(type, valueType);
      return this;
    }
    public Config deserializer(DataType.ValueType valueType, MapperFactory.Deserializer type) {
      deserializers.put(valueType, type);
      return this;
    }
    public Config serializer(DataType.ValueType valueType, MapperFactory.Serializer type) {
      serializers.put(valueType, type);
      return this;
    }
    public GenericMapperFactory build() {
      Map<DataType.ValueType, Mapper> mappers = new HashMap<>();
      for(DataType.ValueType type : DataType.ValueType.values()) {
        mappers.put(type, new GenericDataTypeMapper(serializers.get(type), deserializers.get(type)));
      }
      
      return new GenericMapperFactory(
              Collections.unmodifiableMap(mappers),
              Collections.unmodifiableMap(valueTypes));
    }
  }
}
