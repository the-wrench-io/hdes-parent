package io.resys.wrench.assets.flow.spi.hints.task;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.resys.hdes.client.api.ast.AstFlow.FlowAstCommandRange;
import io.resys.hdes.client.api.ast.AstFlow.FlowAstNode;
import io.resys.hdes.client.api.ast.AstFlow.NodeFlow;
import io.resys.hdes.client.api.ast.AstFlow.NodeFlowVisitor;
import io.resys.hdes.client.api.ast.ImmutableAstFlow;
import io.resys.hdes.client.spi.flow.ast.FlowNodesFactory;
import io.resys.hdes.client.spi.flow.ast.beans.NodeFlowBean;

public class SwitchAutocomplete implements NodeFlowVisitor {

  @Override
  public void visit(NodeFlow flow, ImmutableAstFlow.Builder modelBuilder) {
    FlowAstNode tasks = flow.get(NodeFlowBean.KEY_TASKS);
    if(tasks == null) {
      return;
    }

    List<FlowAstNode> taskChildren = new ArrayList<>(flow.getTasks().values());
    Collections.sort(taskChildren);

    List<FlowAstCommandRange> ranges = new ArrayList<>();
    int previous = tasks.getStart();
    for(FlowAstNode child : taskChildren) {

      int start = child.getStart() - 1;
      if(previous <= start) {
        ranges.add(FlowNodesFactory.range().build(previous, start));
      }
      previous = child.getEnd() + 1;
    }

    if(flow.getEnd() >= previous) {
      ranges.add(FlowNodesFactory.range().build(previous, flow.getEnd()));
    }

    modelBuilder.addAutocomplete(
        FlowNodesFactory.ac()
        .id(SwitchAutocomplete.class.getSimpleName())
        .addField(2, "- {name}")
        .addField(6, "id", "{id}")
        .addField(6, "switch")
        .addField(8, "- {caseName}")
        .addField(12, "when", "{when}")
        .addField(12, "then", "{then}")
        .addRange(ranges)
        .build());

  }
}
