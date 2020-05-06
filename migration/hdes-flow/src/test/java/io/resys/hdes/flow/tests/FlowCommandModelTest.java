package io.resys.hdes.flow.tests;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.resys.hdes.datatype.api.DataTypeCommand;
import io.resys.hdes.flow.api.FlowModel;
import io.resys.hdes.flow.tests.config.TestFlowConfig;

public class FlowCommandModelTest {

  @Test
  public void indentNormal() throws IOException {
    FlowModel.Root node = TestFlowConfig.builder()
        .add("id: uber flow")
        .add("description: uber description")
        .add("tasks:")
        .add("  - first task:")
        .add("  - second task:")
        .model();

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
    FlowModel.Root node = TestFlowConfig.builder()
        .add("id: uber flow")
        .add("description: uber description")
        .add("tasks:")
        .add("  - first task:")
        .add("  - second task:")
        .delete(1)
        .delete(4)
        .model();
    Assertions.assertEquals("uber description", node.get("description").getValue());
    Assertions.assertNotNull(node.get("tasks"));
    Assertions.assertEquals(1, node.get("tasks").getChildren().size());
    Assertions.assertNotNull(node.get("tasks").get("first task"));
  }

  @Test
  public void deleteAndSetId() throws IOException {
    FlowModel.Root node = TestFlowConfig.builder()
        .add("id: uber flow")
        .add("description: uber description")
        .add("tasks:")
        .add("  - first task:")
        .add("  - second task:")
        .delete(1)
        .add(1, "id: uber flow")
        .model();

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
  public void assets() {
    String content = TestFlowConfig.DATA_TYPE_SERVICE.read()
        .classpath("trafficMain.in.json")
        .build(ObjectNode.class)
        .get("content").asText();
    
    List<DataTypeCommand> commands = TestFlowConfig.DATA_TYPE_SERVICE.read().src(content).list(DataTypeCommand.class);
    FlowModel.Root model = TestFlowConfig.builder().add(commands).model();

    String expected = FileUtils.toString("trafficMain.out.yaml");
    Assertions.assertEquals(expected, model.getValue());
  }
}
