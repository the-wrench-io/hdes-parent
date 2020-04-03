package io.resys.hdes.ast.api;

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
