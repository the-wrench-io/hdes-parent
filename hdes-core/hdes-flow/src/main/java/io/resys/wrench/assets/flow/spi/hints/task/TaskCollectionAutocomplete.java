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
import java.util.List;
import java.util.Map;

import io.resys.wrench.assets.flow.api.FlowAstFactory.NodeFlow;
import io.resys.wrench.assets.flow.api.FlowAstFactory.NodeRef;
import io.resys.wrench.assets.flow.api.FlowAstFactory.NodeTask;
import io.resys.wrench.assets.flow.api.model.FlowAst.FlowCommandRange;
import io.resys.wrench.assets.flow.api.model.FlowAst.NodeFlowVisitor;
import io.resys.wrench.assets.flow.api.model.ImmutableFlowAst;
import io.resys.wrench.assets.flow.spi.model.NodeFlowBean;
import io.resys.wrench.assets.flow.spi.support.FlowNodesFactory;

public class TaskCollectionAutocomplete implements NodeFlowVisitor {

  @Override
  public void visit(NodeFlow flow, ImmutableFlowAst.Builder modelBuilder) {
    Map<String, NodeTask> tasks = flow.getTasks();

    if(tasks.isEmpty()) {
      return;
    }

    List<FlowCommandRange> ranges = new ArrayList<>();
    for(NodeTask task : tasks.values()) {
      NodeRef ref = task.getRef();
      if(ref == null) {
        continue;
      }

      FlowCommandRange range;
      if(ref.getCollection() != null) {
        range = FlowNodesFactory.range().build(ref.getCollection().getStart());
      } else {
        range = FlowNodesFactory.range().build(ref.getStart(), ref.getEnd(), true);
      }

      ranges.add(range);
    }

    if(!ranges.isEmpty()) {
      modelBuilder
      .addAutocomplete(FlowNodesFactory.ac()
          .id(TaskCollectionAutocomplete.class.getSimpleName())
          .addField("        " + NodeFlowBean.KEY_COLLECTION, true)
          .addRange(ranges)
          .build())
      .addAutocomplete(FlowNodesFactory.ac()
          .id(TaskCollectionAutocomplete.class.getSimpleName())
          .addField("        " + NodeFlowBean.KEY_COLLECTION, false)
          .addRange(ranges)
          .build());
    }
  }
}
