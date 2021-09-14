package io.resys.wrench.assets.flow.api;

/*-
 * #%L
 * wrench-assets-flow
 * %%
 * Copyright (C) 2016 - 2019 Copyright 2016 ReSys OÜ
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
import java.util.Map;
import java.util.function.Consumer;

import io.resys.wrench.assets.datatype.api.AstCommandType;
import io.resys.wrench.assets.flow.api.model.FlowAst.FlowCommandMessage;

public interface FlowAstFactory {

  NodeBuilder create(Consumer<FlowCommandMessage> messageConsumer);

  interface NodeBuilder {
    NodeBuilder add(int line, String value);
    NodeBuilder set(int line, String value);
    NodeBuilder delete(int line);
    NodeBuilder delete(int from, int to);
    NodeFlow build();
  }

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
}
