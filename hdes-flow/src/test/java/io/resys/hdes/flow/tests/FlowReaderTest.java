package io.resys.hdes.flow.tests;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.resys.hdes.datatype.api.ImmutableDataTypeCommand;
import io.resys.hdes.datatype.api.ImmutableDataTypeInputMap;

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

import io.resys.hdes.flow.api.FlowAst;
import io.resys.hdes.flow.api.FlowCommandType;
import io.resys.hdes.flow.api.FlowExecution;
import io.resys.hdes.flow.tests.config.TestFlowConfig;


public class FlowReaderTest {

  @Test
  public void amlTest() throws IOException {

    FlowAst flowMetamodel = TestFlowConfig.builder().add(
      ImmutableDataTypeCommand.builder().id(0)
        .type(FlowCommandType.SET_CONTENT.toString())
        .value(FileUtils.toString("aml-flow.yaml"))
        .build()
    ).ast();
    Assertions.assertNotNull(flowMetamodel);

    FlowExecution flow = TestFlowConfig.FLOW_SERVICE.execution()
      .from(flowMetamodel)
      .input(ImmutableDataTypeInputMap.builder().build())
      .id("aml flow")
      .build().blockingGet();

    // Assert till first form
    Assertions.assertEquals("addPartyToInvestigationList->resolveAmlViolation->resolveAmlViolation-MERGE->", flow.getTasksLog());
    Assertions.assertEquals("resolveAmlViolation-MERGE", flow.getLastTask().get().getId());
    Assertions.assertEquals(FlowExecution.ExecutionStatus.SUSPENDED, flow.getLastTask().get().getStatus());

    // Assert tasks
    Assertions.assertEquals(3, flow.getValue().size());
    Assertions.assertTrue(flow.getTask("resolveAmlViolation").isPresent());

    // Complete the user task and run the flow again
    flow = TestFlowConfig.FLOW_SERVICE.execution()
      .from(TestFlowConfig.FLOW_SERVICE
        .executionChange()
        .from(flow)
        .addInput("whitelist", true)
        .endTask("resolveAmlViolation")
        .build())
      .build().blockingGet();

    // Flow should be completed
    Assertions.assertEquals(
      "addPartyToInvestigationList->resolveAmlViolation->resolveAmlViolation-MERGE->resolveAmlViolation-MERGE->add transaction to source whitelist?->addToWhitelist->rmInvList->end->", flow.getTasksLog());
  }

  @Test
  public void selfRefTest() {
    FlowAst flowMetamodel = TestFlowConfig.builder().add(
      ImmutableDataTypeCommand.builder().id(0)
        .type(FlowCommandType.SET_CONTENT.toString())
        .value(FileUtils.toString("self-ref.yaml"))
        .build()
    ).ast();
    Assertions.assertNotNull(flowMetamodel);
  }

}
