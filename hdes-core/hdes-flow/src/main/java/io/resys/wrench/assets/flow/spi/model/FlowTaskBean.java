package io.resys.wrench.assets.flow.spi.model;

/*-
 * #%L
 * wrench-component-flow
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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.resys.wrench.assets.flow.api.model.Flow.FlowTask;
import io.resys.wrench.assets.flow.api.model.Flow.FlowTaskStatus;

public class FlowTaskBean implements FlowTask {

  private static final long serialVersionUID = -323649458991224778L;

  private String id;
  private String modelId;
  private FlowTaskStatus status;
  private Map<String, Serializable> inputs = new HashMap<>();
  private Map<String, Serializable> variables = new HashMap<>();

  @Override
  public String getId() {
    return id;
  }
  @Override
  public FlowTaskStatus getStatus() {
    return status;
  }
  @Override
  public FlowTaskBean setStatus(FlowTaskStatus status) {
    this.status = status;
    return this;
  }
  public FlowTaskBean setId(String id) {
    this.id = id;
    return this;
  }
  @Override
  public String getModelId() {
    return modelId;
  }
  public FlowTaskBean setModelId(String modelId) {
    this.modelId = modelId;
    return this;
  }
  @Override
  public Map<String, Serializable> getVariables() {
    return Collections.unmodifiableMap(variables);
  }
  @Override
  public FlowTask putVariables(Map<String, Serializable> variable) {
    this.variables.putAll(variable);
    return this;
  }
  @Override
  public FlowTask putInputs(Map<String, Serializable> inputs) {
    this.inputs.putAll(inputs);
    return this;
  }
  @Override
  public Map<String, Serializable> getInputs() {
    return Collections.unmodifiableMap(inputs);
  }
}
