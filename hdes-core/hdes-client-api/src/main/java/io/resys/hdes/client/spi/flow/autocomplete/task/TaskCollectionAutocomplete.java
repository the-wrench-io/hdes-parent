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
import java.util.List;
import java.util.Map;

import io.resys.hdes.client.api.ast.AstFlow.AstFlowRefNode;
import io.resys.hdes.client.api.ast.AstFlow.AstFlowRoot;
import io.resys.hdes.client.api.ast.AstFlow.AstFlowTaskNode;
import io.resys.hdes.client.api.ast.AstFlow.FlowAstCommandRange;
import io.resys.hdes.client.api.ast.ImmutableAstFlow;
import io.resys.hdes.client.spi.config.HdesClientConfig.AstFlowNodeVisitor;
import io.resys.hdes.client.spi.flow.ast.AstFlowNodesFactory;
import io.resys.hdes.client.spi.flow.ast.beans.NodeFlowBean;

public class TaskCollectionAutocomplete implements AstFlowNodeVisitor {

  @Override
  public void visit(AstFlowRoot flow, ImmutableAstFlow.Builder modelBuilder) {
    Map<String, AstFlowTaskNode> tasks = flow.getTasks();

    if(tasks.isEmpty()) {
      return;
    }

    List<FlowAstCommandRange> ranges = new ArrayList<>();
    for(AstFlowTaskNode task : tasks.values()) {
      AstFlowRefNode ref = task.getRef();
      if(ref == null) {
        continue;
      }

      FlowAstCommandRange range;
      if(ref.getCollection() != null) {
        range = AstFlowNodesFactory.range().build(ref.getCollection().getStart());
      } else {
        range = AstFlowNodesFactory.range().build(ref.getStart(), ref.getEnd(), true);
      }

      ranges.add(range);
    }

    if(!ranges.isEmpty()) {
      modelBuilder
      .addAutocomplete(AstFlowNodesFactory.ac()
          .id(TaskCollectionAutocomplete.class.getSimpleName())
          .addField("        " + NodeFlowBean.KEY_COLLECTION, true)
          .addRange(ranges)
          .build())
      .addAutocomplete(AstFlowNodesFactory.ac()
          .id(TaskCollectionAutocomplete.class.getSimpleName())
          .addField("        " + NodeFlowBean.KEY_COLLECTION, false)
          .addRange(ranges)
          .build());
    }
  }
}
