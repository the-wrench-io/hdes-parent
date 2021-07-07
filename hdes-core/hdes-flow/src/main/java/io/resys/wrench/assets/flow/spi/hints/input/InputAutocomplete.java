package io.resys.wrench.assets.flow.spi.hints.input;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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

import io.resys.wrench.assets.flow.api.FlowAstFactory.Node;
import io.resys.wrench.assets.flow.api.FlowAstFactory.NodeFlow;
import io.resys.wrench.assets.flow.api.model.FlowAst.FlowCommandRange;
import io.resys.wrench.assets.flow.api.model.FlowAst.NodeFlowVisitor;
import io.resys.wrench.assets.flow.api.model.ImmutableFlowAst;
import io.resys.wrench.assets.flow.spi.model.NodeFlowBean;
import io.resys.wrench.assets.flow.spi.support.FlowNodesFactory;

public class InputAutocomplete implements NodeFlowVisitor {

  @Override
  public void visit(NodeFlow flow, ImmutableFlowAst.Builder modelBuilder) {
    Node node = flow.get(NodeFlowBean.KEY_INPUTS);
    if(node == null) {
      return;
    }

    List<Node> allInputs = new ArrayList<>(node.getChildren().values());
    int previous = node.getStart();
    Collections.sort(allInputs);

    Collection<FlowCommandRange> range = new ArrayList<>();
    for(Node input : allInputs) {
      if(input.getStart() - previous > 1) {
        range.add(FlowNodesFactory.range().build(previous + 1, input.getStart() - 1));
      }
      previous = node.getStart();
    }

    int start = node.getEnd();
    int end = flow.hasNonNull(NodeFlowBean.KEY_TASKS) ? flow.get(NodeFlowBean.KEY_TASKS).getStart() - 1 : flow.getEnd();

    modelBuilder.addAutocomplete(
        FlowNodesFactory.ac()
        .id(InputAutocomplete.class.getSimpleName())
        .addField(2, "{name}")
        .addField(4, "required", "{required}")
        .addField(4, "type", "{type}")
        .addField(4, "debugValue", "{debugValue}")
        .addRange(start, end)
        .addRange(range)
        .build());
  }
}
