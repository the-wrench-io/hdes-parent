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

import io.resys.wrench.assets.datatype.api.DataTypeRepository.ValueType;
import io.resys.wrench.assets.flow.api.FlowAstFactory.NodeFlow;
import io.resys.wrench.assets.flow.api.FlowAstFactory.NodeInput;
import io.resys.wrench.assets.flow.api.model.FlowAst.FlowCommandRange;
import io.resys.wrench.assets.flow.api.model.FlowAst.NodeFlowVisitor;
import io.resys.wrench.assets.flow.api.model.ImmutableFlowAst;
import io.resys.wrench.assets.flow.spi.model.NodeFlowBean;
import io.resys.wrench.assets.flow.spi.support.FlowNodesFactory;

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
  public void visit(NodeFlow flow, ImmutableFlowAst.Builder modelBuilder) {
    Map<String, NodeInput> inputs = flow.getInputs();

    if(inputs.isEmpty()) {
      return;
    }

    List<FlowCommandRange> ranges = new ArrayList<>();
    for(NodeInput input : inputs.values()) {
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
