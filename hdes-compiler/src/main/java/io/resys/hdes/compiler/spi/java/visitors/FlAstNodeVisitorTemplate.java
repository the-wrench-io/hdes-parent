package io.resys.hdes.compiler.spi.java.visitors;

import io.resys.hdes.ast.api.nodes.AstNode.ArrayInputNode;
import io.resys.hdes.ast.api.nodes.AstNode.DateConversion;
import io.resys.hdes.ast.api.nodes.AstNode.DateTimeConversion;
import io.resys.hdes.ast.api.nodes.AstNode.DecimalConversion;
import io.resys.hdes.ast.api.nodes.AstNode.Literal;
import io.resys.hdes.ast.api.nodes.AstNode.ObjectInputNode;
import io.resys.hdes.ast.api.nodes.AstNode.ScalarInputNode;
import io.resys.hdes.ast.api.nodes.AstNode.TimeConversion;
import io.resys.hdes.ast.api.nodes.AstNode.TypeName;
import io.resys.hdes.ast.api.nodes.AstNodeVisitor.FlowAstNodeVisitor;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowBody;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowInputs;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowReturnType;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowTask;
import io.resys.hdes.ast.api.nodes.FlowNode.Mapping;
import io.resys.hdes.ast.api.nodes.FlowNode.TaskRef;
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
  public T visitObjectInputNode(ObjectInputNode node) {
    
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitArrayInputNode(ArrayInputNode node) {
    
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitScalarInputNode(ScalarInputNode node) {
    
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public R visitFlowBody(FlowBody node) {
    
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitFlowReturnType(FlowReturnType node) {
    
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitFlowInputs(FlowInputs node) {
    
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public T visitFlowTask(FlowTask node) {
    
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
  public T visitTaskRef(TaskRef node) {
    
    throw new IllegalArgumentException("Not implemented");
  }

}
