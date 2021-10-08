package io.resys.wrench.assets.flow.api;

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

import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

import io.resys.hdes.client.api.execution.FlowResult;
import io.resys.hdes.client.api.execution.FlowProgram;
import io.resys.hdes.client.api.execution.FlowResult.FlowTask;

public interface FlowRepository {
  
  FlowModelBuilder createModel();
  FlowModelExporter createExporter();
  FlowModelExecutor createExecutor();
  FlowTaskBuilder createTaskBuilder();
  
  interface FlowTaskBuilder {
    FlowTask complete(FlowResult flow, String taskId);
    FlowTaskBuilder data(Map<String, Serializable> data);
  }
  
  interface FlowModelExecutor {
    FlowModelExecutor insert(String name, Serializable value);
    FlowResult run(FlowProgram model);
  }

  interface FlowModelExporter {
    FlowModelExporter src(FlowProgram model);
    String build();
  }


  interface FlowModelBuilder {
    FlowModelBuilder content(String input);
    FlowModelBuilder rename(Optional<String> rename);
    FlowModelBuilder dryRun();
    FlowModelBuilder stream(InputStream inputStream);
    Map.Entry<String, FlowProgram> build();
  }
}
