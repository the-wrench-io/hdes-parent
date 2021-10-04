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

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import io.resys.hdes.client.api.execution.Flow;
import io.resys.hdes.client.api.model.FlowModel;
import io.resys.hdes.client.api.model.FlowModel.FlowTaskModel;
import io.resys.hdes.client.spi.util.Assert;
import io.resys.wrench.assets.flow.spi.log.FlowLogger;

public class GenericFlow implements Flow {

  private static final long serialVersionUID = -6048886689681204156L;

  private final String id;
  private final FlowModel model;
  private final FlowContext context;
  private final Clock clock;

  public GenericFlow(String id, FlowModel model, FlowContext context, Clock clock) {
    super();
    this.id = id;
    this.model = model;
    this.context = context;
    this.clock = clock;
  }
  @Override
  public String getId() {
    return id;
  }
  @Override
  public FlowModel getModel() {
    return model;
  }
  @Override
  public FlowContext getContext() {
    return context;
  }
  @Override
  public FlowTask start(FlowTaskModel model) {
    Optional<FlowTask> openFlowTask = context.getTasks(model.getId()).stream().filter(t -> t.getStatus() == FlowTaskStatus.OPEN).findFirst();
    if(openFlowTask.isPresent()) {
      return openFlowTask.get();
    }

    FlowTaskBean bean = new FlowTaskBean()
        .setId(String.valueOf(context.getTasks().size() + 1))
        .setModelId(model.getId())
        .setStatus(FlowTaskStatus.OPEN);

    context
    .setPointer(model.getId())
    .addTask(bean)
    .addHistory(new FlowHistoryBean()
        .setId(bean.getId())
        .setModelId(model.getId())
        .setStart(now()));


    return bean;
  }
  @Override
  public FlowTask complete(FlowTask task) {
    Assert.isTrue(task.getStatus() == FlowTaskStatus.OPEN, () -> "Flow task: \"" + id + "\" status must be OPEN but was: " + task.getStatus() + "!");
    context.getHistory(task.getId()).setEnd(now());
    task.setStatus(FlowTaskStatus.COMPLETED);
    return task;
  }
  @Override
  public FlowTask suspend(FlowTask task) {
    context.setStatus(FlowStatus.SUSPENDED);
    return task;
  }
  protected LocalDateTime now() {
    return LocalDateTime.now(clock);
  }
  @Override
  public FlowTask end(FlowTask task) {
    Assert.isTrue(task.getStatus() == FlowTaskStatus.COMPLETED, () -> "Flow task: \"" + id + "\" status must be COMPLETED but was: " + task.getStatus() + "!");
    context.setStatus(FlowStatus.ENDED).setPointer(null);
    return task;
  }
  @Override
  public Map<String, Object> getLog() {
    return new FlowLogger().flow(this).build();
  }
}
