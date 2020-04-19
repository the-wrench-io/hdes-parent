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

import io.resys.hdes.ast.api.nodes.AstNode.ArrayTypeDefNode;
import io.resys.hdes.ast.api.nodes.AstNode.DateConversion;
import io.resys.hdes.ast.api.nodes.AstNode.DateTimeConversion;
import io.resys.hdes.ast.api.nodes.AstNode.DecimalConversion;
import io.resys.hdes.ast.api.nodes.AstNode.Literal;
import io.resys.hdes.ast.api.nodes.AstNode.ObjectTypeDefNode;
import io.resys.hdes.ast.api.nodes.AstNode.ScalarTypeDefNode;
import io.resys.hdes.ast.api.nodes.AstNode.TimeConversion;
import io.resys.hdes.ast.api.nodes.AstNode.TypeName;
import io.resys.hdes.ast.api.nodes.AstNodeVisitor.FlowAstNodeVisitor;
import io.resys.hdes.ast.api.nodes.FlowNode.EndPointer;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowBody;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowInputs;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowTaskNode;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowTaskPointer;
import io.resys.hdes.ast.api.nodes.FlowNode.Mapping;
import io.resys.hdes.ast.api.nodes.FlowNode.ThenPointer;
import io.resys.hdes.ast.api.nodes.FlowNode.When;
import io.resys.hdes.ast.api.nodes.FlowNode.WhenThen;
import io.resys.hdes.ast.api.nodes.FlowNode.WhenThenPointer;

public class FlAstNodeVisitorTemplate<T, R> implements FlowAstNodeVisitor<T, R> {

  @Override
  public T visitTypeName(TypeName node) {
    
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitLiteral(Literal node) {
    
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitDateConversion(DateConversion node) {
    
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitDateTimeConversion(DateTimeConversion node) {
    
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitTimeConversion(TimeConversion node) {
    
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitDecimalConversion(DecimalConversion node) {
    
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitObjectInputNode(ObjectTypeDefNode node) {
    
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitArrayInputNode(ArrayTypeDefNode node) {
    
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitScalarInputNode(ScalarTypeDefNode node) {
    
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public R visitFlowBody(FlowBody node) {
    
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitFlowInputs(FlowInputs node) {
    
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitFlowTask(FlowTaskNode node) {
    
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitWhenThenPointer(WhenThenPointer node) {
    
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitThenPointer(ThenPointer node) {
    
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitWhenThen(WhenThen node) {
    
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitWhen(When node) {
    
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitMapping(Mapping node) {
    
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitTaskRef(FlowTaskNode node) {
    
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitFlowTaskPointer(FlowTaskPointer node) {
    
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitEndPointer(EndPointer node) {
    
    throw new IllegalArgumentException("Not implemented");
  }

}
