package io.resys.hdes.client.api.ast;

/*-
 * #%L
 * wrench-assets-flow
 * %%
 * Copyright (C) 2016 - 2018 Copyright 2016 ReSys OÜ
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

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.immutables.value.Value;


@Value.Immutable
public interface FlowAstType extends AstType, Serializable {
  Node getSrc();
  List<FlowCommandMessage> getMessages();
  List<FlowAutocomplete> getAutocomplete();

  @Value.Immutable
  interface FlowAutocomplete extends Serializable {
    String getId();
    List<FlowCommandRange> getRange();
    List<String> getValue();
  }

  @Value.Immutable
  interface FlowCommandMessage extends Serializable {
    int getLine();
    String getValue();
    FlowCommandMessageType getType();
    @Nullable
    FlowCommandRange getRange();
  }

  @Value.Immutable
  interface FlowCommandRange extends Serializable {
    int getStart();
    int getEnd();
    @Nullable
    Integer getColumn();
    @Nullable
    Boolean getInsert();
  }

  enum FlowCommandMessageType { ERROR, WARNING }
  
  interface NodeInputType extends Serializable {
    String getName();
    String getRef();
    String getValue();
  }

  interface NodeFlow extends Node {
    Node getId();
    Node getDescription();
    Collection<NodeInputType> getTypes();
    Map<String, NodeInput> getInputs();
    Map<String, NodeTask> getTasks();
  }

  interface NodeTask extends Node {
    Node getId();
    int getOrder();
    Node getThen();
    NodeRef getRef();
    NodeRef getUserTask();
    NodeRef getDecisionTable();
    NodeRef getService();
    Map<String, NodeSwitch> getSwitch();
  }

  interface NodeRef extends Node {
    Node getRef();
    Node getCollection();
    Node getInputsNode();
    Map<String, Node> getInputs();
  }

  interface NodeSwitch extends Node {
    int getOrder();
    Node getWhen();
    Node getThen();
  }

  interface NodeInput extends Node {
    Node getRequired();
    Node getType();
    Node getDebugValue();
  }

  interface Node extends Serializable, Comparable<Node> {
    Node getParent();
    String getKeyword();
    Map<String, Node> getChildren();
    Node get(String name);
    String getValue();
    NodeSource getSource();
    boolean hasNonNull(String name);
    int getStart();
    int getEnd();
  }

  interface NodeSource extends Serializable {
    int getLine();
    String getValue();
    Collection<AstCommandType> getCommands();
  }
  
  interface NodeFlowVisitor {
    void visit(NodeFlow node, ImmutableFlowAstType.Builder nodesBuilder);
  }

}
