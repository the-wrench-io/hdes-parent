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

import org.immutables.value.Value;
import org.immutables.value.Value.Immutable;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import io.resys.hdes.ast.api.nodes.AstNode.ArrayTypeDefNode;
import io.resys.hdes.ast.api.nodes.AstNode.DirectionType;
import io.resys.hdes.ast.api.nodes.AstNode.ObjectTypeDefNode;
import io.resys.hdes.ast.api.nodes.AstNode.ScalarTypeDefNode;
import io.resys.hdes.ast.api.nodes.AstNode.TypeDefNode;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowBody;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowInputs;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowOutputs;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowTaskNode;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowTaskPointer;
import io.resys.hdes.ast.api.nodes.FlowNode.ThenPointer;
import io.resys.hdes.ast.api.nodes.FlowNode.WhenThen;
import io.resys.hdes.ast.api.nodes.FlowNode.WhenThenPointer;
import io.resys.hdes.compiler.api.Flow;
import io.resys.hdes.compiler.api.HdesCompilerException;
import io.resys.hdes.compiler.spi.NamingContext;
import io.resys.hdes.compiler.spi.java.JavaSpecUtil;
import io.resys.hdes.compiler.spi.java.visitors.FlJavaSpec.FlHeaderSpec;
import io.resys.hdes.compiler.spi.java.visitors.FlJavaSpec.FlTaskSpec;
import io.resys.hdes.compiler.spi.java.visitors.FlJavaSpec.FlTasksSpec;
import io.resys.hdes.compiler.spi.java.visitors.FlJavaSpec.FlTypesSpec;

public class FlAstNodeVisitorJavaInterface extends FlAstNodeVisitorTemplate<FlJavaSpec, TypeSpec> {
  private final NamingContext naming;
  private FlowBody body;

  public FlAstNodeVisitorJavaInterface(NamingContext naming) {
    super();
    this.naming = naming;
  }

  @Override
  public TypeSpec visitBody(FlowBody node) {
    this.body = node;
    TypeSpec.Builder flowBuilder = TypeSpec.interfaceBuilder(naming.fl().interfaze(node))
        .addModifiers(Modifier.PUBLIC)
        .addAnnotation(AnnotationSpec.builder(javax.annotation.Generated.class).addMember("value", "$S", FlAstNodeVisitorJavaInterface.class.getCanonicalName()).build())
        .addSuperinterface(naming.fl().superinterface(node))
        .addTypes(visitInputs(node.getInputs()).getValues())
        .addTypes(visitOutputs(node.getOutputs()).getValues());
    // State
    TypeSpec.Builder stateBuilder = TypeSpec
        .interfaceBuilder(naming.fl().state(node))
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .addAnnotation(Value.Immutable.class)
        .addSuperinterface(naming.fl().stateSuperinterface(node));
    // tasks
    if (node.getTask().isPresent()) {
      
      for (FlTaskSpec task : visitTask(node.getTask().get()).getValues()) {
        flowBuilder.addType(task.getType());
        String typeName = task.getType().name;
        stateBuilder.addMethod(MethodSpec
            .methodBuilder(JavaSpecUtil.getMethodName(typeName.substring(body.getId().length())))
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .returns(ParameterizedTypeName.get(
                task.getTask().getLoop().map(l -> ClassName.get(List.class)).orElse(ClassName.get(Optional.class)),
                ClassName.get("", typeName)))
            .build());
      }
    }
    return flowBuilder.addType(stateBuilder.build()).build();
  }

  @Override
  public FlTasksSpec visitTask(FlowTaskNode node) {
    TypeSpec.Builder stateBuilder = TypeSpec.interfaceBuilder(naming.fl().taskState(body, node))
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .addAnnotation(Value.Immutable.class)
        .addSuperinterface(naming.fl().taskStateSuperinterface(body, node));
    FlTasksSpec children = visitTaskPointer(node.getNext());
    return ImmutableFlTasksSpec.builder()
        .addValues(ImmutableFlTaskSpec.builder().task(node).type(stateBuilder.build()).build())
        .addAllValues(children.getValues())
        .build();
  }

  @Override
  public FlTasksSpec visitTaskPointer(FlowTaskPointer node) {
    if (node instanceof ThenPointer) {
      ThenPointer then = (ThenPointer) node;
      return visitTask(then.getTask().get());
    } else if (node instanceof WhenThenPointer) {
      List<FlTaskSpec> values = new ArrayList<>();
      WhenThenPointer whenThen = (WhenThenPointer) node;
      for (WhenThen c : whenThen.getValues()) {
        values.addAll(visitTaskPointer(c.getThen()).getValues());
      }
      return ImmutableFlTasksSpec.builder().values(values).build();
    }
    return ImmutableFlTasksSpec.builder().build();
  }

  @Override
  public FlTypesSpec visitInputs(FlowInputs node) {
    TypeSpec.Builder inputBuilder = TypeSpec
        .interfaceBuilder(naming.fl().input(body))
        .addSuperinterface(Flow.FlowInput.class)
        .addAnnotation(Immutable.class)
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
    List<TypeSpec> nested = new ArrayList<>();
    for (TypeDefNode input : node.getValues()) {
      FlHeaderSpec spec = visitTypeDef(input);
      nested.addAll(spec.getChildren());
      inputBuilder.addMethod(spec.getValue());
    }
    return ImmutableFlTypesSpec.builder()
        .addValues(inputBuilder.build())
        .addAllValues(nested)
        .build();
  }

  @Override
  public FlTypesSpec visitOutputs(FlowOutputs node) {
    TypeSpec.Builder outputBuilder = TypeSpec
        .interfaceBuilder(naming.fl().output(body))
        .addSuperinterface(Flow.FlowOutput.class)
        .addAnnotation(Immutable.class)
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
    List<TypeSpec> nested = new ArrayList<>();
    for (TypeDefNode output : node.getValues()) {
      FlHeaderSpec spec = visitTypeDef(output);
      nested.addAll(spec.getChildren());
      outputBuilder.addMethod(spec.getValue());
    }
    return ImmutableFlTypesSpec.builder()
        .addValues(outputBuilder.build())
        .addAllValues(nested)
        .build();
  }

  private FlHeaderSpec visitTypeDef(TypeDefNode node) {
    if (node instanceof ScalarTypeDefNode) {
      return visitScalarDef((ScalarTypeDefNode) node);
    } else if (node instanceof ArrayTypeDefNode) {
      return visitArrayDef((ArrayTypeDefNode) node);
    } else if (node instanceof ObjectTypeDefNode) {
      return visitObjectDef((ObjectTypeDefNode) node);
    }
    throw new HdesCompilerException(HdesCompilerException.builder().unknownFlInputRule(node));
  }

  @Override
  public FlHeaderSpec visitScalarDef(ScalarTypeDefNode node) {
    Class<?> returnType = JavaSpecUtil.type(node.getType());
    MethodSpec method = MethodSpec.methodBuilder(JavaSpecUtil.getMethodName(node.getName()))
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .returns(node.getRequired() ? ClassName.get(returnType) : ParameterizedTypeName.get(Optional.class, returnType))
        .build();
    return ImmutableFlHeaderSpec.builder().value(method).build();
  }

  @Override
  public FlHeaderSpec visitArrayDef(ArrayTypeDefNode node) {
    FlHeaderSpec childSpec = visitTypeDef(node.getValue());
    com.squareup.javapoet.TypeName arrayType;
    if (node.getValue().getRequired()) {
      arrayType = childSpec.getValue().returnType;
    } else {
      arrayType = ((ParameterizedTypeName) childSpec.getValue().returnType).typeArguments.get(0);
    }
    return ImmutableFlHeaderSpec.builder()
        .value(childSpec.getValue().toBuilder()
            .returns(ParameterizedTypeName.get(ClassName.get(List.class), arrayType))
            .build())
        .children(childSpec.getChildren())
        .build();
  }

  @Override
  public FlHeaderSpec visitObjectDef(ObjectTypeDefNode node) {
    ClassName typeName = node.getDirection() == DirectionType.IN ? naming.fl().input(body, node) : naming.fl().output(body, node);
    TypeSpec.Builder objectBuilder = TypeSpec
        .interfaceBuilder(typeName)
        .addSuperinterface(node.getDirection() == DirectionType.IN ? Flow.FlowInput.class : Flow.FlowOutput.class)
        .addAnnotation(Immutable.class)
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
    List<TypeSpec> nested = new ArrayList<>();
    for (TypeDefNode input : node.getValues()) {
      FlHeaderSpec spec = visitTypeDef(input);
      nested.addAll(spec.getChildren());
      objectBuilder.addMethod(spec.getValue());
    }
    TypeSpec objectType = objectBuilder.build();
    nested.add(objectType);
    return ImmutableFlHeaderSpec.builder()
        .children(nested)
        .value(
            MethodSpec.methodBuilder(JavaSpecUtil.getMethodName(node.getName()))
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(node.getRequired() ? typeName : ParameterizedTypeName.get(ClassName.get(Optional.class), typeName))
                .build())
        .build();
  }
}
