package io.resys.wrench.assets.flow.api;

import io.resys.hdes.client.api.programs.FlowResult;
import io.resys.hdes.client.api.programs.FlowProgram.FlowTaskType;
import io.resys.hdes.client.api.programs.FlowProgram.Step;
import io.resys.hdes.client.api.programs.FlowResult.FlowTask;

public interface FlowExecutorRepository {

  FlowExecutor createExecutor();
  FlowTaskExecutor createTaskExecutor(FlowTaskType type);

  interface FlowExecutor {
    void execute(FlowResult flow);
  }

  interface FlowTaskExecutor {
    Step execute(FlowResult flow, FlowTask task);
  }
}
