package io.resys.wrench.assets.flow.spi;

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

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.client.api.programs.FlowProgram;
import io.resys.hdes.client.api.programs.FlowResult;
import io.resys.hdes.client.api.programs.FlowResult.FlowStatus;
import io.resys.hdes.client.api.programs.FlowResult.FlowTask;
import io.resys.hdes.client.spi.util.FileUtils;
import io.resys.wrench.assets.flow.api.FlowRepository;
import io.resys.wrench.assets.flow.spi.config.TestFlowConfig;
import io.resys.wrench.assets.flow.spi.support.FlowFlatToCommandBuilder;

@RunWith(BlockJUnit4ClassRunner.class)
public class FlowReaderTest {

  private FlowRepository flowRepository = TestFlowConfig.flowRepository();
  private ObjectMapper objectMapper = TestFlowConfig.objectMapper();

  @Test
  public void amlTest() throws IOException {
    String content = new FlowFlatToCommandBuilder(objectMapper).build(FileUtils.toInputStream(getClass(), "aml-flow.yaml"));

    FlowProgram flowMetamodel = flowRepository.createModel().content(content).build().getValue();
//    FlowModel flowMetamodel = flowMetamodelRepository.createBuilder().format(FlowFormat.JSON).stream(context.getResource("classpath:aml-flow.yaml").getInputStream()).build();
    Assert.assertNotNull(flowMetamodel);

    FlowResult flow = flowRepository.createExecutor().insert("whitelist", true).run(flowMetamodel);

    // Assert till first form
    Assert.assertEquals(FlowStatus.SUSPENDED, flow.getContext().getStatus());
    Assert.assertEquals("resolveAmlViolation-MERGE", flow.getContext().getPointer());
    Assert.assertEquals("[addPartyToInvestigationList, resolveAmlViolation, resolveAmlViolation-MERGE]", flow.getContext().getShortHistory());

    // Assert tasks
    Assert.assertEquals(3, flow.getContext().getTasks().size());
    Assert.assertEquals(1, flow.getContext().getTasks("resolveAmlViolation").size());

    FlowTask task = flow.getContext().getTasks("resolveAmlViolation").iterator().next();
    flowRepository.createTaskBuilder().complete(flow, task.getId());

    // Flow should be completed
    Assert.assertEquals("[addPartyToInvestigationList, resolveAmlViolation, resolveAmlViolation-MERGE, resolveAmlViolation-EXCLUSIVE, addToWhitelist, rmInvList, end]", flow.getContext().getShortHistory());
  }

  @Test
  public void selfRefTest() throws IOException {
    String content = new FlowFlatToCommandBuilder(objectMapper).build(FileUtils.toInputStream(getClass(), "self-ref.yaml"));


    FlowProgram flowMetamodel = flowRepository.createModel().content(content).build().getValue();
    Assert.assertNotNull(flowMetamodel);

    //FlowRepository.Flow flow = flowRepository.createBuilder().insert("whitelist", true).run("self ref");

    // Flow should be completed
    // Assert.assertEquals("[Add party to investigation list, Resolve aml violation, Resolve aml violation-EXCLUSIVE, addToWhitelist, rmInvList, end]", flow.getContext().getHistory().toString());

  }
}
