package io.resys.hdes.ast.api;

import java.util.List;
import java.util.function.Consumer;

import org.immutables.value.Value;

import io.resys.hdes.ast.api.nodes.AstNode;
import io.resys.hdes.ast.api.nodes.AstNode.ScalarType;
import io.resys.hdes.ast.api.types.ServiceTask;

public interface HdesEnvir {
  Source getSource();
  List<AstNode> getNodes();
  
  @Value.Immutable
  interface Source {
    List<String> getFlows();
    List<String> getExpressions();
    List<String> getDecisionTables();
    List<String> getManualTasks();
  }
  
  interface SourceBuilder<R> {
    R flow(String src);
    R expression(String src);
    R expression(String src, Consumer<DependencyBuilder> consumer);
    R manualTask(String src);
    R serviceTask(Class<ServiceTask<?, ?, ?>> src);
    R decisionTable(String src);
  }
  
  interface DependencyBuilder {
    DependencyNodeBuilder<DependencyBuilder> addType();
    DependencyNodeBuilder<DependencyBuilder> addMethod();
  }
  
  interface MethodDependencyBuilder<T> {
    DependencyNodeBuilder<T> name(String name);
    T returnType(Consumer<ObjectDependencyBuilder> consumer);
    T parameters(Consumer<ArrayDependencyBuilder> consume);
  }
  
  interface DependencyNodeBuilder<T> {
    DependencyNodeBuilder<T> name(String name);
    T scalar(ScalarType type);
    T object(Consumer<DependencyBuilder> consumer);
    T array(Consumer<DependencyBuilder> consume);
  }
  
  interface ObjectDependencyBuilder {
    
  }
  
  interface ArrayDependencyBuilder {
    
  }
  
  
  interface HdesEnvirBuilder {
    HdesEnvirBuilder from(HdesEnvir envir);
    SourceBuilder<HdesEnvirBuilder> add();
    HdesEnvirBuilder strict();
    HdesEnvir build();
  }
}
