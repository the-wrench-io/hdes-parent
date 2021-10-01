package io.resys.wrench.assets.datatype.spi.builders;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.resys.hdes.client.api.ast.AstType.Direction;
import io.resys.hdes.client.api.ast.AstType.ValueType;
import io.resys.hdes.client.api.model.DataType;
import io.resys.hdes.client.api.model.DataType.DataTypeConstraint;
import io.resys.hdes.client.api.model.DataType.DataTypeDeserializer;
import io.resys.hdes.client.api.model.DataType.DataTypeSerializer;
import io.resys.hdes.client.api.model.DataType.ValueTypeResolver;
import io.resys.wrench.assets.datatype.api.DataTypeRepository.DataTypeBuilder;
import io.resys.wrench.assets.datatype.api.DataTypeRepository.DataTypeConstraintBuilder;
import io.resys.wrench.assets.datatype.spi.beans.ImmutableDataType;
import io.resys.wrench.assets.datatype.spi.util.Assert;

public class GenericDataTypeBuilder implements DataTypeBuilder {

  private final Map<ValueType, DataTypeDeserializer> deserializers;
  private final Map<ValueType, DataTypeSerializer> serializers;
  private final ValueTypeResolver valueTypeResolver;

  private Boolean required;
  private String name;
  private ValueType valueType;
  private Direction direction;
  private Class<?> beanType;
  private String description;
  private String values;
  private List<DataType> properties = new ArrayList<>();
  private List<DataTypeConstraint> constraints = new ArrayList<>();
  private String ref;
  private DataType dataType;

  public GenericDataTypeBuilder(
      Map<ValueType, DataTypeDeserializer> deserializers,
      Map<ValueType, DataTypeSerializer> serializers,
      ValueTypeResolver valueTypeResolver) {
    super();
    this.deserializers = deserializers;
    this.serializers = serializers;
    this.valueTypeResolver = valueTypeResolver;
  }
  @Override
  public DataTypeBuilder required(boolean required) {
    this.required = required;
    return this;
  }
  @Override
  public DataTypeBuilder name(String name) {
    this.name = name;
    return this;
  }
  @Override
  public DataTypeBuilder valueType(ValueType valueType) {
    this.valueType = valueType;
    return this;
  }
  @Override
  public DataTypeBuilder direction(Direction direction) {
    this.direction = direction;
    return this;
  }
  @Override
  public DataTypeBuilder beanType(Class<?> beanType) {
    this.beanType = beanType;
    return this;
  }
  @Override
  public DataTypeBuilder description(String description) {
    this.description = description;
    return this;
  }
  @Override
  public DataTypeBuilder values(String values) {
    this.values = values;
    return this;
  }
  @Override
  public DataTypeConstraintBuilder constraint() {
    return new GenericDataTypeConstraintBuilder() {
      @Override
      public DataTypeConstraint build() {
        DataTypeConstraint result = super.build();
        constraints.add(result);
        return result;
      }
    };
  }
  @Override
  public DataTypeBuilder ref(String ref, DataType dataType) {
    Assert.isTrue(ref != null, () -> "ref can't be null!");
    Assert.isTrue(dataType != null, () -> "dataType can't be null for ref: " + ref + "!");
    this.dataType = dataType;
    return this;
  }
  @Override
  public DataTypeBuilder property() {
    return new GenericDataTypeBuilder(deserializers, serializers, valueTypeResolver) {
      @Override
      public DataType build() {
        DataType property = super.build();
        properties.add(property);
        return property;
      }
    };
  }
  @Override
  public DataType build() {
    Assert.notNull(name, () -> "name can't be null!");

    if(dataType != null) {
      valueType = dataType.getValueType();
      constraints.addAll(dataType.getConstraints());
      properties.addAll(dataType.getProperties());

      DataTypeDeserializer deserializer = dataType.getDeserializer();
      DataTypeSerializer serializer = dataType.getSerializer();

      return new ImmutableDataType(
          name, ref, description,
          direction, valueType, beanType,
          Boolean.TRUE.equals(required), values,
          constraints, properties, deserializer, serializer);
    }

    if(valueType == null) {
      Assert.notNull(beanType, () -> "beanType can't be null!");
      valueType = valueTypeResolver.get(beanType);
    }

    DataTypeDeserializer deserializer = deserializers.get(valueType);
    DataTypeSerializer serializer = serializers.get(valueType);

    Assert.notNull(valueType, () -> "valueType can't be null!");
    return new ImmutableDataType(
        name, ref, description,
        direction, valueType, beanType,
        Boolean.TRUE.equals(required),
        values,
        constraints, properties, deserializer, serializer);
  }
}
