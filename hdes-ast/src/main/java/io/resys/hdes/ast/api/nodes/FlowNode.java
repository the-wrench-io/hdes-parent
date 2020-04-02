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

public interface FlowNode extends AstNode {

  enum RefTaskType { MANUAL_TASK, DECISION_TABLE, SERVICE_TASK }
  
  interface TaskBody extends FlowNode {}
  
  @Value.Immutable
  interface FlowBody extends FlowNode {
    String getId();
    String getDescription();
    FlowInputs getInputs();
    Optional<Task> getTask();
  }

  @Value.Immutable
  interface FlowInputs extends FlowNode {
    List<FlowInput> getValues();
  }
  
  interface FlowInput extends FlowNode {
    Boolean getRequired();    
    String getName();
  }
  
  @Value.Immutable
  interface ObjectFlowInput extends FlowInput {
    List<FlowInput> getValues();
  }
  
  @Value.Immutable
  interface ArrayFlowInput extends FlowInput {
    FlowInput getValue();
  }
  
  @Value.Immutable
  interface ScalarFlowInput extends FlowInput {
    String getDebugValue();
    ScalarType getType();
  }
  
  @Value.Immutable
  interface Task extends FlowNode {
    String getId();
    TaskBody getBody();
  }
  
  /**
   * Task bodies based on types
   */
  @Value.Immutable
  interface SwitchBody extends TaskBody {
    List<WhenThen> getValue();
  }
  @Value.Immutable
  interface EmptyTaskBody extends TaskBody {
    Then getThen();
  }
  @Value.Immutable
  interface RefTaskBody extends TaskBody {
    RefNode getRef();
    List<Mapping> getMapping();
    Then getThen();
  }
  
  @Value.Immutable
  interface EndTaskBody extends TaskBody {
    List<Mapping> getMapping();
  }
  
  
  @Value.Immutable
  interface WhenThen extends FlowNode {
    ExpressionNode getWhen();
    Then getThen();
  }
  
  @Value.Immutable
  interface Then extends FlowNode {
    Task getTask();
  }
  
  @Value.Immutable
  interface Mapping extends FlowNode {
    String getLeft();
    String getRight();
  }

  @Value.Immutable
  interface RefNode extends FlowNode {
    RefTaskType getType();
    String getName();
    AstNode getValue();
  }
}
