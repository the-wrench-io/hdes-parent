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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.resys.hdes.client.api.ast.FlowAstType.FlowAstNode;
import io.resys.hdes.client.api.ast.FlowAstType.NodeFlow;
import io.resys.hdes.client.api.ast.FlowAstType.NodeFlowVisitor;
import io.resys.hdes.client.spi.flow.ast.FlowNodesFactory;
import io.resys.hdes.client.spi.flow.ast.beans.NodeFlowBean;
import io.resys.hdes.client.api.ast.ImmutableFlowAstType;
import io.resys.wrench.assets.flow.spi.hints.input.InputsAutocomplete;

public class TasksAutocomplete implements NodeFlowVisitor {

  private final static List<String> AFTER = Arrays.asList(
      NodeFlowBean.KEY_ID,
      NodeFlowBean.KEY_DESC,
      NodeFlowBean.KEY_INPUTS);

  @Override
  public void visit(NodeFlow flow, ImmutableFlowAstType.Builder modelBuilder) {
    FlowAstNode node = flow.get(NodeFlowBean.KEY_TASKS);
    if(node != null) {
      return;
    }

    List<Integer> after = AFTER.stream()
        .filter(name -> flow.hasNonNull(name))
        .map(name -> flow.get(name).getStart()).sorted()
        .collect(Collectors.toList());
    if(after.isEmpty()) {
      return;
    }

    int start = after.get(after.size() - 1) + 1;
    int end = flow.getEnd();

    modelBuilder.addAutocomplete(
        FlowNodesFactory.ac()
        .id(InputsAutocomplete.class.getSimpleName())
        .addField(NodeFlowBean.KEY_TASKS)
        .addRange(start, end)
        .build());
  }
}
