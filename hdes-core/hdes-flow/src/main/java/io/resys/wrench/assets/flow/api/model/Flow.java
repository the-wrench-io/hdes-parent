package io.resys.wrench.assets.flow.api.model;

/*-
 * #%L
 * wrench-assets-flow
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
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

import io.resys.wrench.assets.flow.api.model.FlowModel.FlowTaskModel;



public interface Flow extends Serializable {
  String getId();
  FlowModel getModel();
  FlowContext getContext();
  Map<String, Object> getLog();

  FlowTask start(FlowTaskModel model);
  FlowTask suspend(FlowTask task);
  FlowTask complete(FlowTask task);
  FlowTask end(FlowTask task);

  interface FlowContext extends Serializable {
    FlowStatus getStatus();
    FlowContext setStatus(FlowStatus status);

    String getPointer();
    FlowContext setPointer(String taskModelId);

    Collection<FlowHistory> getHistory();
    String getShortHistory();
    FlowHistory getHistory(String taskId);
    FlowContext addHistory(FlowHistory history);

    FlowTask getTask(String taskId);
    Collection<FlowTask> getTasks();
    Collection<FlowTask> getTasks(String modelId);
    FlowContext addTask(FlowTask task);

    FlowContext putVariable(String name, Serializable value);
    Map<String, Serializable> getVariables();
  }

  interface FlowHistory extends Serializable {
    String getId();
    String getModelId();
    LocalDateTime getStart();
    LocalDateTime getEnd();
    FlowHistory setEnd(LocalDateTime end);
  }

  interface FlowTask extends Serializable {
    String getId();
    String getModelId();
    FlowTaskStatus getStatus();
    FlowTask setStatus(FlowTaskStatus status);
    Map<String, Serializable> getVariables();
    Map<String, Serializable> getInputs();
    FlowTask putInputs(Map<String, Serializable> inputs);
    FlowTask putVariables(Map<String, Serializable> variable);
  }

  public enum FlowTaskStatus {
    OPEN, COMPLETED
  }
  public enum FlowStatus {
    CREATED, RUNNING, SUSPENDED, ENDED
  }
}
