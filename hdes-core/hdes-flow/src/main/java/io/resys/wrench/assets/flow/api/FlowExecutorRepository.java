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

import io.resys.hdes.client.api.programs.FlowResult;
import io.resys.hdes.client.api.programs.FlowProgram.FlowProgramStepType;
import io.resys.hdes.client.api.programs.FlowProgram.FlowProgramStep;
import io.resys.hdes.client.api.programs.FlowResult.FlowTask;

public interface FlowExecutorRepository {

  FlowExecutor createExecutor();
  FlowTaskExecutor createTaskExecutor(FlowProgramStepType type);

  interface FlowExecutor {
    void execute(FlowResult flow);
  }

  interface FlowTaskExecutor {
    FlowProgramStep execute(FlowResult flow, FlowTask task);
  }
}
