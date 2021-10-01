package io.resys.wrench.assets.flow.spi;

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
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import io.resys.hdes.client.api.ast.FlowAstType.FlowCommandMessage;
import io.resys.hdes.client.api.ast.FlowAstType.Node;
import io.resys.wrench.assets.flow.api.FlowAstFactory;
import io.resys.wrench.assets.flow.spi.config.TestFlowConfig;

@RunWith(BlockJUnit4ClassRunner.class)
public class FlowCommandModelTest {

  private FlowAstFactory nodeRepository = TestFlowConfig.nodeRepository();

  @Test
  public void indentNormal() throws IOException {
    List<FlowCommandMessage> messages = new ArrayList<>();
    Node node = nodeRepository.create(m -> messages.add(m))
        .add(1, "id: uber flow")
        .add(2, "description: uber description")
        .add(3, "tasks:")
        .add(4, "  - first task:")
        .add(5, "  - second task:")
        .build();

    Assert.assertTrue(messages.toString(), messages.isEmpty());

    Assert.assertEquals(3, node.getChildren().size());

    Assert.assertNotNull(node.get("id"));
    Assert.assertEquals("uber flow", node.get("id").getValue());

    Assert.assertNotNull(node.get("description"));
    Assert.assertEquals("uber description", node.get("description").getValue());

    Assert.assertNotNull(node.get("tasks"));
    Assert.assertEquals(2, node.get("tasks").getChildren().size());

    Assert.assertNotNull(node.get("tasks").get("first task"));
    Assert.assertNotNull(node.get("tasks").get("second task"));
  }


  @Test
  public void deleteId() throws IOException {
    List<FlowCommandMessage> messages = new ArrayList<>();
    Node node = nodeRepository.create(m -> messages.add(m))
        .add(1, "id: uber flow")
        .add(2, "description: uber description")
        .add(3, "tasks:")
        .add(4, "  - first task:")
        .add(5, "  - second task:")
        .delete(1)
        .delete(4)
        .build();
    Assert.assertTrue(messages.toString(), messages.isEmpty());
    Assert.assertEquals("uber description", node.get("description").getValue());
    Assert.assertNotNull(node.get("tasks"));
    Assert.assertEquals(1, node.get("tasks").getChildren().size());
    Assert.assertNotNull(node.get("tasks").get("first task"));
  }

  @Test
  public void deleteAndSetId() throws IOException {
    List<FlowCommandMessage> messages = new ArrayList<>();
    Node node = nodeRepository.create(m -> messages.add(m))
        .add(1, "id: uber flow")
        .add(2, "description: uber description")
        .add(3, "tasks:")
        .add(4, "  - first task:")
        .add(5, "  - second task:")
        .delete(1)
        .add(1, "id: uber flow")
        .build();

    Assert.assertTrue(messages.toString(), messages.isEmpty());
    Assert.assertEquals(3, node.getChildren().size());
    Assert.assertNotNull(node.get("id"));
    Assert.assertEquals("uber flow", node.get("id").getValue());
    Assert.assertNotNull(node.get("description"));
    Assert.assertEquals("uber description", node.get("description").getValue());

    Assert.assertNotNull(node.get("tasks"));
    Assert.assertEquals(2, node.get("tasks").getChildren().size());
    Assert.assertNotNull(node.get("tasks").get("first task"));
    Assert.assertNotNull(node.get("tasks").get("second task"));
  }
}
