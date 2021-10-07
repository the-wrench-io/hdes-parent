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
public interface AstFlow extends AstBody, Serializable {
  NodeFlow getSrc();
  List<FlowAstCommandMessage> getMessages();
  List<FlowAstAutocomplete> getAutocomplete();

  @Value.Immutable
  interface FlowAstAutocomplete extends Serializable {
    String getId();
    List<FlowAstCommandRange> getRange();
    List<String> getValue();
  }

  @Value.Immutable
  interface FlowAstCommandMessage extends Serializable {
    int getLine();
    String getValue();
    FlowCommandMessageType getType();
    @Nullable
    FlowAstCommandRange getRange();
  }

  @Value.Immutable
  interface FlowAstCommandRange extends Serializable {
    int getStart();
    int getEnd();
    @Nullable
    Integer getColumn();
    @Nullable
    Boolean getInsert();
  }

  enum FlowCommandMessageType { ERROR, WARNING }
  
  //v.name(), null, v.name()
  //public ImmutableNodeInputType(String name, String ref, String value) {
  
  @Value.Immutable
  interface FlowAstInputType extends Serializable {
    String getName();
    String getValue();
    @Nullable
    String getRef();
  }

  interface NodeFlow extends FlowAstNode {
    FlowAstNode getId();
    FlowAstNode getDescription();
    Collection<FlowAstInputType> getTypes();
    Map<String, FlowAstInput> getInputs();
    Map<String, FlowAstTask> getTasks();
  }

  interface FlowAstTask extends FlowAstNode {
    FlowAstNode getId();
    int getOrder();
    FlowAstNode getThen();
    FlowAstRef getRef();
    FlowAstRef getUserTask();
    FlowAstRef getDecisionTable();
    FlowAstRef getService();
    Map<String, FlowAstSwitch> getSwitch();
  }

  interface FlowAstRef extends FlowAstNode {
    FlowAstNode getRef();
    FlowAstNode getCollection();
    FlowAstNode getInputsNode();
    Map<String, FlowAstNode> getInputs();
  }

  interface FlowAstSwitch extends FlowAstNode {
    int getOrder();
    FlowAstNode getWhen();
    FlowAstNode getThen();
  }

  interface FlowAstInput extends FlowAstNode {
    FlowAstNode getRequired();
    FlowAstNode getType();
    FlowAstNode getDebugValue();
  }

  interface FlowAstNode extends Serializable, Comparable<FlowAstNode> {
    FlowAstNode getParent();
    String getKeyword();
    Map<String, FlowAstNode> getChildren();
    FlowAstNode get(String name);
    String getValue();
    AstChangeset getSource();
    boolean hasNonNull(String name);
    int getStart();
    int getEnd();
  }
  
  interface NodeFlowVisitor {
    void visit(NodeFlow node, ImmutableAstFlow.Builder nodesBuilder);
  }

}
