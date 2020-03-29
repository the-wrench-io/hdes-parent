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

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;

import org.apache.commons.lang3.StringUtils;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.resys.hdes.aproc.spi.generator.dt.expressions.CellExpressionFactory;
import io.resys.hdes.aproc.spi.generator.dt.expressions.CellOutputFactory;
import io.resys.hdes.datatype.api.DataType.Direction;
import io.resys.hdes.datatype.api.DataType.ValueType;
import io.resys.hdes.datatype.api.DataTypeInput;
import io.resys.hdes.datatype.api.DataTypeService;
import io.resys.hdes.decisiontable.api.DecisionTableModel;
import io.resys.hdes.decisiontable.api.DecisionTableModel.Cell;
import io.resys.hdes.decisiontable.api.DecisionTableModel.Header;
import io.resys.hdes.decisiontable.api.DecisionTableModel.HitPolicy;
import io.resys.hdes.decisiontable.api.DecisionTableModel.Row;
import io.resys.hdes.execution.HdesService;
import io.resys.hdes.execution.HdesService.Execution;
import io.resys.hdes.execution.HdesService.ExecutionValue;
import io.resys.hdes.execution.ImmutableExecutionValue;

public class DTExecutionBuilderRun extends DTExecutionBuilderTemplate {
  private static final ParameterizedTypeName TYPE_ROW_VALUE = ParameterizedTypeName.get(List.class, ExecutionValue.class);
  private static final ParameterizedTypeName TYPE_ROW_TYPE = ParameterizedTypeName.get(DataTypeService.Operation.class, DataTypeInput.class);
  private final CodeBlock.Builder runMethodCodeBlock = CodeBlock.builder().addStatement("$T values = new $T<>()", TYPE_ROW_VALUE, ArrayList.class);
  private final List<FieldSpec> rowFields = new ArrayList<>();
  private final List<FieldSpec> valueFields = new ArrayList<>();
  private DecisionTableModel model;
  private static final CodeBlock HIT_POLICY_FIRST = CodeBlock.builder()
      .addStatement("emitter.onSuccess(createSuccess(id, values))")
      .addStatement("return;")
      .build();
  private static final CodeBlock HIT_POLICY_ALL = CodeBlock.builder()
      .addStatement("emitter.onSuccess(createSuccess(id, values))")
      .build();

  @Override
  public DTExecutionBuilder model(DecisionTableModel model) {
    this.model = model;
    return this;
  }

  @Override
  public DTExecutionBuilder addRow(Row row) {
    String rowName = getRowName(row);
    String rowValueName = "VALUE_" + rowName;
    CodeBlock.Builder rowField = CodeBlock.builder()
        .beginControlFlow("($T input) ->", DataTypeInput.class);
    
    CodeBlock.Builder rowResult = CodeBlock.builder()
        .add("return $T.builder().from($L)", ImmutableExecutionValue.class, rowValueName);

    CodeBlock.Builder rowValueField = CodeBlock.builder()
        .add("$T.builder()", ImmutableExecutionValue.class);
    
    int cellIndex = 0;
    for (Cell cell : row.getCells()) {
      Header header = model.getHeaders().get(cellIndex);
      cellIndex++;
      
      String name = header.getDirection() + "_" + header.getName().toUpperCase();
      if (header.getDirection() == Direction.IN) {
        ValueType valueType = ValueType.valueOf(header.getValue());
        String src = cell.getValue();
        rowField.addStatement("$T $L = input.apply($L)", Serializable.class, header.getName(), name);
        if(StringUtils.isEmpty(cell.getValue())) {
          continue;
        }

        String exp = CellExpressionFactory.create(header, cell);
        rowField.beginControlFlow("if($L == false)", exp).addStatement("return null").endControlFlow();
        
        rowResult.add("\r\n.putInputs(new $T<>($S, $L))", AbstractMap.SimpleImmutableEntry.class, 
            header.getName(), header.getName());
        
      } else {
        rowValueField.add("\r\n.putOutputs(new $T<>($S, $L))", AbstractMap.SimpleImmutableEntry.class, 
            header.getName(), CellOutputFactory.create(header, cell));
      }
    }
    rowField.add(rowResult.addStatement(".build()").build()).endControlFlow();
    rowFields.add(FieldSpec.builder(TYPE_ROW_TYPE, rowName, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
        .initializer(rowField.build())
        .build());
    valueFields.add(FieldSpec.builder(ExecutionValue.class, rowValueName, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
        .initializer(rowValueField.add(".build()").build())
        .build());
    runMethodCodeBlock
        .addStatement("$T value$L = ($T) $L.apply(input)",
            HdesService.ExecutionValue.class, row.getOrder(), HdesService.ExecutionValue.class, rowName)
        .beginControlFlow("if(value$L != null)", row.getOrder())
        .addStatement("values.add(value$L)", row.getOrder());
    if (model.getHitPolicy() == HitPolicy.FIRST) {
      runMethodCodeBlock.add(HIT_POLICY_FIRST);
    }
    runMethodCodeBlock.endControlFlow();
    return this;
  }

  @Override
  public void build(TypeSpec.Builder typeSpec) {
    if (model.getHitPolicy() == HitPolicy.ALL) {
      runMethodCodeBlock.add(HIT_POLICY_ALL);
    }
    typeSpec
        .addFields(valueFields)
        .addFields(rowFields)
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
            .addStatement("return $T.create(emitter -> run(id, input, emitter))", Single.class)
            .build());
  }

  private String getRowName(Row row) {
    return "ROW_" + row.getOrder();
  }
}
