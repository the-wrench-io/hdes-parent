package io.resys.wrench.assets.flow.spi.hints.input;

/*-
 * #%L
 * hdes-flow
 * %%
 * Copyright (C) 2020 - 2021 Copyright 2020 ReSys OÜ
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import io.resys.hdes.client.api.ast.FlowAstType.FlowCommandRange;
import io.resys.hdes.client.api.ast.FlowAstType.Node;
import io.resys.hdes.client.api.ast.FlowAstType.NodeFlow;
import io.resys.hdes.client.api.ast.FlowAstType.NodeFlowVisitor;
import io.resys.hdes.client.api.ast.ImmutableFlowAstType;
import io.resys.wrench.assets.flow.spi.model.NodeFlowBean;
import io.resys.wrench.assets.flow.spi.support.FlowNodesFactory;

public class InputAutocomplete implements NodeFlowVisitor {

  @Override
  public void visit(NodeFlow flow, ImmutableFlowAstType.Builder modelBuilder) {
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
