package io.resys.hdes.client.spi;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.client.api.HdesAstTypes.DataTypeAstBuilder;
import io.resys.hdes.client.api.ast.AstDataType;
import io.resys.hdes.client.api.ast.AstDataType.DataTypeDeserializer;
import io.resys.hdes.client.api.ast.AstDataType.DataTypeSerializer;
import io.resys.hdes.client.api.ast.AstDataType.Direction;
import io.resys.hdes.client.api.ast.AstDataType.ValueType;
import io.resys.hdes.client.api.ast.AstDataType.ValueTypeResolver;
import io.resys.hdes.client.api.ast.AstType.AstExpression;
import io.resys.hdes.client.api.ast.ImmutableAstDataType;
import io.resys.hdes.client.spi.expression.OperationFactory;
import io.resys.hdes.client.spi.serializers.DateDataTypeDeserializer;
import io.resys.hdes.client.spi.serializers.DateTimeDataTypeDeserializer;
import io.resys.hdes.client.spi.serializers.GenericDataTypeDeserializer;
import io.resys.hdes.client.spi.serializers.GenericDataTypeSerializer;
import io.resys.hdes.client.spi.serializers.JsonObjectDataTypeDeserializer;
import io.resys.hdes.client.spi.serializers.TimeDataTypeDeserializer;
import io.resys.hdes.client.spi.util.Assert;

public class HdesDataTypeFactory {

  private final Map<ValueType, DataTypeDeserializer> deserializers;
  private final Map<ValueType, DataTypeSerializer> serializers;
  private final ValueTypeResolver valueTypeResolver;
  private final ObjectMapper objectMapper; 
  
  public HdesDataTypeFactory(
      ObjectMapper objectMapper,
      Map<ValueType, DataTypeDeserializer> deserializers,
      Map<ValueType, DataTypeSerializer> serializers,
      ValueTypeResolver valueTypeResolver) {
    super();
    this.deserializers = deserializers;
    this.serializers = serializers;
    this.valueTypeResolver = valueTypeResolver;
    this.objectMapper = objectMapper;
  }

  public HdesDataTypeFactory(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
    
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

  public DataTypeAstBuilder dataType() {
    return new GenericDataTypeBuilder();
  }
  
  public AstExpression expression(ValueType valueType, String src) {
    AstExpression expression = OperationFactory.builder()
        .objectMapper(objectMapper)
        .valueType(valueType)
        .src(src).build();
    return expression;
  }
  
  public class GenericDataTypeBuilder implements DataTypeAstBuilder {
    private Boolean required;
    private String name;
    private ValueType valueType;
    private Direction direction;
    private Class<?> beanType;
    private String description;
    private String values;
    private List<AstDataType> properties = new ArrayList<>();
    private String ref;
    private AstDataType dataType;
    private Integer order;
    private String script;
    private String id;
    private boolean data = true;

    @Override
    public DataTypeAstBuilder required(boolean required) {
      this.required = required;
      return this;
    }
    @Override
    public DataTypeAstBuilder data(boolean data) {
      this.data = data;
      return this;
    }
    @Override
    public DataTypeAstBuilder name(String name) {
      this.name = name;
      return this;
    }
    @Override
    public DataTypeAstBuilder order(Integer order) {
      this.order = order;
      return this;
    }
    @Override
    public DataTypeAstBuilder script(String script) {
      this.script = script;
      return this;
    }
    @Override
    public DataTypeAstBuilder id(String id) {
      this.id = id;
      return this;
    }
    @Override
    public DataTypeAstBuilder valueType(ValueType valueType) {
      this.valueType = valueType;
      return this;
    }
    @Override
    public DataTypeAstBuilder direction(Direction direction) {
      this.direction = direction;
      return this;
    }
    @Override
    public DataTypeAstBuilder beanType(Class<?> beanType) {
      this.beanType = beanType;
      return this;
    }
    @Override
    public DataTypeAstBuilder description(String description) {
      this.description = description;
      return this;
    }
    @Override
    public DataTypeAstBuilder values(String values) {
      this.values = values;
      return this;
    }
    @Override
    public DataTypeAstBuilder ref(String ref, AstDataType dataType) {
      Assert.isTrue(ref != null, () -> "ref can't be null!");
      Assert.isTrue(dataType != null, () -> "dataType can't be null for ref: " + ref + "!");
      this.dataType = dataType;
      return this;
    }
    @Override
    public DataTypeAstBuilder property() {
      return new GenericDataTypeBuilder() {
        @Override
        public AstDataType build() {
          AstDataType property = super.build();
          properties.add(property);
          return property;
        }
      };
    }
    @Override
    public AstDataType build() {
      Assert.notNull(name, () -> "name can't be null!");

      if(dataType != null) {
        valueType = dataType.getValueType();
        properties.addAll(dataType.getProperties());

        DataTypeDeserializer deserializer = dataType.getDeserializer();
        DataTypeSerializer serializer = dataType.getSerializer();
        return ImmutableAstDataType.builder()
            .id(id).script(script).order(order)
            .name(name).ref(ref).description(description)
            .direction(direction)
            .valueType(valueType)
            .beanType(beanType)
            .isRequired(Boolean.TRUE.equals(required))
            .values(values)
            .properties(properties)
            .deserializer(deserializer)
            .serializer(serializer)
            .build();
      }

      if(valueType == null) {
        Assert.notNull(beanType, () -> "beanType can't be null!");
        valueType = valueTypeResolver.get(beanType);
      }

      DataTypeDeserializer deserializer = deserializers.get(valueType);
      DataTypeSerializer serializer = serializers.get(valueType);

      Assert.notNull(valueType, () -> "valueType can't be null!");
      return ImmutableAstDataType.builder()
          .id(id).script(script).order(order)
          .name(name)
          .ref(ref)
          .data(data)
          .description(description)
          .direction(direction)
          .valueType(valueType)
          .beanType(beanType)
          .isRequired(Boolean.TRUE.equals(required))
          .values(values)
          .properties(properties)
          .deserializer(deserializer)
          .serializer(serializer)
          .build();
    }
  }
}
