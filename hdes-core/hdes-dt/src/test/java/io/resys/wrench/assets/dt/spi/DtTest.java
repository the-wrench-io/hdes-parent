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
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import io.resys.hdes.client.api.execution.DecisionProgram;
import io.resys.hdes.client.api.execution.DecisionProgram.Row;
import io.resys.hdes.client.api.execution.DecisionResult;
import io.resys.hdes.client.spi.util.DateParser;
import io.resys.hdes.client.spi.util.FileUtils;
import io.resys.wrench.assets.dt.api.DecisionTableRepository;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.DecisionTableFormat;
import io.resys.wrench.assets.dt.spi.config.TestDtConfig;

@RunWith(BlockJUnit4ClassRunner.class)
public class DtTest {

  private DecisionTableRepository decisionTableRepository = TestDtConfig.decisionTableRepository();

  @Test
  public void readerNodeOrderTest() throws IOException {
    InputStream stream = FileUtils.toInputStream(getClass(), "dt.json");

    DecisionProgram decisionTable = decisionTableRepository.createBuilder().format(DecisionTableFormat.JSON).src(stream).build();

    List<Row> rows = decisionTable.getRows();
    Assert.assertEquals(0, rows.get(0).getOrder());
    Assert.assertEquals(1, rows.get(1).getOrder());
    Assert.assertEquals(2, rows.get(2).getOrder());
    Assert.assertEquals(3, rows.get(3).getOrder());
    Assert.assertEquals(4, rows.get(4).getOrder());
    Assert.assertEquals(5, rows.size());
  }

  @Test
  public void executionTest() throws IOException {
    InputStream stream = FileUtils.toInputStream(getClass(), "dt.json");

    DecisionProgram decisionTable = decisionTableRepository.createBuilder().format(DecisionTableFormat.JSON).src(stream).build();

    Map<String, Object> values = new HashMap<>();
    values.put("sriBoolean", false);
    values.put("risk", "CAREFUL");
    values.put("sri", 1);
    values.put("sriDate", DateParser.parseLocalDate("2017-07-03"));

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


    DecisionResult result = decisionTableRepository.createExecutor().
        decisionTable(decisionTable).
        context((type) -> values.get(type.getName())).
        execute();

    Assert.assertEquals(2, result.getMatches().size());
    Assert.assertEquals(0, result.getMatches().get(0).getNode().getOrder());
    Assert.assertEquals(2, result.getMatches().get(1).getNode().getOrder());
  }

  @Test
  public void nullEqualsNull() throws IOException {
    InputStream stream = FileUtils.toInputStream(getClass(), "nullEqualsNull.json");

    DecisionProgram decisionTable = decisionTableRepository.createBuilder().format(DecisionTableFormat.JSON).src(stream).build();

    Map<String, Object> values = new HashMap<>();
    values.put("risk", null);

    DecisionResult result = decisionTableRepository.createExecutor().
        decisionTable(decisionTable).
        context((type) -> values.get(type.getName())).
        execute();

    Assert.assertEquals(1, result.getMatches().size());
  }
}
