package io.resys.wrench.assets.flow.api;

/*-
 * #%L
 * hdes-flow
 * %%
 * Copyright (C) 2020 - 2021 Copyright 2020 ReSys OÜ
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

import io.resys.hdes.client.api.execution.Flow;
import io.resys.hdes.client.api.execution.Flow.FlowTask;
import io.resys.hdes.client.api.model.FlowModel.FlowTaskModel;
import io.resys.hdes.client.api.model.FlowModel.FlowTaskType;

public interface FlowExecutorRepository {

  FlowExecutor createExecutor();
  FlowTaskExecutor createTaskExecutor(FlowTaskType type);

  interface FlowExecutor {
    void execute(Flow flow);
  }

  interface FlowTaskExecutor {
    FlowTaskModel execute(Flow flow, FlowTask task);
  }
}
