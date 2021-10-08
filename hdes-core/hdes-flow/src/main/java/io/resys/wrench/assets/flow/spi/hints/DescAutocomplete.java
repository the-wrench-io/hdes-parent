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

import io.resys.hdes.client.api.ast.AstFlow.AstFlowRoot;
import io.resys.hdes.client.api.ast.AstFlow.AstFlowNodeVisitor;
import io.resys.hdes.client.api.ast.ImmutableAstFlow;
import io.resys.hdes.client.spi.flow.ast.AstFlowNodesFactory;
import io.resys.hdes.client.spi.flow.ast.beans.NodeFlowBean;

public class DescAutocomplete implements AstFlowNodeVisitor {

  private final static List<String> BEFORE = Arrays.asList(
      NodeFlowBean.KEY_INPUTS,
      NodeFlowBean.KEY_TASKS);

  @Override
  public void visit(AstFlowRoot flow, ImmutableAstFlow.Builder modelBuilder) {
    if(flow.getDescription() != null || flow.getId() == null) {
      return;
    }

    Optional<Integer> before = BEFORE.stream()
        .filter(name -> flow.hasNonNull(name)).findFirst()
        .map(name -> flow.get(name).getStart() - 1);

    int start = flow.getId().getEnd() + 1;
    int end = before.orElse(flow.getEnd());

    modelBuilder.addAutocomplete(
        AstFlowNodesFactory.ac()
        .id(DescAutocomplete.class.getSimpleName())
        .addField(NodeFlowBean.KEY_DESC)
        .addRange(start, end)
        .build());
  }
}
