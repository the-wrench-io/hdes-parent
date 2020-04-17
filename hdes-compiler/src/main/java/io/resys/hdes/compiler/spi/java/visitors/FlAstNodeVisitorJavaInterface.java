package io.resys.hdes.compiler.spi.java.visitors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/*-
 * #%L
 * hdes-compiler
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

import javax.lang.model.element.Modifier;

import org.immutables.value.Value;
import org.immutables.value.Value.Immutable;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import io.resys.hdes.ast.api.nodes.AstNode.ArrayTypeDefNode;
import io.resys.hdes.ast.api.nodes.AstNode.TypeDefNode;
import io.resys.hdes.ast.api.nodes.AstNode.ObjectTypeDefNode;
import io.resys.hdes.ast.api.nodes.AstNode.ScalarTypeDefNode;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowBody;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowInputs;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowTask;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowTaskPointer;
import io.resys.hdes.compiler.api.Flow;
import io.resys.hdes.compiler.api.Flow.FlowState;
import io.resys.hdes.compiler.api.Flow.FlowTaskState;
import io.resys.hdes.compiler.api.HdesCompilerException;
import io.resys.hdes.compiler.spi.java.visitors.FlJavaSpec.FlInputSpec;
import io.resys.hdes.compiler.spi.java.visitors.FlJavaSpec.FlTaskSpec;
import io.resys.hdes.compiler.spi.java.visitors.FlJavaSpec.FlTypesSpec;

public class FlAstNodeVisitorJavaInterface extends FlAstNodeVisitorTemplate<FlJavaSpec, TypeSpec> {
  private FlowBody body;

  @Override
  public TypeSpec visitFlowBody(FlowBody node) {
    this.body = node;
    ClassName inputType = ClassName.get("", JavaNaming.flInput(node.getId()));
    ClassName stateType = ClassName.get("", JavaNaming.flState(node.getId()));
    TypeSpec.Builder flowBuilder = TypeSpec.interfaceBuilder(node.getId())
        .addModifiers(Modifier.PUBLIC)
        .addSuperinterface(ParameterizedTypeName.get(
            ClassName.get(Flow.class),
            inputType,
            stateType))
        .addTypes(visitFlowInputs(node.getInputs()).getValues());

    // State
    TypeSpec.Builder stateBuilder = TypeSpec.interfaceBuilder(stateType)
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .addAnnotation(Value.Immutable.class)
        .addSuperinterface(ParameterizedTypeName.get(
            ClassName.get(FlowState.class),
            inputType));
    // tasks
    if (node.getTask().isPresent()) {
      FlTaskSpec taskSpecs = visitFlowTask(node.getTask().get());
      
      for(TypeSpec task : taskSpecs.getChildren()) {
        stateBuilder.addMethod(MethodSpec
            .methodBuilder(JavaNaming.getMethod(task.name.substring(body.getId().length())))
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .returns(ParameterizedTypeName.get(
                ClassName.get(Optional.class),
                ClassName.get("", task.name)))
            .build()
            );
      }

      flowBuilder.addTypes(taskSpecs.getChildren());
    }
    return flowBuilder.addType(stateBuilder.build()).build();
  }

  @Override
  public FlTaskSpec visitFlowTask(FlowTask node) {
    ClassName inputType = ClassName.get("", JavaNaming.flTaskInput(body.getId(), node.getId()));
    ClassName outputType = ClassName.get("", JavaNaming.flTaskOutput(body.getId(), node.getId()));

    TypeSpec.Builder stateBuilder = TypeSpec.interfaceBuilder(JavaNaming.flTask(body.getId(), node.getId()))
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .addAnnotation(Value.Immutable.class)
        .addSuperinterface(ParameterizedTypeName.get(
            ClassName.get(FlowTaskState.class),
            inputType, outputType));

    List<TypeSpec> children = node.getNext()
        .map(e -> visitFlowTaskPointer(e).getChildren())
        .orElse(Collections.emptyList());

    return ImmutableFlTaskSpec.builder()
        .addAllChildren(children)
        .addChildren(stateBuilder.build())
        .build();
  }

  @Override
  public FlTaskSpec visitFlowTaskPointer(FlowTaskPointer node) {
    return ImmutableFlTaskSpec.builder()
        //.addAllChildren(children)
        //.addChildren(stateBuilder.build())
        .build();
  }

  @Override
  public FlTypesSpec visitFlowInputs(FlowInputs node) {
    TypeSpec.Builder inputBuilder = TypeSpec
        .interfaceBuilder(JavaNaming.flInput(body.getId()))
        .addAnnotation(Immutable.class)
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
    List<TypeSpec> nested = new ArrayList<>();
    for (TypeDefNode input : node.getValues()) {
      FlInputSpec spec = visitInput(input);
      nested.addAll(spec.getChildren());
      inputBuilder.addMethod(spec.getValue());
    }
    return ImmutableFlTypesSpec.builder()
        .addValues(inputBuilder.build())
        .addAllValues(nested)
        .build();
  }

  private FlInputSpec visitInput(TypeDefNode node) {
    if (node instanceof ScalarTypeDefNode) {
      return visitScalarInputNode((ScalarTypeDefNode) node);
    } else if (node instanceof ArrayTypeDefNode) {
      return visitArrayInputNode((ArrayTypeDefNode) node);
    } else if (node instanceof ObjectTypeDefNode) {
      return visitObjectInputNode((ObjectTypeDefNode) node);
    }
    throw new HdesCompilerException(HdesCompilerException.builder().unknownFlInputRule(node));
  }

  @Override
  public FlInputSpec visitScalarInputNode(ScalarTypeDefNode node) {
    Class<?> returnType = JavaNaming.type(node.getType());
    MethodSpec method = MethodSpec.methodBuilder(JavaNaming.getMethod(node.getName()))
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .returns(node.getRequired() ? ClassName.get(returnType) : ParameterizedTypeName.get(Optional.class, returnType))
        .build();
    return ImmutableFlInputSpec.builder().value(method).build();
  }

  @Override
  public FlInputSpec visitArrayInputNode(ArrayTypeDefNode node) {
    FlInputSpec childSpec = visitInput(node.getValue());
    com.squareup.javapoet.TypeName arrayType;
    if (node.getValue().getRequired()) {
      arrayType = childSpec.getValue().returnType;
    } else {
      arrayType = ((ParameterizedTypeName) childSpec.getValue().returnType).typeArguments.get(0);
    }
    return ImmutableFlInputSpec.builder()
        .value(childSpec.getValue().toBuilder()
            .returns(ParameterizedTypeName.get(ClassName.get(List.class), arrayType))
            .build())
        .children(childSpec.getChildren())
        .build();
  }

  @Override
  public FlInputSpec visitObjectInputNode(ObjectTypeDefNode node) {
    TypeSpec.Builder objectBuilder = TypeSpec
        .interfaceBuilder(JavaNaming.flInputNested(body.getId(), node.getName()))
        .addAnnotation(Immutable.class)
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
    List<TypeSpec> nested = new ArrayList<>();
    for (TypeDefNode input : node.getValues()) {
      FlInputSpec spec = visitInput(input);
      nested.addAll(spec.getChildren());
      objectBuilder.addMethod(spec.getValue());
    }
    TypeSpec objectType = objectBuilder.build();
    ClassName objectTypeName = ClassName.get("", objectType.name);
    nested.add(objectType);
    return ImmutableFlInputSpec.builder()
        .children(nested)
        .value(
            MethodSpec.methodBuilder(JavaNaming.getMethod(node.getName()))
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(node.getRequired() ? objectTypeName : ParameterizedTypeName.get(ClassName.get(Optional.class), objectTypeName))
                .build())
        .build();
  }
}
