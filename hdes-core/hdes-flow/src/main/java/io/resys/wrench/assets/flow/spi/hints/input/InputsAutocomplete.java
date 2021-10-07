package io.resys.wrench.assets.flow.spi.hints.input;

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

import io.resys.hdes.client.api.ast.AstFlow.FlowAstNode;
import io.resys.hdes.client.api.ast.AstFlow.NodeFlow;
import io.resys.hdes.client.api.ast.AstFlow.NodeFlowVisitor;
import io.resys.hdes.client.api.ast.ImmutableAstFlow;
import io.resys.hdes.client.api.ast.TypeDef.ValueType;
import io.resys.hdes.client.spi.flow.ast.FlowNodesFactory;
import io.resys.hdes.client.spi.flow.ast.beans.NodeFlowBean;

public class InputsAutocomplete implements NodeFlowVisitor {

  private final static List<String> AFTER = Arrays.asList(
      NodeFlowBean.KEY_ID,
      NodeFlowBean.KEY_DESC
      );

  @Override
  public void visit(NodeFlow flow, ImmutableAstFlow.Builder modelBuilder) {
    FlowAstNode node = flow.get(NodeFlowBean.KEY_INPUTS);
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
    int end = flow.hasNonNull(NodeFlowBean.KEY_TASKS) ? flow.get(NodeFlowBean.KEY_TASKS).getStart() - 1 : flow.getEnd();

    modelBuilder.addAutocomplete(
        FlowNodesFactory.ac()
        .id(InputsAutocomplete.class.getSimpleName())
        .addField(NodeFlowBean.KEY_INPUTS)
        .addField(2, "myInputParam")
        .addField(4, "required", true)
        .addField(4, "type", ValueType.STRING)
        .addRange(start, end)
        .build());
  }
}
