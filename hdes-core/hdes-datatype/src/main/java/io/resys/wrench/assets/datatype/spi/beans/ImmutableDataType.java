package io.resys.wrench.assets.datatype.spi.beans;

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

import java.io.Serializable;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.resys.hdes.client.api.ast.AstType.Direction;
import io.resys.hdes.client.api.ast.AstType.ValueType;
import io.resys.hdes.client.api.model.DataType;

public class ImmutableDataType implements DataType {

  private final String name;
  private final String description;
  private final String ref;
  private final Direction direction;
  private final ValueType valueType;
  private final Class<?> beanType;
  private final boolean required;

  private final String values;
  private final Collection<DataTypeConstraint> constraints;
  private final Collection<DataType> properties;

  @JsonIgnore
  private final DataTypeDeserializer deserializer;
  @JsonIgnore
  private final DataTypeSerializer serializer;

  public ImmutableDataType(
      String name, String ref, String description, Direction direction,
      ValueType valueType, Class<?> beanType, boolean required,
      String values,
      Collection<DataTypeConstraint> constraints,
      Collection<DataType> properties,
      DataTypeDeserializer deserializer,
      DataTypeSerializer serializer) {
    super();
    this.name = name;
    this.ref = ref;
    this.description = description;
    this.direction = direction;
    this.valueType = valueType;
    this.beanType = beanType;
    this.required = required;
    this.values = values;
    this.constraints = constraints;
    this.properties = properties;
    this.deserializer = deserializer;
    this.serializer = serializer;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public Direction getDirection() {
    return direction;
  }

  @Override
  public ValueType getValueType() {
    return valueType;
  }

  @Override
  public Class<?> getBeanType() {
    return beanType;
  }

  @Override
  public boolean isRequired() {
    return required;
  }

  @Override
  public Collection<DataTypeConstraint> getConstraints() {
    return constraints;
  }

  @Override
  public Collection<DataType> getProperties() {
    return properties;
  }

  @Override
  public Serializable toValue(Object value) {
    return deserializer.deserialize(this, value);
  }

  @Override
  public String toString(Object value) {
    return serializer.serialize(this, value);
  }

  @Override
  public String getRef() {
    return ref;
  }

  @Override
  public DataTypeDeserializer getDeserializer() {
    return deserializer;
  }

  @Override
  public DataTypeSerializer getSerializer() {
    return serializer;
  }

  @Override
  public String getValues() {
    return values;
  }
}
