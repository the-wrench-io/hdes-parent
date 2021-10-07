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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.resys.hdes.client.api.HdesAstTypes;
import io.resys.hdes.client.api.ast.AstFlow;
import io.resys.hdes.client.api.ast.AstFlow.FlowAstCommandMessage;
import io.resys.hdes.client.api.ast.AstFlow.FlowAstNode;
import io.resys.hdes.client.spi.HdesAstTypesImpl;
import io.resys.hdes.client.spi.util.FileUtils;


public class FlowAstTest {

  private static ObjectMapper objectMapper = new ObjectMapper();
  private static HdesAstTypes nodeRepository = new HdesAstTypesImpl(objectMapper);


  @Test
  public void indentNormal() throws IOException {
    
    final var ast = nodeRepository.flow()
        .srcAdd(1, "id: uber flow")
        .srcAdd(2, "description: uber description")
        .srcAdd(3, "tasks:")
        .srcAdd(4, "  - first task:")
        .srcAdd(5, "  - second task:")
        .build();

    FlowAstNode node = ast.getSrc();
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
  public void deleteId() throws IOException {
    List<FlowAstCommandMessage> messages = new ArrayList<>();
    final var ast = nodeRepository.flow()
        .srcAdd(1, "id: uber flow")
        .srcAdd(2, "description: uber description")
        .srcAdd(3, "tasks:")
        .srcAdd(4, "  - first task:")
        .srcAdd(5, "  - second task:")
        .srcDel(1)
        .srcDel(4)
        .build();
    
    FlowAstNode node = ast.getSrc();
    
    Assertions.assertTrue(messages.isEmpty());
    Assertions.assertEquals("uber description", node.get("description").getValue());
    Assertions.assertNotNull(node.get("tasks"));
    Assertions.assertEquals(1, node.get("tasks").getChildren().size());
    Assertions.assertNotNull(node.get("tasks").get("first task"));
  }

  @Test
  public void deleteAndSetId() throws IOException {
    List<FlowAstCommandMessage> messages = new ArrayList<>();
    final var ast = nodeRepository.flow()
        .srcAdd(1, "id: uber flow")
        .srcAdd(2, "description: uber description")
        .srcAdd(3, "tasks:")
        .srcAdd(4, "  - first task:")
        .srcAdd(5, "  - second task:")
        .srcDel(1)
        .srcAdd(1, "id: uber flow")
        .build();
    
    FlowAstNode node = ast.getSrc();

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
  public void assets() throws IOException {
    InputStream stream = FileUtils.toInputStream(getClass(), "trafficMain.in.json");
    String content = objectMapper.readValue(stream, ObjectNode.class).get("content").asText();

    ArrayNode commands = objectMapper.readValue(content, ArrayNode.class);
    AstFlow flowCommandModel = nodeRepository.flow().src(commands).build();

    String expected = FileUtils.toString(getClass(), "trafficMain.out.yaml");
    Assertions.assertEquals(expected, flowCommandModel.getSrc().getValue());
  }

}
