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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.resys.hdes.client.api.ast.FlowAstType.FlowCommandRange;
import io.resys.hdes.client.api.ast.FlowAstType.NodeFlow;
import io.resys.hdes.client.api.ast.FlowAstType.NodeFlowVisitor;
import io.resys.hdes.client.api.ast.FlowAstType.NodeInput;
import io.resys.hdes.client.api.ast.ImmutableFlowAstType;
import io.resys.wrench.assets.flow.spi.model.NodeFlowBean;
import io.resys.wrench.assets.flow.spi.support.FlowNodesFactory;

public class InputDebugValueAutocomplete implements NodeFlowVisitor {

  @Override
  public void visit(NodeFlow flow, ImmutableFlowAstType.Builder modelBuilder) {
    Map<String, NodeInput> inputs = flow.getInputs();

    if(inputs.isEmpty()) {
      return;
    }

    List<FlowCommandRange> ranges = new ArrayList<>();
    for(NodeInput input : inputs.values()) {
      if(input.getDebugValue() == null) {
        FlowCommandRange range = FlowNodesFactory.range().build(input.getStart(), input.getEnd(), true);
        ranges.add(range);
      }
    }

    if(!ranges.isEmpty()) {
      modelBuilder.addAutocomplete(FlowNodesFactory.ac()
          .id(InputDebugValueAutocomplete.class.getSimpleName())
          .addField("    " + NodeFlowBean.KEY_DEBUG_VALUE)
          .addRange(ranges)
          .build());
    }
  }
}
