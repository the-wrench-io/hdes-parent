package io.resys.hdes.aproc.spi.generator.flow.builders;

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

import io.resys.hdes.datatype.api.DataType;
import io.resys.hdes.flow.api.FlowAst;
import io.resys.hdes.storage.api.Changes;

public class FlowExecutionBuilderGetters extends FlowExecutionBuilderTemplate {
  private static final ParameterizedTypeName TYPE_COLLECTION_DATA_TYPE = ParameterizedTypeName.get(Collection.class, DataType.class);
  private final List<FieldSpec> fields = new ArrayList<>();
  private final StringBuilder names = new StringBuilder();

  @Override
  public FlowExecutionBuilder changes(Changes changes) {
    fields.add(FieldSpec.builder(String.class, "ID", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
        .initializer("$S", changes.getId()).build());
    fields.add(FieldSpec.builder(String.class, "LABEL", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
        .initializer("$S", changes.getLabel()).build());
    return this;
  }

  @Override
  public FlowExecutionBuilder ast(FlowAst model) {
    fields.add(FieldSpec.builder(String.class, "NAME", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
        .initializer("$S", model.getId()).build());
    return this;
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
