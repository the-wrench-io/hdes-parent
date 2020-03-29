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

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.immutables.value.Value;

import io.resys.hdes.datatype.api.DataType.DataTypeConstraint;
import io.resys.hdes.datatype.api.DataType.Direction;
import io.resys.hdes.datatype.api.DataType.ValueType;

public interface DataTypeService {
  ModelBuilder model();
  Reader read();
  Writer write();
  ExpressionBuilder expression();
  Converter converter();
  Mapper mapper(DataType.ValueType type);
  
  interface Converter {
    Converter input(DataTypeInput input);
    Converter input(Object entity);
    Converter dataTypes(Collection<DataType> dataTypes);
    <T> T build(Class<T> type);
  }
  
  interface Mapper {
    Serializable toValue(Object value, DataType dataType);
    String toString(Object value, DataType dataType);
  }
  
  interface Reader {
    Reader src(String src);
    Reader classpath(String pattern);
    <T> List<T> list(Class<T> targetType);
    <T> T build(Class<T> targetType);
  }

  interface Writer {
    Writer type(Object type);
    String build();
  }
  
  interface ExpressionBuilder {
    ExpressionBuilder src(String src);
    ExpressionBuilder valueType(ValueType valueType);
    ExpressionBuilder srcType(ExpressionSourceType srcType);
    Expression build();
  }

  interface ModelBuilder {
    ModelBuilder ref(String ref, DataType dataType);
    ModelBuilder required(boolean required);
    ModelBuilder array(boolean array);
    ModelBuilder name(String name);

    ModelBuilder valueType(ValueType valueType);
    ModelBuilder direction(Direction direction);
    
    ModelBuilder beanType(Class<?> beanType);
    ModelBuilder description(String description);
    ModelBuilder values(Collection<String> values);

    ConstraintBuilder constraint();
    ModelBuilder property();
    ModelBuilder property(Consumer<ModelBuilder> property);
    DataType build();
  }

  interface ConstraintBuilder {
    ConstraintBuilder values(List<String> values);
    DataTypeConstraint build();
  }

  @Value.Immutable
  interface Expression {
    String getSrc();
    ExpressionSourceType getSrcType();
    ValueType getType();
    List<String> getConstants();
    Operation getOperation();
    
  }
  
  @FunctionalInterface
  interface Operation<T> {
    Object apply(T entity);
  }
  
  enum ExpressionSourceType {
    GROOVY, DT, JEXL
  }
  
  @Target(ElementType.TYPE) 
  @Retention(RetentionPolicy.CLASS) 
  @interface DataTypeFactory {
    DataTypeFactorySource source() default DataTypeFactorySource.LOCAL;
    String url() default "";
    String[] tags() default {};
  }
  
  enum DataTypeFactorySource {
    LOCAL
  }
}
