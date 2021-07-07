package io.resys.wrench.assets.flow.spi.hints;

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
import java.util.Optional;

import io.resys.wrench.assets.flow.api.FlowAstFactory.NodeFlow;
import io.resys.wrench.assets.flow.api.model.FlowAst.NodeFlowVisitor;
import io.resys.wrench.assets.flow.api.model.ImmutableFlowAst;
import io.resys.wrench.assets.flow.spi.model.NodeFlowBean;
import io.resys.wrench.assets.flow.spi.support.FlowNodesFactory;

public class DescAutocomplete implements NodeFlowVisitor {

  private final static List<String> BEFORE = Arrays.asList(
      NodeFlowBean.KEY_INPUTS,
      NodeFlowBean.KEY_TASKS);

  @Override
  public void visit(NodeFlow flow, ImmutableFlowAst.Builder modelBuilder) {
    if(flow.getDescription() != null || flow.getId() == null) {
      return;
    }

    Optional<Integer> before = BEFORE.stream()
        .filter(name -> flow.hasNonNull(name)).findFirst()
        .map(name -> flow.get(name).getStart() - 1);

    int start = flow.getId().getEnd() + 1;
    int end = before.orElse(flow.getEnd());

    modelBuilder.addAutocomplete(
        FlowNodesFactory.ac()
        .id(DescAutocomplete.class.getSimpleName())
        .addField(NodeFlowBean.KEY_DESC)
        .addRange(start, end)
        .build());
  }
}
