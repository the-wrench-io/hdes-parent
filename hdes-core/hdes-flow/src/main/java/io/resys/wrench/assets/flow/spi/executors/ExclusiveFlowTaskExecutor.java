package io.resys.wrench.assets.flow.spi.executors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/*-
 * #%L
 * wrench-component-assets-flow
 * %%
 * Copyright (C) 2016 - 2017 Copyright 2016 ReSys OÜ
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.resys.hdes.client.api.programs.FlowResult;
import io.resys.hdes.client.api.programs.FlowProgram.Step;
import io.resys.hdes.client.api.programs.FlowResult.FlowTask;
import io.resys.hdes.client.api.programs.FlowResult.FlowTaskStatus;
import io.resys.hdes.client.spi.util.HdesAssert;
import io.resys.wrench.assets.flow.api.FlowExecutorRepository;
import io.resys.wrench.assets.flow.spi.FlowException;

public class ExclusiveFlowTaskExecutor implements FlowExecutorRepository.FlowTaskExecutor {
  private static final Logger LOGGER = LoggerFactory.getLogger(ExclusiveFlowTaskExecutor.class);

  @Override
  public Step execute(FlowResult flow, FlowTask task) {
    Step node = flow.getModel().getStep().get(task.getModelId());

    // Need to eval all child node to figure out what's the next node
    Step defaultNode = null;
    for(Step next : node.getNext()) {
      if(next.getBody() == null) {
        defaultNode = next;
        continue;
      }
      if(eval(flow, task, next)) {
        HdesAssert.isTrue(next.getNext().size() <= 1, () -> "There can be only 1 or 0 next nodes in single decision point: " + next.getId() + "!");
        if(next.getNext().isEmpty()) {
          return null;
        }
        flow.complete(task);
        Step result = next.getNext().iterator().next();
        return result;
      }
    }
    if(defaultNode != null) {
      flow.complete(task);
      return defaultNode.getNext().iterator().next();
    }
    throw new IllegalArgumentException("No matching expressions for switch node: " + node.getId() + "!");
  }

  protected boolean eval(FlowResult flow, FlowTask task, Step node) {
    try {
      return node.getBody().getExpression().eval((name) -> getFlowContextValue(flow, name));
    } catch(Exception e) {
      String message = "Failed to evaluate expression: \"" + node.getBody().getExpression() + "\" in flow: \"" + flow.getModel().getId() + "\", id: " + flow.getId() + ", decision: \"" + node.getId() + "\"!";
      LOGGER.error(message, e);
      throw new FlowException(message, flow, node, e);
    }
  }

  protected Object getFlowContextValue(FlowResult flow, String name) {
    Map<String, Serializable> vars = new HashMap<>();
    vars.putAll(flow.getContext().getVariables());

    for(FlowTask previousTask : flow.getContext().getTasks()) {
      if(previousTask.getStatus() == FlowTaskStatus.COMPLETED) {
        Serializable taskOutput = previousTask.getVariables().get(previousTask.getModelId());
        vars.put(previousTask.getModelId(), taskOutput);
      }
    }
    if(flow.getContext().getVariables().containsKey(name)) {
      return flow.getContext().getVariables().get(name);
    }
    for(FlowTask task : flow.getContext().getTasks(name)) {
      Serializable result = task.getVariables().get(task.getModelId());
      return result;
    }
    return null;
  }

}
