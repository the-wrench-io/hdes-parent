package io.resys.wrench.assets.bundle.spi.flow.executors;

import java.io.Serializable;

/*-
 * #%L
 * wrench-component-assets-activiti
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.Service;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceExecution;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceResponse;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceStore;
import io.resys.wrench.assets.bundle.spi.builders.GenericServiceQuery;
import io.resys.wrench.assets.bundle.spi.exceptions.DataException;
import io.resys.wrench.assets.bundle.spi.exceptions.Message;
import io.resys.wrench.assets.bundle.spi.exceptions.MessageList;
import io.resys.wrench.assets.bundle.spi.flowtask.FlowTaskInput;
import io.resys.wrench.assets.flow.api.FlowTaskExecutorException;
import io.resys.wrench.assets.flow.api.model.Flow;
import io.resys.wrench.assets.flow.api.model.Flow.FlowTask;
import io.resys.wrench.assets.flow.api.model.Flow.FlowTaskStatus;
import io.resys.wrench.assets.flow.api.model.FlowModel.FlowTaskModel;
import io.resys.wrench.assets.flow.api.model.FlowModel.FlowTaskValue;
import io.resys.wrench.assets.flow.spi.executors.ServiceFlowTaskExecutor;

public class GenericFlowServiceExecutor extends ServiceFlowTaskExecutor {

  private static final Logger LOGGER = LoggerFactory.getLogger(GenericFlowServiceExecutor.class);

  private final VariableResolver variableResolver;
  private final ServiceStore serviceStore;

  public GenericFlowServiceExecutor(ServiceStore serviceStore, VariableResolver variableResolver) {
    super();
    this.serviceStore = serviceStore;
    this.variableResolver = variableResolver;
  }

  @Override
  public FlowTaskModel execute(Flow flow, FlowTask task) {
    try {
      FlowTaskModel node = flow.getModel().getTask().get(task.getModelId());
      String flowTaskId = node.getBody().getRef();

      Service service = new GenericServiceQuery(serviceStore).flowTask(flowTaskId);

      Map<String, Serializable> tasks = new HashMap<>();
      flow.getContext().getTasks().stream()
      .filter(t -> t.getStatus() == FlowTaskStatus.COMPLETED)
      .forEach(t -> tasks.put(t.getModelId(), t.getVariables().get(t.getModelId())));

      // Execute all services
      ServiceExecution serviceExecution = service.newExecution().insert(flow).insert(node);

      // Mapping input
      Map<String, Serializable> serviceInput = new HashMap<>();
      for(Map.Entry<String, String> input : node.getBody().getInputs().entrySet()) {
        String ref = input.getValue();
        Serializable value;
        if(flow.getContext().getVariables().containsKey(ref)) {
          value = flow.getContext().getVariables().get(ref);
        } else {
          value = variableResolver.getVariableOnPath(ref, tasks);
        }
        serviceInput.put(input.getKey(), value);
      }

      try(ServiceResponse response = serviceExecution.insert(new FlowTaskInput(serviceInput)).run()) {
        task
          .putInputs(serviceInput)
          .putVariables(createVariables(flow, task, node.getBody(), response.get()));
      } catch(FlowTaskExecutorException e) {
        LOGGER.error(e.getMessage(), e);
        final var rootCause = ExceptionUtils.getRootCause(e);
        final var rootMsg = ExceptionUtils.getRootCauseStackTrace(rootCause);
        List<Message> messages = new ArrayList<>(); 
        for(final var trace : rootMsg) {
          if(trace.contains("resys")) {
            var msg = new Message("trace", trace);
            messages.add(msg);
          }
          
        }
        
        final var result = new MessageList().setStatus(422)
            .addAll(messages)
            .add(new Message(flow.getModel().getId()+ "/" + task.getModelId(), rootCause.getMessage()));
        throw new DataException(result);
      } catch(RuntimeException e) {
        LOGGER.error(e.getMessage(), e);
        throw e;
      } catch(Exception e) {
        LOGGER.error(e.getMessage(), e);
        throw new RuntimeException(e.getMessage(), e);
      }

    } catch(Exception e) {
      LOGGER.error(e.getMessage(), e);
      throw e;
    }
    return super.execute(flow, task);
  }

  @SuppressWarnings("rawtypes")
  protected Map<String, Serializable> createVariables(Flow flow, FlowTask task, FlowTaskValue taskValue, Object outputs) {
    final Serializable value;
    if(taskValue.isCollection()) {
      List<Serializable> entities = new ArrayList<>();
      for(Object out : (List) outputs) {
        entities.add((Serializable) out);
      }
      value = (Serializable) entities;
    } else {
      value = (Serializable) outputs;
    }

    Map<String, Serializable> variables = new HashMap<>();
    variables.put(task.getModelId(), value);
    return variables;
  }
}
