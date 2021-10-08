package io.resys.wrench.assets.bundle.spi.dt.resolvers;

/*-
 * #%L
 * wrench-assets-bundle
 * %%
 * Copyright (C) 2016 - 2018 Copyright 2016 ReSys OÜ
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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import io.resys.hdes.client.api.ast.TypeDef;
import io.resys.hdes.client.api.execution.FlowResult;
import io.resys.hdes.client.api.execution.FlowResult.FlowTaskStatus;
import io.resys.hdes.client.api.execution.FlowProgram.Step;
import io.resys.wrench.assets.bundle.spi.dt.DtInputResolver;
import io.resys.wrench.assets.bundle.spi.flow.executors.VariableResolver;

public class FlowDtInputResolver implements Serializable, DtInputResolver {

  private static final long serialVersionUID = 8818379343078413837L;
  private final VariableResolver variableResolver;
  private final Map<String, Object> variables;
  private final Map<String, Serializable> tasks;
  private final Map<String, String> mapping;

  public FlowDtInputResolver(FlowResult flow, Step node, VariableResolver variableResolver) {
    mapping = node.getBody().getInputs();
    variables = new HashMap<>();
    tasks = new HashMap<>();
    this.variableResolver = variableResolver;

    // Flow level variables
    flow.getContext().getVariables().forEach((key, value) -> variables.put(key, value));

    // Completed task variables
    flow.getContext().getTasks().stream()
    .filter(t -> t.getStatus() == FlowTaskStatus.COMPLETED)
    .forEach(t -> tasks.put(t.getModelId(), t.getVariables().get(t.getModelId())));
  }

  @Override
  public Object apply(TypeDef t) {
    String name = mapping.get(t.getName());

    // Flat mapping
    if(variables.containsKey(name)) {
      return variables.get(name);
    }
    return variableResolver.getVariableOnPath(name, tasks);
  }
}
