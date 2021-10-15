package io.resys.hdes.client.test;

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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.resys.hdes.client.api.ast.AstFlow;
import io.resys.hdes.client.api.ast.AstFlow.AstFlowNode;
import io.resys.hdes.client.api.ast.AstFlow.FlowAstCommandMessage;
import io.resys.hdes.client.api.programs.FlowProgram.FlowExecutionStatus;
import io.resys.hdes.client.api.programs.FlowProgram.FlowResult;
import io.resys.hdes.client.api.programs.FlowProgram.FlowResultLog;
import io.resys.hdes.client.spi.util.FileUtils;


public class FlowTest {

  @Test
  public void astIndentNormal() throws IOException {
    
    final var ast = TestUtils.client.astTypes().flow()
        .srcAdd(1, "id: uber flow")
        .srcAdd(2, "description: uber description")
        .srcAdd(3, "tasks:")
        .srcAdd(4, "  - first task:")
        .srcAdd(5, "  - second task:")
        .build();

    AstFlowNode node = ast.getSrc();
    List<FlowAstCommandMessage> messages = ast.getMessages();
    
    Assertions.assertTrue(messages.isEmpty());

    Assertions.assertEquals(3, node.getChildren().size());

    Assertions.assertNotNull(node.get("id"));
    Assertions.assertEquals("uber flow", node.get("id").getValue());

    Assertions.assertNotNull(node.get("description"));
    Assertions.assertEquals("uber description", node.get("description").getValue());

    Assertions.assertNotNull(node.get("tasks"));
    Assertions.assertEquals(2, node.get("tasks").getChildren().size());

    Assertions.assertNotNull(node.get("tasks").get("first task"));
    Assertions.assertNotNull(node.get("tasks").get("second task"));
  }


  @Test
  public void astDeleteId() throws IOException {
    List<FlowAstCommandMessage> messages = new ArrayList<>();
    final var ast = TestUtils.client.astTypes().flow()
        .srcAdd(1, "id: uber flow")
        .srcAdd(2, "description: uber description")
        .srcAdd(3, "tasks:")
        .srcAdd(4, "  - first task:")
        .srcAdd(5, "  - second task:")
        .srcDel(1)
        .srcDel(4)
        .build();
    
    AstFlowNode node = ast.getSrc();
    
    Assertions.assertTrue(messages.isEmpty());
    Assertions.assertEquals("uber description", node.get("description").getValue());
    Assertions.assertNotNull(node.get("tasks"));
    Assertions.assertEquals(1, node.get("tasks").getChildren().size());
    Assertions.assertNotNull(node.get("tasks").get("first task"));
  }

  @Test
  public void astDeleteAndSetId() throws IOException {
    List<FlowAstCommandMessage> messages = new ArrayList<>();
    final var ast = TestUtils.client.astTypes().flow()
        .srcAdd(1, "id: uber flow")
        .srcAdd(2, "description: uber description")
        .srcAdd(3, "tasks:")
        .srcAdd(4, "  - first task:")
        .srcAdd(5, "  - second task:")
        .srcDel(1)
        .srcAdd(1, "id: uber flow")
        .build();
    
    AstFlowNode node = ast.getSrc();

    Assertions.assertTrue(messages.isEmpty());
    Assertions.assertEquals(3, node.getChildren().size());
    Assertions.assertNotNull(node.get("id"));
    Assertions.assertEquals("uber flow", node.get("id").getValue());
    Assertions.assertNotNull(node.get("description"));
    Assertions.assertEquals("uber description", node.get("description").getValue());

    Assertions.assertNotNull(node.get("tasks"));
    Assertions.assertEquals(2, node.get("tasks").getChildren().size());
    Assertions.assertNotNull(node.get("tasks").get("first task"));
    Assertions.assertNotNull(node.get("tasks").get("second task"));
  }
  
  @Test
  public void astTrafficExample() throws IOException {
    InputStream stream = FileUtils.toInputStream(getClass(), "flow/trafficMain.in.json");
    String content = TestUtils.objectMapper.readValue(stream, ObjectNode.class).get("content").asText();

    ArrayNode commands = TestUtils.objectMapper.readValue(content, ArrayNode.class);
    AstFlow flowCommandModel = TestUtils.client.astTypes().flow().src(commands).build();

    String expected = FileUtils.toString(getClass(), "flow/trafficMain.out.yaml");
    Assertions.assertEquals(expected, flowCommandModel.getSrc().getValue());
  }
  
  
  @Test
  public void programAmlTest() throws IOException {
    final var syntax = FileUtils.toInputStream(getClass(), "flow/aml-flow.yaml");
    final var ast = TestUtils.client.ast().syntax(syntax).flow();
    final var program = TestUtils.client.program().ast(ast);

    FlowResult flow = TestUtils.client.executor()
        .inputField("whitelist", false)
        .inputField("param1", 1)
        .flow(program).andGetBody();

    // Assert till first form
    Assertions.assertEquals(FlowExecutionStatus.COMPLETED, flow.getStatus());
    Assertions.assertEquals("resolveAmlViolation-MERGE", flow.getStepId());
    Assertions.assertEquals("[addPartyToInvestigationList, resolveAmlViolation, resolveAmlViolation-MERGE]", flow.getShortHistory());

    // Assert tasks
    Assertions.assertEquals(3, flow.getLogs().size());
    Assertions.assertEquals(1, flow.getLogs().stream().filter(t -> t.getStepId().equals("resolveAmlViolation")).count());

    FlowResultLog task = flow.getLogs().stream().filter(t -> t.getStepId().equals("resolveAmlViolation")).findFirst().get();
    Assertions.assertNotNull(task);
    
    flow = TestUtils.client.executor()
        .inputField("whitelist", true)
        .inputField("param1", 1)
        .flow(program).andGetBody();
    
    // Flow should be completed
    Assertions.assertEquals("[addPartyToInvestigationList, resolveAmlViolation, resolveAmlViolation-MERGE, resolveAmlViolation-EXCLUSIVE, addToWhitelist, rmInvList, end]", 
        flow.getShortHistory());
  }

  @Test
  public void programSelfRefTest() throws IOException {
    final var syntax = FileUtils.toInputStream(getClass(), "flow/self-ref.yaml");
    final var ast = TestUtils.client.ast().syntax(syntax).flow();
    final var program = TestUtils.client.program().ast(ast);
    FlowResult flow = TestUtils.client.executor()
        .inputField("whitelist", true)
        .flow(program).andGetBody();
    
    Assertions.assertEquals("[Add party to investigation list, Resolve aml violation, Resolve aml violation-EXCLUSIVE, addToWhitelist, rmInvList, end]", flow.getShortHistory());

  }

}
