package io.resys.wrench.assets.flow.api.model;

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
import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import io.resys.wrench.assets.datatype.api.AstCommandType;
import io.resys.wrench.assets.datatype.api.AstType;
import io.resys.wrench.assets.flow.api.FlowAstFactory.Node;
import io.resys.wrench.assets.flow.api.FlowAstFactory.NodeFlow;


@Value.Immutable
public interface FlowAst extends AstType, Serializable {
  Node getSrc();
  List<AstCommandType> getCommands();
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

  enum FlowCommandType { SET, ADD, DELETE }
  
  interface NodeFlowVisitor {
    void visit(NodeFlow node, ImmutableFlowAst.Builder nodesBuilder);
  }

}
