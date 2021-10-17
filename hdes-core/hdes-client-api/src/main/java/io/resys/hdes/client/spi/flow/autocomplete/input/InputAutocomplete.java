package io.resys.hdes.client.spi.flow.autocomplete.input;

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

import io.resys.hdes.client.api.ast.AstFlow.AstFlowNode;
import io.resys.hdes.client.api.ast.AstFlow.AstFlowRoot;
import io.resys.hdes.client.api.ast.AstFlow.FlowAstCommandRange;
import io.resys.hdes.client.api.ast.ImmutableAstFlow;
import io.resys.hdes.client.spi.config.HdesClientConfig.AstFlowNodeVisitor;
import io.resys.hdes.client.spi.flow.ast.AstFlowNodesFactory;
import io.resys.hdes.client.spi.flow.ast.beans.NodeFlowBean;

public class InputAutocomplete implements AstFlowNodeVisitor {

  @Override
  public void visit(AstFlowRoot flow, ImmutableAstFlow.Builder modelBuilder) {
    AstFlowNode node = flow.get(NodeFlowBean.KEY_INPUTS);
    if(node == null) {
      return;
    }

    List<AstFlowNode> allInputs = new ArrayList<>(node.getChildren().values());
    int previous = node.getStart();
    Collections.sort(allInputs);

    Collection<FlowAstCommandRange> range = new ArrayList<>();
    for(AstFlowNode input : allInputs) {
      if(input.getStart() - previous > 1) {
        range.add(AstFlowNodesFactory.range().build(previous + 1, input.getStart() - 1));
      }
      previous = node.getStart();
    }

    int start = node.getEnd();
    int end = flow.hasNonNull(NodeFlowBean.KEY_TASKS) ? flow.get(NodeFlowBean.KEY_TASKS).getStart() - 1 : flow.getEnd();

    modelBuilder.addAutocomplete(
        AstFlowNodesFactory.ac()
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