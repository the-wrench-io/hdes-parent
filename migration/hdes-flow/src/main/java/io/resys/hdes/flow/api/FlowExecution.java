package io.resys.hdes.flow.api;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

import org.immutables.value.Value;

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

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.reactivex.annotations.Nullable;
import io.resys.hdes.execution.HdesService;
import io.resys.hdes.flow.api.FlowExecution.ExecutionTask;


@Value.Immutable
public abstract class FlowExecution implements HdesService.Execution<ExecutionTask> {
  private static final long serialVersionUID = -5273381639549472475L;

  public abstract FlowAst getAst();
  
  public abstract Map<String, Serializable> getInput();
  
  @JsonIgnore
  public String getTasksLog() {
    StringBuilder result = new StringBuilder();
    getValue().forEach(t -> result.append(t.getId()).append("->"));
    return result.toString();
  }

  @JsonIgnore
  public Optional<ExecutionTask> getTask(String id) {
    return getValue().stream().filter(t -> t.getId().equalsIgnoreCase(id)).findFirst();
  }

  @JsonIgnore
  public Optional<ExecutionTask> getLastTask() {
    if (getValue().isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(getValue().get(getValue().size() - 1));
  }

  @Value.Immutable
  public interface ExecutionTask extends HdesService.ExecutionValue {
    String getId();

    ExecutionStatus getStatus();

    @Nullable
    String getNextTaskId();
  }

  public enum ExecutionStatus {
    ENDED, OPEN, SUSPENDED
  }
}