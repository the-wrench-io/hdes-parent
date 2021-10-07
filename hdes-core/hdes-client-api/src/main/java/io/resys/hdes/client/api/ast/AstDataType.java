package io.resys.hdes.client.api.ast;

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

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.resys.hdes.client.api.model.Model;


@Value.Immutable
public abstract class AstDataType implements Model {
  private static final long serialVersionUID = -1945170579949676929L;
  
  public abstract String getName();
  public abstract Direction getDirection();
  public abstract ValueType getValueType();
  public abstract boolean isRequired();
  public abstract Collection<AstDataType> getProperties();

  @Nullable
  public abstract Class<?> getBeanType();
  @Nullable
  public abstract String getDescription();
  @Nullable
  public abstract String getValues();
  @Nullable
  public abstract String getRef();
  
  @JsonIgnore
  public abstract DataTypeDeserializer getDeserializer();
  @JsonIgnore
  public abstract DataTypeSerializer getSerializer();
  @JsonIgnore
  public Serializable toValue(Object value) {
    return getDeserializer().deserialize(this, value);
  }
  @JsonIgnore
  public String toString(Object value) {
    return getSerializer().serialize(this, value);
  }
    
  public interface DataTypeDeserializer {
    Serializable deserialize(AstDataType dataType, Object value);
  }

  public interface DataTypeSerializer {
    String serialize(AstDataType dataType, Object value);
  }

  @FunctionalInterface
  public interface ValueTypeResolver {
    ValueType get(Class<?> src);
  }

  public enum AssociationType { ONE_TO_ONE, ONE_TO_MANY }
  public enum Direction { IN, OUT }
  public enum ValueType {
    TIME, DATE, DATE_TIME, INSTANT, STRING, INTEGER, LONG, DECIMAL, BOOLEAN, PERIOD, DURATION, PERCENT, OBJECT, ARRAY;
  }
}
