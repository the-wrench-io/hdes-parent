package io.resys.hdes.client.api.model;

/*-
 * #%L
 * hdes-client-api
 * %%
 * Copyright (C) 2020 - 2021 Copyright 2020 ReSys OÜ
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

import javax.annotation.Nullable;

import io.resys.hdes.client.api.ast.AstType.Direction;
import io.resys.hdes.client.api.ast.AstType.ValueType;

public interface DataType extends Model {

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
  
  interface DataTypeDeserializer {
    Serializable deserialize(DataType dataType, Object value);
  }

  interface DataTypeSerializer {
    String serialize(DataType dataType, Object value);
  }
  interface DataTypeConstraint {
    ConstraintType getType();
  }

  @FunctionalInterface
  interface ValueTypeResolver {
    ValueType get(Class<?> src);
  }

  enum AssociationType { ONE_TO_ONE, ONE_TO_MANY }
  enum ConstraintType { RANGE, VALUES, PATTERN }
}
