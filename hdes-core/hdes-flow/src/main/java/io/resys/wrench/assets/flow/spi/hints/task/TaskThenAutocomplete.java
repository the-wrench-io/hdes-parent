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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.resys.hdes.client.api.ast.FlowAstType.FlowAstCommandRange;
import io.resys.hdes.client.api.ast.FlowAstType.NodeFlow;
import io.resys.hdes.client.api.ast.FlowAstType.NodeFlowVisitor;
import io.resys.hdes.client.api.ast.FlowAstType.FlowAstTask;
import io.resys.hdes.client.spi.flow.ast.FlowNodesFactory;
import io.resys.hdes.client.spi.flow.ast.beans.NodeFlowBean;
import io.resys.hdes.client.api.ast.ImmutableFlowAstType;
import io.resys.wrench.assets.flow.spi.support.NodeFlowAdapter;

public class TaskThenAutocomplete implements NodeFlowVisitor {


  @Override
  public void visit(NodeFlow flow, ImmutableFlowAstType.Builder modelBuilder) {
    Map<String, FlowAstTask> tasks = flow.getTasks();
    if(tasks.isEmpty()) {
      return;
    }

    List<String> ref = tasks.values().stream().map(t -> NodeFlowAdapter.getStringValue(t.getId()))
        .filter(t -> t != null).collect(Collectors.toList());
    ref.add(NodeFlowBean.VALUE_END);

    for(FlowAstTask task : tasks.values()) {

      String taskId = NodeFlowAdapter.getStringValue(task.getId());
      final FlowAstCommandRange range;

      if(task.getThen() == null) {
        int start = task.getId() == null ? task.getStart() : task.getId().getEnd();
        range = FlowNodesFactory.range().build(start, start, true);
      } else {
        range = FlowNodesFactory.range().build(task.getThen().getStart(), task.getThen().getEnd(), false);
      }

      ref.stream().filter(r -> !r.equals(taskId)).forEach(r -> modelBuilder.addAutocomplete(FlowNodesFactory.ac().id(TaskThenAutocomplete.class.getSimpleName())
          .addField(6, NodeFlowBean.KEY_THEN, r)
          .addRange(range)
          .build()));

    }
  }
}
