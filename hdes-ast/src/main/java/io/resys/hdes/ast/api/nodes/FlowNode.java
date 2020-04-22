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
import java.util.Optional;

import org.immutables.value.Value;

import io.resys.hdes.ast.api.nodes.ExpressionNode.ExpressionBody;

public interface FlowNode extends AstNode {

  enum RefTaskType { FLOW_TASK, MANUAL_TASK, DECISION_TABLE, SERVICE_TASK }
  
  interface FlowTaskPointer extends FlowNode {}
  
  @Value.Immutable
  interface FlowBody extends FlowNode, BodyNode {
    String getDescription();
    FlowInputs getInputs();
    FlowOutputs getOutputs(); 
    List<FlowTaskNode> getUnreachableTasks();
    Optional<FlowTaskNode> getTask();
  }

  @Value.Immutable
  interface FlowInputs extends FlowNode {
    List<TypeDefNode> getValues();
  }
  
  @Value.Immutable
  interface FlowOutputs extends FlowNode {
    List<TypeDefNode> getValues();
  }
  
  @Value.Immutable
  interface FlowTaskNode extends FlowNode {
    String getId();
    Optional<FlowTaskPointer> getNext();
    Optional<TaskRef> getRef();
  }
  
  @Value.Immutable
  interface WhenThenPointer extends FlowTaskPointer {
    List<WhenThen> getValues();
  }
  
  @Value.Immutable
  interface ThenPointer extends FlowTaskPointer {
    String getName();
    // Only possible in invalid tree
    Optional<FlowTaskNode> getTask();
  }

  @Value.Immutable
  interface EndPointer extends FlowTaskPointer {
    String getName();
    List<Mapping> getValues();
  }
  
  @Value.Immutable
  interface WhenThen extends FlowNode {
    Optional<ExpressionBody> getWhen();
    FlowTaskPointer getThen();
  }
  
  
  @Value.Immutable
  interface Mapping extends FlowNode {
    String getLeft();
    String getRight();
  }

  @Value.Immutable
  interface TaskRef extends FlowNode {
    RefTaskType getType();
    String getValue();
    List<Mapping> getMapping();
  }
}
