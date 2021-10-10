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

import java.time.LocalDateTime;

import io.resys.hdes.client.api.programs.FlowResult.FlowHistory;

public class FlowHistoryBean implements FlowHistory {

  private static final long serialVersionUID = -8121017016883667984L;
  private String id;
  private String modelId;
  private LocalDateTime start;
  private LocalDateTime end;

  @Override
  public String getId() {
    return id;
  }
  public FlowHistoryBean setId(String id) {
    this.id = id;
    return this;
  }
  @Override
  public LocalDateTime getStart() {
    return start;
  }
  public FlowHistoryBean setStart(LocalDateTime start) {
    this.start = start;
    return this;
  }
  @Override
  public LocalDateTime getEnd() {
    return end;
  }
  @Override
  public FlowHistoryBean setEnd(LocalDateTime end) {
    this.end = end;
    return this;
  }
  @Override
  public String getModelId() {
    return modelId;
  }
  public FlowHistoryBean setModelId(String modelId) {
    this.modelId = modelId;
    return this;
  }
}
