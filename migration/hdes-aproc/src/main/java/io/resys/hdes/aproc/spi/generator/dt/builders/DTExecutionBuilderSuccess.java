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

import java.time.LocalDateTime;
import java.util.List;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import io.resys.hdes.decisiontable.api.DecisionTableModel;
import io.resys.hdes.execution.HdesService.Execution;
import io.resys.hdes.execution.HdesService.ExecutionValue;
import io.resys.hdes.execution.ImmutableExecution;
import io.resys.hdes.storage.api.Changes;

public class DTExecutionBuilderSuccess extends DTExecutionBuilderTemplate {
  private DecisionTableModel model;
  private Changes changes;
  private String tag;

  @Override
  public DTExecutionBuilder model(DecisionTableModel model) {
    this.model = model;
    return this;
  }

  @Override
  public DTExecutionBuilder tag(String tag) {
    this.tag = tag;
    return this;
  }

  @Override
  public DTExecutionBuilder changes(Changes changes) {
    this.changes = changes;
    return this;
  }

  @Override
  public void build(TypeSpec.Builder typeSpec) {
    typeSpec
        .addMethod(MethodSpec.methodBuilder("createSuccess")
            .addModifiers(Modifier.PRIVATE)
            .addParameter(String.class, "id")
            .addParameter(ParameterizedTypeName.get(List.class, ExecutionValue.class), "values")
            .returns(Execution.class)
            .addStatement("return $T.builder().id(id).name(NAME).label(LABEL).tag($S).localDateTime($T.now()).types(TYPES).value(values).build()",
                ImmutableExecution.class, tag, LocalDateTime.class)
            .build());
  }
}
