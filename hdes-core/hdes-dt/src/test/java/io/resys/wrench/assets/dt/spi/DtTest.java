package io.resys.wrench.assets.dt.spi;

/*-
 * #%L
 * wrench-component-assets-Dt
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
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import io.resys.hdes.client.api.execution.DecisionTableResult;
import io.resys.hdes.client.api.model.DecisionTable;
import io.resys.hdes.client.api.model.DecisionTable.DecisionTableNode;
import io.resys.wrench.assets.datatype.spi.util.FileUtils;
import io.resys.wrench.assets.dt.api.DecisionTableRepository;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.DecisionTableFormat;
import io.resys.wrench.assets.dt.spi.config.TestDtConfig;

@RunWith(BlockJUnit4ClassRunner.class)
public class DtTest {

  private DecisionTableRepository decisionTableRepository = TestDtConfig.decisionTableRepository();

  @Test
  public void readerNodeOrderTest() throws IOException {
    InputStream stream = FileUtils.toInputStream(getClass(), "dt.json");

    DecisionTable decisionTable = decisionTableRepository.createBuilder().format(DecisionTableFormat.JSON).src(stream).build();

    DecisionTableNode node = decisionTable.getNode();
    Assert.assertEquals(0, node.getId());
    Assert.assertEquals(null, node.getPrevious());

    Assert.assertEquals(1, node.getNext().getId());
    Assert.assertEquals(node, node.getNext().getPrevious());

    Assert.assertEquals(2, node.getNext().getNext().getId());
    Assert.assertEquals(3, node.getNext().getNext().getNext().getId());
    Assert.assertEquals(4, node.getNext().getNext().getNext().getNext().getId());

    Assert.assertEquals(3, node.getNext().getNext().getNext().getNext().getPrevious().getId());
    Assert.assertEquals(null, node.getNext().getNext().getNext().getNext().getNext());
  }

  @Test
  public void executionTest() throws IOException {
    InputStream stream = FileUtils.toInputStream(getClass(), "dt.json");

    DecisionTable decisionTable = decisionTableRepository.createBuilder().format(DecisionTableFormat.JSON).src(stream).build();

    Map<String, Object> values = new HashMap<>();
    values.put("sriBoolean", false);
    values.put("risk", "CAREFUL");
    values.put("sri", 1);
    values.put("sriDate", ValueBuilder.parseLocalDate("2017-07-03"));

// Match 1
//   {
//      "id": "sriBoolean",
//      "value": "false"
//   },
//   {
//      "id": "risk",
//      "value": "in [\"CAREFUL\", \"NOT\"]"
//   },
//   {
//      "id": "sri",
//      "value": "[1..2]"
//   },
//   {
//      "id": "sriDate",
//      "value": "equals 2017-07-03T00:00:00"
//   },
//
// Match 3
//   {
//      "id": "sriBoolean",
//      "value": "false"
//   },
//   {
//      "id": "risk",
//      "value": "not in [\"MODERATE\"]"
//   },
//   {
//      "id": "sri",
//      "value": "[1..5]"
//   },
//   {
//      "id": "sriDate",
//      "value": "equals 2017-07-03T00:00:00"
//   }


    DecisionTableResult result = decisionTableRepository.createExecutor().
        decisionTable(decisionTable).
        context((type) -> values.get(type.getName())).
        execute();

    Assert.assertEquals(2, result.getMatches().size());
    Assert.assertEquals(0, result.getMatches().get(0).getNode().getId());
    Assert.assertEquals(2, result.getMatches().get(1).getNode().getId());
  }

  @Test
  public void nullEqualsNull() throws IOException {
    InputStream stream = FileUtils.toInputStream(getClass(), "nullEqualsNull.json");

    DecisionTable decisionTable = decisionTableRepository.createBuilder().format(DecisionTableFormat.JSON).src(stream).build();

    Map<String, Object> values = new HashMap<>();
    values.put("risk", null);

    DecisionTableResult result = decisionTableRepository.createExecutor().
        decisionTable(decisionTable).
        context((type) -> values.get(type.getName())).
        execute();

    Assert.assertEquals(1, result.getMatches().size());
  }
}
