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

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import io.resys.hdes.ast.api.nodes.ExpressionNode.ExpressionBody;
import io.resys.hdes.ast.api.nodes.FlowNode.EndPointer;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowBody;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowTaskNode;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowTaskPointer;
import io.resys.hdes.ast.api.nodes.FlowNode.Mapping;
import io.resys.hdes.ast.api.nodes.FlowNode.MappingValue;
import io.resys.hdes.ast.api.nodes.FlowNode.TaskRef;
import io.resys.hdes.ast.api.nodes.FlowNode.ThenPointer;
import io.resys.hdes.ast.api.nodes.FlowNode.WhenThen;
import io.resys.hdes.ast.api.nodes.FlowNode.WhenThenPointer;
import io.resys.hdes.compiler.api.Flow.ExecutionStatusType;
import io.resys.hdes.compiler.api.Flow.FlowExecutionLog;
import io.resys.hdes.compiler.api.HdesCompilerException;
import io.resys.hdes.compiler.api.HdesWhen;
import io.resys.hdes.compiler.api.ImmutableFlowExecutionLog;
import io.resys.hdes.compiler.spi.NamingContext;
import io.resys.hdes.compiler.spi.java.FlowUtil;
import io.resys.hdes.compiler.spi.java.JavaSpecUtil;
import io.resys.hdes.compiler.spi.java.visitors.FlJavaSpec.FlCodeSpec;
import io.resys.hdes.compiler.spi.java.visitors.FlJavaSpec.FlTaskVisitSpec;
import io.resys.hdes.compiler.spi.java.visitors.FlJavaSpec.FlWhenThenSpec;

public class FlAstNodeVisitorJavaGen extends FlAstNodeVisitorTemplate<FlJavaSpec, TypeSpec> {
  
  private final NamingContext naming;
  private final TypeNameRef typeNameRef = (v) -> {
    String value = v.getValue();
    if(value.contains(".")) {
      int first = value.indexOf(".");
      return "after." + 
          JavaSpecUtil.getMethodCall(value.substring(0, first)) + 
          ".get().getOutput().get()." + 
          JavaSpecUtil.getMethodCall(value.substring(first + 1));
    }
    return "after.getInput()." + JavaSpecUtil.getMethodCall(value);
  };
  
  private ClassName flowState;
  private ClassName flowOutput;
  
  public FlAstNodeVisitorJavaGen(NamingContext naming) {
    super();
    this.naming = naming;
  }
  
  @Override
  public TypeSpec visitBody(FlowBody node) {
    this.flowState = naming.fl().state(node);
    this.flowOutput = naming.fl().output(node);
    
    TypeSpec.Builder flowBuilder = TypeSpec.classBuilder(naming.fl().impl(node))
        .addModifiers(Modifier.PUBLIC)
        .addSuperinterface(naming.fl().interfaze(node));

    FlTaskVisitSpec taskImpl = node.getTask().map(n -> visitTask(n)).orElseGet(() -> 
      ImmutableFlTaskVisitSpec.builder().value(CodeBlock.builder().add("no tasks nothing to do").build()).build());

    MethodSpec applyMethod = MethodSpec.methodBuilder("apply")
      .addModifiers(Modifier.PUBLIC)
      .addParameter(ParameterSpec.builder(naming.fl().input(node), "input").build())
      .returns(flowState)
      .addStatement("$T after = start(input).build()", flowState)
      .addCode(taskImpl.getValue())
      .build();
 
    return flowBuilder
        .addAnnotation(AnnotationSpec.builder(javax.annotation.Generated.class).addMember("value", "$S", FlAstNodeVisitorJavaGen.class.getCanonicalName()).build())
        .addMethod(MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addParameter(ParameterSpec.builder(HdesWhen.class, "when").build())
            .addStatement("this.when = when")
            .build())
        .addField(FieldSpec.builder(HdesWhen.class, "when", Modifier.PRIVATE, Modifier.FINAL).build())
        
        .addMethod(applyMethod)
        .addMethod(startMethod(node))
        .addMethod(endMethod(node))
        .addMethods(taskImpl.getValues())
        .build();
  }
  
  protected MethodSpec startMethod(FlowBody node) {
    return MethodSpec.methodBuilder("start")
      .addModifiers(Modifier.PROTECTED)
      .addParameter(ParameterSpec.builder(naming.fl().input(node), "input").build())
      .returns(naming.immutableBuilder(flowState))
      .addCode(CodeBlock.builder()
          .add("return $T.builder()", naming.immutable(flowState))
          .add("\r\n  ").add(".id($S)", node.getId())
          .add("\r\n  ").add(".type($T.RUNNING)", ExecutionStatusType.class)
          .add("\r\n  ").add(".input(input)")
          .add("\r\n  ").add(".log($L)", CodeBlock.builder()
          .add("$T.builder()", ImmutableFlowExecutionLog.class)
          .add("\r\n    ").add(".id($S)", "start")
          .add("\r\n    ").add(".start(System.currentTimeMillis())")
          .add("\r\n    ").add(".build()").build())
          .addStatement("")
          .build())
      .build();
  }

  protected MethodSpec endMethod(FlowBody node) {
    // End method
    return MethodSpec.methodBuilder("end")
        .addModifiers(Modifier.PROTECTED)
        .addParameter(ParameterSpec.builder(flowState, "currentState").build())
        .returns(naming.immutableBuilder(flowState))
        .addStatement("long end = System.currentTimeMillis()")
        .addStatement(CodeBlock.builder()
            .add("return $T.builder()", naming.immutable(flowState))
            .add("\r\n  ").add(".from(currentState)")
            .add("\r\n  ").add(".log($L)", CodeBlock.builder()
                .add("$T.builder()", ImmutableFlowExecutionLog.class)
                .add("\r\n    ").add(".id($S)", "end")
                .add("\r\n    ").add(".duration(end - $T.getStart(currentState.getLog()))", FlowUtil.class)
                .add("\r\n    ").add(".end(end)")
                .add("\r\n    ").add(".parent(currentState.getLog())")
                .add("\r\n    ").add(".build()").build())
            .build()).build();
  }
  
  @Override
  public FlTaskVisitSpec visitTask(FlowTaskNode node) {
    CodeBlock.Builder codeblock = CodeBlock.builder();
    
    // visit method
    if(node.getRef().isPresent()) {
      codeblock.add(visitTaskRef(node).getValue());
    } else {
      codeblock.addStatement("$T after = before", flowState);
    }
    
    // next
    String methodName = "visit" + node.getId();
    List<MethodSpec> children = new ArrayList<>();    
    FlTaskVisitSpec next = visitTaskPointer(node.getNext());
    codeblock.add(next.getValue());
    for(MethodSpec method : next.getValues()) {
      if(!children.stream().filter(m -> m.name.equals(method.name)).findFirst().isPresent()) {
        children.add(method);          
      }
    }
    
    return ImmutableFlTaskVisitSpec.builder()
        .value(CodeBlock.builder().addStatement("return $L(after)", methodName).build())
        .addValues(MethodSpec
          .methodBuilder(methodName)
          .addModifiers(Modifier.PRIVATE)
          .addParameter(ParameterSpec.builder(flowState, "before").build())
          .addCode(codeblock.build())
          .returns(flowState).build())
        .addAllValues(children).build();
  }
  
  @Override
  public FlCodeSpec visitTaskRef(FlowTaskNode parent) {
    return ImmutableFlCodeSpec.builder().value(CodeBlock.builder()
      // Create mapping in/out from the task
      .addStatement("long start = System.currentTimeMillis()")
      .add(visitMapping(parent).getValue())
      .addStatement("long end = System.currentTimeMillis()")
      
      // build execution log
      .add("\r\n")
      .add("$T log = $T.builder()", FlowExecutionLog.class, ImmutableFlowExecutionLog.class)
      .add("\r\n  ").add(".id($S)", parent.getId())
      .add("\r\n  ").add(".parent(before.getLog())")
      .add("\r\n  ").add(".start(start)").add(".end(end)")
      .add("\r\n  ").add(".duration(end - start)")
      .add("\r\n  ").addStatement(".build()")
    
      // create new state
      .add("\r\n")
      .add("$T after = $T.builder()", flowState, naming.immutable(flowState))
      .add("\r\n  ").add(".from(before)")
      .add("\r\n  ").add(".input(input)")
      .add("\r\n  ").add(".output(output)")
      .add("\r\n  ").add(".log(log)")
      .add("\r\n  ").addStatement(".build()")
      .add("\r\n").build()
    ).build(); 
  }
  
  @Override
  public FlCodeSpec visitMapping(FlowTaskNode node) {
    TaskRef ref = node.getRef().get();
    ClassName input = naming.fl().refInput(node.getRef().get());
    
    CodeBlock.Builder codeBlock = CodeBlock.builder()
        .add("$T input = $T.builder()", input, naming.immutable(input));
    for(Mapping mapping : ref.getMapping()) {
      //String right = JavaSpecUtil.getMethodCall(mapping.getRight());
      //codeBlock.add("\r\n  ").add(".$L(before.$L)", mapping.getLeft(), right);
    }
    
    codeBlock
    .addStatement(".build()")
    .add("$T output = ", naming.fl().refOutput(node.getRef().get()));
    
    switch (ref.getType()) {
    case DECISION_TABLE: codeBlock.addStatement("$L.apply(input)", naming.fl().refMethod(ref)); break;
    case FLOW_TASK: codeBlock.addStatement("$L.apply(input)", naming.fl().refMethod(ref)); break;
    case MANUAL_TASK: codeBlock.addStatement("$L().apply(input)", naming.fl().refMethod(ref)); break;
    case SERVICE_TASK: codeBlock.addStatement("$L().apply(input)", naming.fl().refMethod(ref)); break;
    default: throw new HdesCompilerException(HdesCompilerException.builder().unknownFlTaskRef(ref));
    }
    return ImmutableFlCodeSpec.builder().value(codeBlock.build()).build();
  }
  
  @Override
  public FlTaskVisitSpec visitTaskPointer(FlowTaskPointer node) {
    // if / else
    if(node instanceof WhenThenPointer) {
      WhenThenPointer pointer = (WhenThenPointer) node;
      return visitWhenThenPointer(pointer);
    }
    
    // next
    if(node instanceof ThenPointer) {
      ThenPointer then = (ThenPointer) node;
      return visitThenPointer(then);
    } 
    
    // end
    if(node instanceof EndPointer) {
      EndPointer then = (EndPointer) node;
      return visitEndPointer(then);
    } 
    throw new HdesCompilerException(HdesCompilerException.builder().unknownFlTaskPointer(node));
  
  }
  
  @Override
  public FlCodeSpec visitWhen(ExpressionBody node) {
    return ImmutableFlCodeSpec.builder()
        .value(new EnAstNodeJavaCodeBlock(typeNameRef).visitExpressionBody(node))
        .build();
  }
  
  @Override
  public FlTaskVisitSpec visitThenPointer(ThenPointer node) {
    if(node.getTask().isPresent()) {
      return visitTask(node.getTask().get());
    }
    return null;
  }
  
  @Override
  public FlWhenThenSpec visitWhenThen(WhenThen node) {
    FlTaskVisitSpec spec = visitTaskPointer(node.getThen());
    
    return ImmutableFlWhenThenSpec.builder()
        .when(node.getWhen().map(w -> visitWhen(w).getValue()))
        .then(spec)
        .build();
  }
  
  @Override
  public FlTaskVisitSpec visitEndPointer(EndPointer node) {
    
    CodeBlock.Builder codeBlock = CodeBlock.builder()
        .add("$T end = $T.builder()", flowOutput, naming.immutable(flowOutput));
    for(Mapping mapping : node.getValues()) {
      //String right = JavaSpecUtil.getMethodCall(mapping.getRight());
      //codeBlock.add("\r\n  ").add(".$L(after.$L)", mapping.getLeft(), right);
    }
    codeBlock.add("\r\n")
    .addStatement("  .build()")
    .addStatement("return end(after).output(end).build()");

    return ImmutableFlTaskVisitSpec.builder().value(codeBlock.build()).build();
  }

  @Override
  public FlTaskVisitSpec visitWhenThenPointer(WhenThenPointer pointer) {
    List<MethodSpec> methods = new ArrayList<>();
    CodeBlock.Builder codeBlock = CodeBlock.builder();
    boolean first = true;
    boolean last = false;

    for(WhenThen whenThen : pointer.getValues()) {
      if(last) {
        throw new HdesCompilerException(HdesCompilerException.builder().wildcardUnknownFlTaskWhenThen(pointer));
      }

      FlWhenThenSpec spec = visitWhenThen(whenThen);
      if(first && spec.getWhen().isPresent()) {
        codeBlock.beginControlFlow("if($L)", spec.getWhen().get());
        first = false;
      } else if(spec.getWhen().isPresent()) {
        codeBlock.beginControlFlow("else if($L)", spec.getWhen().get());
      } else {
        codeBlock.beginControlFlow("else");
        last = true;
      }
      codeBlock.add(spec.getThen().getValue()).endControlFlow();
      methods.addAll(spec.getThen().getValues());
    }
    return ImmutableFlTaskVisitSpec.builder().value(codeBlock.build()).addAllValues(methods).build();
  }
  
  @Override
  public FlJavaSpec visitMappingValue(MappingValue node) {
    // TODO Auto-generated method stub
    return super.visitMappingValue(node);
  }
}
