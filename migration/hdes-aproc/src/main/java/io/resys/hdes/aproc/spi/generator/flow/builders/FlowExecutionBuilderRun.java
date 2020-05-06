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
import java.util.List;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.resys.hdes.datatype.api.DataTypeInput;
import io.resys.hdes.datatype.api.DataTypeService;
import io.resys.hdes.execution.HdesService.Execution;
import io.resys.hdes.execution.HdesService.ExecutionValue;
import io.resys.hdes.flow.api.FlowAst;
import io.resys.hdes.flow.api.FlowAst.FlowTaskType;
import io.resys.hdes.flow.api.FlowAst.Task;

public class FlowExecutionBuilderRun extends FlowExecutionBuilderTemplate {
  private static final ParameterizedTypeName TYPE_ROW_VALUE = ParameterizedTypeName.get(List.class, ExecutionValue.class);
  private static final ParameterizedTypeName TYPE_ROW_TYPE = ParameterizedTypeName.get(DataTypeService.Operation.class, DataTypeInput.class);
  private final CodeBlock.Builder runMethodCodeBlock = CodeBlock.builder().addStatement("$T values = new $T<>()", TYPE_ROW_VALUE, ArrayList.class);
  private final List<MethodSpec> tasks = new ArrayList<>();
  private FlowAst ast;

  @Override
  public FlowExecutionBuilder ast(FlowAst ast) {
    this.ast = ast;
    CodeBlock.Builder body = CodeBlock.builder();
    Task task = ast.getTask();
    addTaskToBody(body, task);
    tasks.add(MethodSpec.methodBuilder("run")
        .addModifiers(Modifier.PUBLIC)
        .addParameter(String.class, "snapshot")
        .addParameter(String.class, "id")
        .addParameter(DataTypeInput.class, "input")
        .addParameter(ParameterizedTypeName.get(SingleEmitter.class, Execution.class), "emitter")
        // .returns(ParameterizedTypeName.get(Single.class, Execution.class))
        .addCode(body.build())
        .build());
    return this;
  }

  private void addTaskToBody(CodeBlock.Builder body, Task task) {
    FlowTaskType type = task.getType();
    if (type == FlowTaskType.DT || type == FlowTaskType.SERVICE) {
      String methodName = getTaskMethodName(task);
      body.addStatement("$L()", methodName);
    }
    for (Task next : task.getNext()) {
      addTaskToBody(body, next);
    }
  }

  @Override
  public FlowExecutionBuilder task(Task task) {
    FlowTaskType type = task.getType();
    if (type == FlowTaskType.DT || type == FlowTaskType.SERVICE) {
      tasks.add(MethodSpec.methodBuilder(getTaskMethodName(task))
          .addModifiers(Modifier.PRIVATE)
          .addParameter(DataTypeInput.class, "input")
          // .addStatement("return $T.create(emitter -> run(id, input, emitter))",
          // Single.class)
          .build());
    }
    return this;
  }

  @Override
  public void build(TypeSpec.Builder typeSpec) {
    typeSpec
        .addMethods(tasks)
        .addMethod(MethodSpec.methodBuilder("run")
            .addModifiers(Modifier.PUBLIC)
            .addParameter(String.class, "id")
            .addParameter(DataTypeInput.class, "input")
            .addParameter(ParameterizedTypeName.get(SingleEmitter.class, Execution.class), "emitter")
            .addCode(runMethodCodeBlock.build())
            .build())
        .addMethod(MethodSpec.methodBuilder("run")
            .addModifiers(Modifier.PUBLIC)
            .addParameter(String.class, "id")
            .addParameter(DataTypeInput.class, "input")
            .returns(ParameterizedTypeName.get(Single.class, Execution.class))
            .addStatement("return $T.create(emitter -> run(null, id, input, emitter))", Single.class)
            .build());
  }

  private String getTaskMethodName(Task task) {
    return "task" + task.getType().name() + task.getId();
  }
}
