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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.resys.hdes.client.api.ast.AstType.ValueType;
import io.resys.hdes.client.api.ast.FlowAstType.FlowAstCommandRange;
import io.resys.hdes.client.api.ast.FlowAstType.NodeFlow;
import io.resys.hdes.client.api.ast.FlowAstType.NodeFlowVisitor;
import io.resys.hdes.client.api.ast.FlowAstType.FlowAstInput;
import io.resys.hdes.client.spi.flow.ast.FlowNodesFactory;
import io.resys.hdes.client.spi.flow.ast.beans.NodeFlowBean;
import io.resys.hdes.client.api.ast.ImmutableFlowAstType;

public class InputDataTypeAutocomplete implements NodeFlowVisitor {

  private static final String ID = InputDataTypeAutocomplete.class.getSimpleName();
  private static final Collection<String> TYPES = Arrays.asList(
      ValueType.ARRAY,
      ValueType.TIME, ValueType.DATE, ValueType.DATE_TIME,
      ValueType.STRING,
      ValueType.INTEGER, ValueType.LONG, ValueType.DECIMAL,
      ValueType.BOOLEAN).stream().map(v -> v.name())
      .collect(Collectors.toList());

  @Override
  public void visit(NodeFlow flow, ImmutableFlowAstType.Builder modelBuilder) {
    Map<String, FlowAstInput> inputs = flow.getInputs();

    if(inputs.isEmpty()) {
      return;
    }

    List<FlowAstCommandRange> ranges = new ArrayList<>();
    for(FlowAstInput input : inputs.values()) {
      if(input.getType() != null) {
        ranges.add(FlowNodesFactory.range().build(input.getType().getStart()));
      } else {
        ranges.add(FlowNodesFactory.range().build(input.getStart(), input.getEnd(), true));
      }
    }

    for(String type : TYPES) {
      if(!ranges.isEmpty()) {
        modelBuilder.addAutocomplete(FlowNodesFactory.ac()
            .id(ID)
            .addField("    " + NodeFlowBean.KEY_TYPE, type)
            .addRange(ranges)
            .build());
      }
    }
  }
}
