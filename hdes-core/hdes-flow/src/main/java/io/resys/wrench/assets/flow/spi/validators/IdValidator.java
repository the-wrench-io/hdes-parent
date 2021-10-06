package io.resys.wrench.assets.flow.spi.validators;

import org.apache.commons.lang3.StringUtils;

import io.resys.hdes.client.api.ast.FlowAstType.FlowCommandMessageType;
import io.resys.hdes.client.api.ast.FlowAstType.NodeFlow;
import io.resys.hdes.client.api.ast.FlowAstType.NodeFlowVisitor;
import io.resys.hdes.client.api.ast.ImmutableFlowAstCommandMessage;
import io.resys.hdes.client.api.ast.ImmutableFlowAstType;
import io.resys.hdes.client.spi.flow.ast.FlowNodesFactory;

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

public class IdValidator implements NodeFlowVisitor {

  @Override
  public void visit(NodeFlow node, ImmutableFlowAstType.Builder modelBuilder) {
    if(node.getId() == null) {
      modelBuilder.addMessages(
          ImmutableFlowAstCommandMessage.builder()
          .line(0)
          .value("flow must have id")
          .range(FlowNodesFactory.range().build(0, 0))
          .type(FlowCommandMessageType.ERROR)
          .build());
      return;
    }
    if(StringUtils.isEmpty(node.getId().getValue())) {
      modelBuilder.addMessages(
          ImmutableFlowAstCommandMessage.builder()
          .line(node.getId().getStart())
          .value("flow id must have a value")
          .range(FlowNodesFactory.range().build(0, node.getId().getSource().getValue().length()))
          .type(FlowCommandMessageType.ERROR)
          .build());
    }
  }
}
