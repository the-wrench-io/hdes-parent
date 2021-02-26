package io.resys.hdes.ast.api.nodes;

import java.util.List;

import javax.annotation.Nullable;

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

import org.immutables.value.Value;

public interface InvocationNode extends HdesNode {
  
  enum StaticMethodType { SUM, AVG, MIN, MAX, IN }
  
  interface Placeholder extends InvocationNode {}

  // Flat placeholder starts with '_'
  @Value.Immutable
  interface NamedPlaceholder extends Placeholder {
    String getValue();
    @Nullable @Value.Default
    default HdesNodeType getNodeType() { return HdesNodeType.INVOCATION_NAMED_PLACEHOLDER; }
  }

  @Value.Immutable
  interface EmptyPlaceholder extends Placeholder {
    @Nullable @Value.Default
    default HdesNodeType getNodeType() { return HdesNodeType.INVOCATION_EMPTY_PLACEHOLDER; }
  }
  
  // Flat one name thats not a placeholder or nested name
  @Value.Immutable
  interface SimpleInvocation extends InvocationNode {
    String getValue();
    @Nullable @Value.Default
    default HdesNodeType getNodeType() { return HdesNodeType.INVOCATION_SIMPLE; }
  }
  
  @Value.Immutable
  interface NestedInvocation extends InvocationNode {
    // path
    InvocationNode getPath();
    
    // last value on nested invocation
    InvocationNode getValue();
    @Nullable @Value.Default
    default HdesNodeType getNodeType() { return HdesNodeType.INVOCATION_NESTED; }
  }
  
  @Value.Immutable
  interface SortBy extends FlowNode {
    List<SortByDef> getValues();
    @Nullable @Value.Default
    default HdesNodeType getNodeType() { return HdesNodeType.INVOCATION_SORTBY; }
  }

  @Value.Immutable
  interface SortByDef extends FlowNode {
    InvocationNode getName();
    Boolean getAsc();
    @Nullable @Value.Default
    default HdesNodeType getNodeType() { return HdesNodeType.INVOCATION_SORTBY_DEF; }
  }
}
