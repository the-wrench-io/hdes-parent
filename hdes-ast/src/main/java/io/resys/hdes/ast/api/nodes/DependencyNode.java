package io.resys.hdes.ast.api.nodes;

import java.util.List;

import org.immutables.value.Value;

public interface DependencyNode extends AstNode {
  
  interface DependencyType extends DependencyNode {}
  
  @Value.Immutable
  interface ReferenceDependency extends DependencyNode {
    String getName();
    DependencyType getValue();
  }
  
  @Value.Immutable
  interface MethodDependency extends DependencyNode {
    String getName();
    DependencyType getReturnType();
    ObjectDependency getParameters();
  }

  @Value.Immutable
  interface ServiceDependency extends DependencyNode {
    ReferenceDependency getType();
    MethodDependency getMethod();
  }
  
  @Value.Immutable
  interface ObjectDependency extends DependencyNode {
    List<NamedDependency> getValues();
  }

  @Value.Immutable
  interface ArrayDependency extends DependencyNode {
    DependencyType getValue();
  }
  
  @Value.Immutable
  interface ScalarDependency extends DependencyType {
    ScalarType getType();
  }
  
  @Value.Immutable
  interface NamedDependency extends DependencyType {
    String getName();
    DependencyType getValue();
  }
}
