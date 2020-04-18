package io.resys.hdes.compiler.spi.java.visitors;

import java.util.ArrayList;
import java.util.List;

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
import io.resys.hdes.ast.api.nodes.FlowNode.FlowTaskNode;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowTaskPointer;
import io.resys.hdes.ast.api.nodes.FlowNode.Mapping;
import io.resys.hdes.ast.api.nodes.FlowNode.TaskRef;
import io.resys.hdes.ast.api.nodes.FlowNode.ThenPointer;
import io.resys.hdes.ast.api.nodes.FlowNode.When;
import io.resys.hdes.ast.api.nodes.FlowNode.WhenThen;
import io.resys.hdes.ast.api.nodes.FlowNode.WhenThenPointer;
import io.resys.hdes.compiler.spi.NamingContext;
import io.resys.hdes.compiler.spi.java.visitors.FlJavaSpec.FlTaskImplSpec;

public class FlAstNodeVisitorJavaGen extends FlAstNodeVisitorTemplate<FlJavaSpec, TypeSpec> {
  
  private final AstEnvir envir;
  private final NamingContext naming;
  private FlowBody body;
  private ClassName flowState;
  
  public FlAstNodeVisitorJavaGen(AstEnvir envir, NamingContext naming) {
    super();
    this.envir = envir;
    this.naming = naming;
  }
  
  @Override
  public TypeSpec visitFlowBody(FlowBody node) {
    this.body = node;
    this.flowState = naming.fl().state(node);
    
    TypeSpec.Builder flowBuilder = TypeSpec.classBuilder(naming.fl().impl(node))
        .addModifiers(Modifier.PUBLIC)
        .addSuperinterface(ClassName.get("", node.getId()));

    FlTaskImplSpec taskImpl = node.getTask().map(n -> visitFlowTask(n)).orElseGet(() ->
      ImmutableFlTaskImplSpec.builder().value(CodeBlock.builder().add("// not tasks described ").build()).build()
    );
    
    MethodSpec applyMethod = MethodSpec.methodBuilder("apply")
        .addModifiers(Modifier.PUBLIC)
        .addParameter(ParameterSpec.builder(naming.fl().input(node), "input").build())
        .returns(flowState)
        .addStatement(visitInit(node))
        .addCode(CodeBlock.builder()
            .add(taskImpl.getValue())
            .build())
        .addStatement(CodeBlock.builder().add("return currentState").build())
        .build();
 
    return flowBuilder
        .addMethod(applyMethod)
        .addMethods(taskImpl.getChildren())
        .build();
  }
  
  private CodeBlock visitInit(FlowBody node) {
    return CodeBlock.builder()
        .add("$T currentState = Immutable$T.builder().input(input).build()", flowState, flowState)
        .build();
  }
  
  @Override
  public FlTaskImplSpec visitFlowTask(FlowTaskNode node) {
    List<MethodSpec> children = new ArrayList<>();
    CodeBlock.Builder codeblock = CodeBlock.builder();
    ClassName inputType = naming.fl().taskInput(body, node);
    ClassName outputType = naming.fl().taskOutput(body, node);
    
    // visit method
    if(node.getRef().isPresent()) {
      String visitMethodName = JavaNaming.flVisitTask(node.getId());
      MethodSpec.Builder visitBuilder = MethodSpec
          .methodBuilder(visitMethodName)
          .addModifiers(Modifier.PRIVATE)
          .addParameter(ParameterSpec.builder(flowState, "currentState").build())
          .returns(flowState);
      children.add(visitBuilder.build());
      
      codeblock.addStatement("currentState = $L(currentState)", visitMethodName);
      
      // create input
      
      // call ref
      
      // create output
    }
    
    // tasks body
    if(node.getNext().isPresent()) {
      FlTaskImplSpec next = visitFlowTaskPointer(node.getNext().get());
      codeblock.add(next.getValue());
      children.addAll(next.getChildren());
    }
    
    return ImmutableFlTaskImplSpec.builder()
        .value(codeblock.build())
        .addAllChildren(children)
        .build();
  }
  
  @Override
  public FlTaskImplSpec visitTaskRef(TaskRef node) {
    // TODO Auto-generated method stub
    
    //output = factory.dt().myDt().apply(input)
    //output = factory.st().myServiceTask().apply(input)
    //output = factory.fl().myFlow().apply(input)
    
    return ImmutableFlTaskImplSpec.builder().build();
  }
  
  @Override
  public FlTaskImplSpec visitFlowTaskPointer(FlowTaskPointer node) {
    // if / else
    
    
    // next
    if(node instanceof ThenPointer) {
      ThenPointer then = (ThenPointer) node;
      if(then.getTask().isPresent()) {
        return visitFlowTask(then.getTask().get());
      }
    }
    
    return ImmutableFlTaskImplSpec.builder().build();
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
