package io.resys.wrench.assets.flow.spi.validators;

import org.apache.commons.lang3.StringUtils;

import io.resys.wrench.assets.flow.api.FlowAstFactory.NodeFlow;
import io.resys.wrench.assets.flow.api.model.FlowAst.FlowCommandMessageType;
import io.resys.wrench.assets.flow.api.model.FlowAst.NodeFlowVisitor;
import io.resys.wrench.assets.flow.api.model.ImmutableFlowAst;
import io.resys.wrench.assets.flow.api.model.ImmutableFlowCommandMessage;
import io.resys.wrench.assets.flow.spi.support.FlowNodesFactory;

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

public class DescriptionValidator implements NodeFlowVisitor {

  @Override
  public void visit(NodeFlow node, ImmutableFlowAst.Builder modelBuilder) {
    if(node.getDescription() == null) {
      return;
    }
    if(StringUtils.isEmpty(node.getDescription().getValue())) {
      modelBuilder.addMessages(
          ImmutableFlowCommandMessage.builder()
          .line(node.getDescription().getStart())
          .value("flow description must have a value")
          .range(FlowNodesFactory.range().build(0, node.getDescription().getSource().getValue().length()))
          .type(FlowCommandMessageType.WARNING)
          .build());
    }
  }
}
