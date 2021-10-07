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
import java.util.Collection;
import java.util.List;

import io.resys.hdes.client.api.ast.AstFlow.FlowAstCommandRange;
import io.resys.hdes.client.api.ast.AstFlow.FlowAstTask;
import io.resys.hdes.client.api.ast.AstFlow.NodeFlow;
import io.resys.hdes.client.api.ast.AstFlow.NodeFlowVisitor;
import io.resys.hdes.client.api.ast.ImmutableAstFlow;
import io.resys.hdes.client.spi.flow.ast.FlowNodesFactory;

public class TaskTypeAutocomplete implements NodeFlowVisitor {

  @Override
  public void visit(NodeFlow flow, ImmutableAstFlow.Builder modelBuilder) {
    Collection<FlowAstTask> tasks = flow.getTasks().values();
    if(tasks.isEmpty()) {
      return;
    }

    List<FlowAstCommandRange> ranges = new ArrayList<>();
    for(FlowAstTask child : tasks) {
      if(child.getDecisionTable() == null && child.getService() == null) {
        ranges.add(FlowNodesFactory.range().build(child.getStart(), child.getEnd(), true));
      }
    }

    modelBuilder.addAutocomplete(
        FlowNodesFactory.ac()
        .id(TaskTypeAutocomplete.class.getSimpleName())
        .addField(6, "{type}")
        .addField(8, "ref", "{ref}")
        .addField(8, "restful", "{restful}")
        .addField(8, "collection", "{collection}")
        .addField(8, "inputs")
        .addRange(ranges)
        .build());

  }
}
