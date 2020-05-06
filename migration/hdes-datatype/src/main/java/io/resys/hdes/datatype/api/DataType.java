package io.resys.hdes.datatype.api;

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

import java.util.Collection;

import javax.annotation.Nullable;

import org.immutables.value.Value;


@Value.Immutable
public interface DataType {
  @Nullable
  String getName();
  Direction getDirection();
  ValueType getValueType();
  
  boolean isArray();
  boolean isRequired();
  
  Collection<String> getValues();
  Collection<DataTypeConstraint> getConstraints();
  Collection<DataType> getProperties();
  
  @Nullable
  String getRef();
  @Nullable
  String getDescription();
  @Nullable
  Class<?> getBeanType();
  
  interface DataTypeConstraint {
    ConstraintType getType();
  }

  enum AssociationType {
    ONE_TO_ONE, ONE_TO_MANY
  }

  enum ConstraintType {
    RANGE, VALUES, PATTERN
  }

  enum Direction {
    IN, OUT
  }

  enum ValueType {
    TIME, DATE, DATE_TIME,
    STRING,
    
    INTEGER, LONG, DECIMAL, 
    NUMERIC, // General type for numbers
    
    ARRAY, // undefined array type
    
    BOOLEAN, PERIOD, DURATION, PERCENT,
    OBJECT, METHOD_INVOCATION;
  }
}
