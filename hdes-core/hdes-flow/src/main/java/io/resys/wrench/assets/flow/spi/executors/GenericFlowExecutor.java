package io.resys.wrench.assets.flow.spi.executors;

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


import java.util.function.Function;

import io.resys.wrench.assets.flow.api.FlowExecutorRepository.FlowExecutor;
import io.resys.wrench.assets.flow.api.FlowExecutorRepository.FlowTaskExecutor;
import io.resys.hdes.client.api.execution.FlowResult;
import io.resys.hdes.client.api.execution.FlowResult.FlowStatus;
import io.resys.hdes.client.api.execution.FlowResult.FlowTask;
import io.resys.hdes.client.api.execution.FlowProgram.Step;
import io.resys.hdes.client.api.execution.FlowProgram.FlowTaskType;
import io.resys.wrench.assets.flow.api.FlowTaskExecutorException;
import io.resys.wrench.assets.flow.spi.FlowException;

public class GenericFlowExecutor implements FlowExecutor {

  private final Function<FlowTaskType, FlowTaskExecutor> executor;

  public GenericFlowExecutor(
      Function<FlowTaskType, FlowTaskExecutor> executor) {
    super();
    this.executor = executor;
  }

  @Override
  public void execute(FlowResult flow) {
    flow.getContext().setStatus(FlowStatus.RUNNING);
    Step node = flow.getModel().getStep().get(flow.getContext().getPointer());
    run(flow, node);
  }

  protected void run(FlowResult flow, Step node) {

    try {
      FlowTask executable = flow.start(node);
      Step next = executor.apply(node.getType()).execute(flow, executable);

      if(flow.getContext().getStatus() == FlowStatus.ENDED ||
          flow.getContext().getStatus() == FlowStatus.SUSPENDED) {

        return;
      }

      run(flow, next);
    } catch(FlowException e) {
      throw e;
    } catch(FlowTaskExecutorException e) {
      final String msg = "Flow with id: " + flow.getModel().getId() + ", failed to execute task: " + node.getId() + " because: " + e.getMessage();
      throw new FlowException(msg, flow, node);      
    } catch(Exception e) {
      final String msg = "Flow with id: " + flow.getModel().getId() + ", failed to execute task: " + node.getId() + " because: " + e.getMessage();
      throw new FlowException(msg, flow, node);
    }
  }
}
