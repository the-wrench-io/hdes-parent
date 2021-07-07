package io.resys.wrench.assets.datatype.api;

/*-
 * #%L
 * wrench-assets-datatype
 * %%
 * Copyright (C) 2016 - 2021 Copyright 2020 ReSys OÜ
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
import java.util.List;

import javax.annotation.Nullable;

public interface DataTypeRepository {
  DataTypeBuilder createBuilder();

  interface DataTypeBuilder {
    DataTypeBuilder ref(String ref, DataType dataType);
    DataTypeBuilder required(boolean required);
    DataTypeBuilder name(String name);

    DataTypeBuilder valueType(ValueType valueType);
    DataTypeBuilder direction(Direction direction);
    DataTypeBuilder beanType(Class<?> beanType);
    DataTypeBuilder description(String description);
    DataTypeBuilder values(String values);

    DataTypeConstraintBuilder constraint();
    DataTypeBuilder property();
    DataType build();
  }

  interface DataTypeConstraintBuilder {
    DataTypeConstraintBuilder values(List<String> values);
    DataTypeConstraint build();
  }

  interface DataType {
    String getName();
    String getRef();
    String getDescription();
    Direction getDirection();
    ValueType getValueType();
    Class<?> getBeanType();
    boolean isRequired();

    @Nullable
    String getValues();
    Collection<DataTypeConstraint> getConstraints();
    Collection<DataType> getProperties();

    Serializable toValue(Object value);
    Serializable toString(Object value);
    DataTypeDeserializer getDeserializer();
    DataTypeSerializer getSerializer();
  }

  interface DataTypeDeserializer {
    Serializable deserialize(DataType dataType, Object value);
  }

  interface DataTypeSerializer {
    String serialize(DataType dataType, Object value);
  }

  @FunctionalInterface
  interface ValueTypeResolver {
    ValueType get(Class<?> src);
  }

  interface DataTypeConstraint {
    ConstraintType getType();
  }

  enum AssociationType {
    ONE_TO_ONE, ONE_TO_MANY
  }

  enum ConstraintType {
    RANGE, VALUES, PATTERN
  }

  public enum Direction {
    IN, OUT
  }

  enum ValueType {
    TIME, DATE, DATE_TIME, INSTANT,
    STRING,
    INTEGER, LONG, DECIMAL,
    BOOLEAN, PERIOD, DURATION, PERCENT,
    OBJECT, ARRAY;
  }
}
