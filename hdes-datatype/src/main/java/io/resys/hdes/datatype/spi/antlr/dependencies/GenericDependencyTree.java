package io.resys.hdes.datatype.spi.antlr.dependencies;

/*-
 * #%L
 * hdes-datatype
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
import java.util.Optional;

import io.resys.hdes.datatype.api.DataType;
import io.resys.hdes.datatype.api.DataType.ValueType;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.DependencyTree;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.Literal;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.MethodInvocation;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.Root;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.TypeName;

public class GenericDependencyTree implements DependencyTree {

  private final Root node;
  private final List<DataType> dependencies;
  private final List<Literal> literals;
  
  public GenericDependencyTree(Root node, List<DataType> dependencies, List<Literal> literals) {
    super();
    this.node = node;
    this.dependencies = dependencies;
    this.literals = literals;
  }
  
  @Override
  public Root getNode() {
    return node;
  }

  @Override
  public Optional<DataType> get(MethodInvocation invocation) {
    String typeName = Optional.ofNullable(invocation.getTypeName()).map(t -> t.getValue() + ".").orElse("");
    String fullName = typeName + invocation.getName().getValue(); 
    return dependencies.stream()
        .filter(d -> d.getValueType() == ValueType.METHOD_INVOCATION)
        .filter(d -> d.getName().equals(fullName))
        .findFirst();
  }
  
  @Override
  public Optional<DataType> get(TypeName typeName) {
    String fullName = typeName.getValue(); 
    return dependencies.stream()
        .filter(d -> d.getValueType() != ValueType.METHOD_INVOCATION)
        .filter(d -> d.getName().equals(fullName))
        .findFirst();
  }
  
  @Override
  public List<DataType> getDependencies() {
    return dependencies;
  }

  @Override
  public List<Literal> getLiterals() {
    return literals;
  }
}
