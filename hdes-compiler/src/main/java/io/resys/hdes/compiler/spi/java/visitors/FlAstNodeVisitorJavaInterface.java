package io.resys.hdes.compiler.spi.java.visitors;

import java.util.ArrayList;
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

import org.immutables.value.Value.Immutable;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import io.resys.hdes.ast.api.nodes.AstNode.ArrayInputNode;
import io.resys.hdes.ast.api.nodes.AstNode.InputNode;
import io.resys.hdes.ast.api.nodes.AstNode.ObjectInputNode;
import io.resys.hdes.ast.api.nodes.AstNode.ScalarInputNode;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowBody;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowInputs;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowTask;
import io.resys.hdes.compiler.api.Flow;
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
    TypeSpec.Builder interfaceBuilder = TypeSpec.interfaceBuilder(node.getId())
        .addModifiers(Modifier.PUBLIC)
        .addSuperinterface(ParameterizedTypeName.get(
            ClassName.get(Flow.class),
            inputType,
            stateType))
        .addTypes(visitFlowInputs(node.getInputs()).getValues());
    return interfaceBuilder.build();
  }
  @Override
    public FlTaskSpec visitFlowTask(FlowTask node) {
      return super.visitFlowTask(node);
    }

  @Override
  public FlTypesSpec visitFlowInputs(FlowInputs node) {
    TypeSpec.Builder inputBuilder = TypeSpec
        .interfaceBuilder(JavaNaming.flInput(body.getId()))
        .addAnnotation(Immutable.class)
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
    
    List<TypeSpec> nested = new ArrayList<>();
    for(InputNode input : node.getValues()) {
      FlInputSpec spec = visitInput(input);
      nested.addAll(spec.getChildren());
      inputBuilder.addMethod(spec.getValue());
    }
    
    return ImmutableFlTypesSpec.builder()
        .addValues(inputBuilder.build())
        .addAllValues(nested)
        .build();
  }
  
  private FlInputSpec visitInput(InputNode node) {
    if(node instanceof ScalarInputNode) {
      return visitScalarInputNode((ScalarInputNode) node);
    } else if(node instanceof ArrayInputNode) {
      return visitArrayInputNode((ArrayInputNode) node);
    } else if(node instanceof ObjectInputNode) {
      return visitObjectInputNode((ObjectInputNode) node);
    }
    throw new HdesCompilerException(HdesCompilerException.builder().unknownFlInputRule(node));
  }
  
  @Override
  public FlInputSpec visitScalarInputNode(ScalarInputNode node) {
    Class<?> returnType = JavaNaming.type(node.getType());
    
    MethodSpec method = MethodSpec.methodBuilder(JavaNaming.getMethod(node.getName()))
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .returns(node.getRequired() ? ClassName.get(returnType) : ParameterizedTypeName.get(Optional.class, returnType))
        .build();
    
    return ImmutableFlInputSpec.builder().value(method).build();
  }

  @Override
  public FlInputSpec visitArrayInputNode(ArrayInputNode node) {
    FlInputSpec childSpec = visitInput(node.getValue());
    
    com.squareup.javapoet.TypeName arrayType;
    if(node.getValue().getRequired()) {
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
  public FlInputSpec visitObjectInputNode(ObjectInputNode node) {
    TypeSpec.Builder objectBuilder = TypeSpec
        .interfaceBuilder(JavaNaming.flInputNested(body.getId(), node.getName()))
        .addAnnotation(Immutable.class)
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
    
    List<TypeSpec> nested = new ArrayList<>();
    for(InputNode input : node.getValues()) {
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
            .build()
            )
        .build();
  }
}
