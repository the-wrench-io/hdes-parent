package io.resys.hdes.ast.api.nodes;

/*-
 * #%L
 * hdes-ast
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
