package io.resys.hdes.compiler.spi.java.visitors;

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

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import io.resys.hdes.ast.api.AstEnvir;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowBody;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowTask;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowTaskPointer;
import io.resys.hdes.ast.api.nodes.FlowNode.Mapping;
import io.resys.hdes.ast.api.nodes.FlowNode.TaskRef;
import io.resys.hdes.ast.api.nodes.FlowNode.ThenPointer;
import io.resys.hdes.ast.api.nodes.FlowNode.When;
import io.resys.hdes.ast.api.nodes.FlowNode.WhenThen;
import io.resys.hdes.ast.api.nodes.FlowNode.WhenThenPointer;

public class FlAstNodeVisitorJavaGen extends FlAstNodeVisitorTemplate<FlJavaSpec, TypeSpec> {
  
  private final AstEnvir envir;
  private FlowBody body;
  private ClassName flowState;
  
  public FlAstNodeVisitorJavaGen(AstEnvir envir) {
    super();
    this.envir = envir;
  }
  
  @Override
  public TypeSpec visitFlowBody(FlowBody node) {
    this.body = node;
    this.flowState = ClassName.get("", JavaNaming.flState(node.getId()));
    
    TypeSpec.Builder flowBuilder = TypeSpec.classBuilder(ClassName.get("", JavaNaming.flImpl(node.getId())))
        .addModifiers(Modifier.PUBLIC)
        .addSuperinterface(ClassName.get("", node.getId()));

    MethodSpec applyMethod = MethodSpec.methodBuilder("apply")
        .addModifiers(Modifier.PUBLIC)
        .addParameter(ParameterSpec.builder(ClassName.get("", JavaNaming.flInput(node.getId())), "input").build())
        .returns(flowState)
        .addStatement(visitInit(node))
        .addStatement(CodeBlock.builder().add("return currentState").build())
        .build();
 
    return flowBuilder.addMethod(applyMethod).build();
  }
  
  private CodeBlock visitInit(FlowBody node) {
    return CodeBlock.builder()
        .add("$T currentState = Immutable$T.builder().input(input).build()", flowState, flowState)
        .build();
  }
  
  @Override
  public FlJavaSpec visitFlowTask(FlowTask node) {
    // TODO Auto-generated method stub
    return super.visitFlowTask(node);
  }
  
  @Override
  public FlJavaSpec visitTaskRef(TaskRef node) {
    // TODO Auto-generated method stub
    return super.visitTaskRef(node);
  }
  
  @Override
  public FlJavaSpec visitFlowTaskPointer(FlowTaskPointer node) {
    // TODO Auto-generated method stub
    return super.visitFlowTaskPointer(node);
  }
  
  @Override
  public FlJavaSpec visitWhen(When node) {
    // TODO Auto-generated method stub
    return super.visitWhen(node);
  }
  
  @Override
  public FlJavaSpec visitThenPointer(ThenPointer node) {
    // TODO Auto-generated method stub
    return super.visitThenPointer(node);
  }
  
  @Override
  public FlJavaSpec visitMapping(Mapping node) {
    // TODO Auto-generated method stub
    return super.visitMapping(node);
  }
  
  @Override
  public FlJavaSpec visitWhenThen(WhenThen node) {
    // TODO Auto-generated method stub
    return super.visitWhenThen(node);
  }
  @Override
  public FlJavaSpec visitWhenThenPointer(WhenThenPointer node) {
    // TODO Auto-generated method stub
    return super.visitWhenThenPointer(node);
  }
}
