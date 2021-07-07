package io.resys.wrench.assets.datatype.spi;

/*-
 * #%L
 * wrench-assets-datatypes
 * %%
 * Copyright (C) 2016 - 2018 Copyright 2016 ReSys OÜ
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

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.wrench.assets.datatype.api.DataTypeRepository;
import io.resys.wrench.assets.datatype.spi.builders.GenericDataTypeBuilder;
import io.resys.wrench.assets.datatype.spi.deserializers.DateDataTypeDeserializer;
import io.resys.wrench.assets.datatype.spi.deserializers.DateTimeDataTypeDeserializer;
import io.resys.wrench.assets.datatype.spi.deserializers.GenericDataTypeDeserializer;
import io.resys.wrench.assets.datatype.spi.deserializers.JsonObjectDataTypeDeserializer;
import io.resys.wrench.assets.datatype.spi.deserializers.TimeDataTypeDeserializer;
import io.resys.wrench.assets.datatype.spi.serializers.GenericDataTypeSerializer;

public class GenericDataTypeRepository implements DataTypeRepository {

  private final Map<ValueType, DataTypeDeserializer> deserializers;
  private final Map<ValueType, DataTypeSerializer> serializers;
  private final ValueTypeResolver valueTypeResolver;

  public GenericDataTypeRepository(
      ObjectMapper objectMapper,
      Map<ValueType, DataTypeDeserializer> deserializers,
      Map<ValueType, DataTypeSerializer> serializers,
      ValueTypeResolver valueTypeResolver) {
    super();
    this.deserializers = deserializers;
    this.serializers = serializers;
    this.valueTypeResolver = valueTypeResolver;
  }

  public GenericDataTypeRepository(ObjectMapper objectMapper) {

    Map<ValueType, DataTypeDeserializer> deserializers = new HashMap<>();
    this.deserializers = Collections.unmodifiableMap(deserializers);

    deserializers.put(ValueType.ARRAY, new GenericDataTypeDeserializer(objectMapper, List.class));
    deserializers.put(ValueType.DURATION, null);
    deserializers.put(ValueType.PERIOD, null);
    deserializers.put(ValueType.TIME, new TimeDataTypeDeserializer(objectMapper));

    deserializers.put(ValueType.OBJECT, new JsonObjectDataTypeDeserializer(objectMapper));
    deserializers.put(ValueType.STRING, new GenericDataTypeDeserializer(objectMapper, String.class));
    deserializers.put(ValueType.BOOLEAN, new GenericDataTypeDeserializer(objectMapper, Boolean.class));
    deserializers.put(ValueType.DECIMAL, new GenericDataTypeDeserializer(objectMapper, BigDecimal.class));
    deserializers.put(ValueType.INTEGER, new GenericDataTypeDeserializer(objectMapper, Integer.class));
    deserializers.put(ValueType.LONG, new GenericDataTypeDeserializer(objectMapper, Long.class));
    deserializers.put(ValueType.PERCENT, new GenericDataTypeDeserializer(objectMapper, BigDecimal.class));
    deserializers.put(ValueType.DATE, new DateDataTypeDeserializer(objectMapper));
    deserializers.put(ValueType.DATE_TIME, new DateTimeDataTypeDeserializer(objectMapper));

    Map<ValueType, DataTypeSerializer> serializers = new HashMap<>();
    this.serializers = Collections.unmodifiableMap(serializers);

    DataTypeSerializer dataTypeSerializer = new GenericDataTypeSerializer(objectMapper);
    serializers.put(ValueType.ARRAY, dataTypeSerializer);
    serializers.put(ValueType.OBJECT, dataTypeSerializer);
    serializers.put(ValueType.DURATION, dataTypeSerializer);
    serializers.put(ValueType.PERIOD, dataTypeSerializer);
    serializers.put(ValueType.TIME, dataTypeSerializer);
    serializers.put(ValueType.STRING, dataTypeSerializer);
    serializers.put(ValueType.BOOLEAN, dataTypeSerializer);
    serializers.put(ValueType.DECIMAL, dataTypeSerializer);
    serializers.put(ValueType.INTEGER, dataTypeSerializer);
    serializers.put(ValueType.LONG, dataTypeSerializer);
    serializers.put(ValueType.PERCENT, dataTypeSerializer);
    serializers.put(ValueType.DATE, dataTypeSerializer);
    serializers.put(ValueType.DATE_TIME, dataTypeSerializer);

    Map<Class<?>, ValueType> valueTypes = new HashMap<>();
    valueTypes.put(List.class, ValueType.ARRAY);
    valueTypes.put(Duration.class, ValueType.DURATION);
    valueTypes.put(Period.class, ValueType.PERIOD);
    valueTypes.put(LocalTime.class, ValueType.TIME);
    valueTypes.put(String.class, ValueType.STRING);
    valueTypes.put(Boolean.class, ValueType.BOOLEAN);
    valueTypes.put(BigDecimal.class, ValueType.DECIMAL);
    valueTypes.put(Integer.class, ValueType.INTEGER);
    valueTypes.put(Long.class, ValueType.LONG);
    valueTypes.put(LocalDate.class, ValueType.DATE);
    valueTypes.put(LocalDateTime.class, ValueType.DATE_TIME);
    valueTypeResolver = src -> valueTypes.containsKey(src) ? valueTypes.get(src) : ValueType.OBJECT;

  }
  @Override
  public DataTypeBuilder createBuilder() {
    return new GenericDataTypeBuilder(deserializers, serializers, valueTypeResolver);
  }
}
