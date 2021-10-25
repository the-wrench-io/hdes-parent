package io.resys.hdes.client.spi.flow.autocomplete.task;

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

import io.resys.hdes.client.api.ast.AstFlow.AstFlowNode;
import io.resys.hdes.client.api.ast.AstFlow.AstFlowRoot;
import io.resys.hdes.client.api.ast.AstFlow.FlowAstCommandRange;
import io.resys.hdes.client.api.ast.ImmutableAstFlow;
import io.resys.hdes.client.spi.config.HdesClientConfig.AstFlowNodeVisitor;
import io.resys.hdes.client.spi.flow.ast.AstFlowNodesFactory;
import io.resys.hdes.client.spi.flow.ast.beans.NodeFlowBean;

public class TaskAutocomplete implements AstFlowNodeVisitor {

  @Override
  public void visit(AstFlowRoot flow, ImmutableAstFlow.Builder modelBuilder) {
    AstFlowNode tasks = flow.get(NodeFlowBean.KEY_TASKS);
    if(tasks == null) {
      return;
    }

    List<AstFlowNode> taskChildren = new ArrayList<>(flow.getTasks().values());
    Collections.sort(taskChildren);

    List<FlowAstCommandRange> ranges = new ArrayList<>();
    int previous = tasks.getStart();
    for(AstFlowNode child : taskChildren) {

      int start = child.getStart() - 1;
      if(previous <= start) {
        ranges.add(AstFlowNodesFactory.range().build(previous, start));
      }
      previous = child.getEnd() + 1;
    }

    if(flow.getEnd() >= previous) {
      ranges.add(AstFlowNodesFactory.range().build(previous, flow.getEnd()));
    }

    modelBuilder.addAutocomplete(
        AstFlowNodesFactory.ac()
        .id(TaskAutocomplete.class.getSimpleName())
        .addField(2, "- {name}")
        .addField(6, "id", "{id}")
        .addField(6, "then", "{then}")
        .addField(6, "{type}")
        .addField(8, "ref", "{ref}")
        .addField(8, "collection", "{collection}")
        .addField(8, "inputs")
        .addRange(ranges)
        .build());

  }
}
