package io.resys.hdes.aproc.spi.generator.dt.builders;

/*-
 * #%L
 * hdes-aproc
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import io.resys.hdes.aproc.spi.generator.MapperGenerator;
import io.resys.hdes.datatype.api.DataType;
import io.resys.hdes.datatype.api.DataType.Direction;
import io.resys.hdes.datatype.api.DataType.ValueType;
import io.resys.hdes.decisiontable.api.DecisionTableModel;
import io.resys.hdes.decisiontable.api.DecisionTableModel.Header;
import io.resys.hdes.storage.api.Changes;

public class DTExecutionBuilderGetters extends DTExecutionBuilderTemplate {
  private static final ParameterizedTypeName TYPE_COLLECTION_DATA_TYPE = ParameterizedTypeName.get(Collection.class, DataType.class);
  private final List<FieldSpec> fields = new ArrayList<>();
  private final StringBuilder names = new StringBuilder();

  @Override
  public DTExecutionBuilder changes(Changes changes) {
    fields.add(FieldSpec.builder(String.class, "ID", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
        .initializer("$S", changes.getId()).build());
    fields.add(FieldSpec.builder(String.class, "LABEL", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
        .initializer("$S", changes.getLabel()).build());
    return this;
  }

  @Override
  public DTExecutionBuilder model(DecisionTableModel model) {
    fields.add(FieldSpec.builder(String.class, "NAME", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
        .initializer("$S", model.getName()).build());
    return this;
  }

  @Override
  public DTExecutionBuilder addHeader(Header entry) {
    String name = entry.getDirection() + "_" + entry.getName().toUpperCase();
    fields.add(FieldSpec.builder(DataType.class, name, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
        .initializer("$T.get().model().beanType($T.class).required($L).name($S).valueType($T.$L).direction($T.$L).build()",
            MapperGenerator.TYPE_NAME,
            getBeanType(entry),
            false, entry.getName(),
            ValueType.class, ValueType.valueOf(entry.getValue()), Direction.class, entry.getDirection()).build());
    if (names.length() > 0) {
      names.append(", ");
    }
    names.append(name);
    return this;
  }

  private Class<?> getBeanType(Header entry) {
    switch (ValueType.valueOf(entry.getValue())) {
    case ARRAY:
      return List.class;
    case BOOLEAN:
      return boolean.class;
    case DATE:
      return LocalDate.class;
    case DATE_TIME:
      return LocalDateTime.class;
    case DECIMAL:
      return BigDecimal.class;
    case DURATION:
      return Duration.class;
    case INTEGER:
      return Integer.class;
    case LONG:
      return Long.class;
    case PERCENT:
      return BigDecimal.class;
    case PERIOD:
      return Period.class;
    case STRING:
      return String.class;
    case TIME:
      return LocalTime.class;
    case OBJECT:
      return Object.class;
    default:
      return Object.class;
    }
  }

  @Override
  public void build(TypeSpec.Builder typeSpec) {
    fields.add(FieldSpec.builder(TYPE_COLLECTION_DATA_TYPE, "TYPES", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
        .initializer("$T.unmodifiableList($T.asList($L))", Collections.class, Arrays.class, names.toString()).build());
    typeSpec.addFields(fields)
        .addMethod(MethodSpec.methodBuilder("getName")
            .addModifiers(Modifier.PUBLIC)
            .returns(String.class)
            .addStatement("return NAME")
            .build())
        .addMethod(MethodSpec.methodBuilder("getLabel")
            .addModifiers(Modifier.PUBLIC)
            .returns(String.class)
            .addStatement("return LABEL")
            .build())
        .addMethod(MethodSpec.methodBuilder("getId")
            .addModifiers(Modifier.PUBLIC)
            .returns(String.class)
            .addStatement("return ID")
            .build())
        .addMethod(MethodSpec.methodBuilder("getTypes")
            .addModifiers(Modifier.PUBLIC)
            .returns(TYPE_COLLECTION_DATA_TYPE)
            .addStatement("return TYPES")
            .build());
  }
}
