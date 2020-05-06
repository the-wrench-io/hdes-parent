package io.resys.hdes.decisiontable.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.resys.hdes.decisiontable.api.DecisionTableAst;
import io.resys.hdes.decisiontable.api.DecisionTableExecution;
import io.resys.hdes.decisiontable.api.DecisionTableFlatModel;
import io.resys.hdes.decisiontable.api.DecisionTableModel.HitPolicy;
import io.resys.hdes.decisiontable.api.DecisionTableService;
import io.resys.hdes.decisiontable.tests.config.TestDtConfig;

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


public class DtTest {

  private DecisionTableService decisionTableRepository = TestDtConfig.decisionTableRepository;

  @Test
  public void readerNodeOrderTest() throws IOException {
    DecisionTableFlatModel flatDecisionTable = TestDtConfig.dataTypeService.read().classpath("dt.json").build(DecisionTableFlatModel.class);
    DecisionTableAst decisionTable = decisionTableRepository
      .ast().from(flatDecisionTable).build();

    DecisionTableAst.Node node = decisionTable.getNode();
    assertEquals(0, node.getId());
    assertEquals(null, node.getPrevious());

    assertEquals(1, node.getNext().getId());
    assertEquals(node, node.getNext().getPrevious());

    assertEquals(2, node.getNext().getNext().getId());
    assertEquals(3, node.getNext().getNext().getNext().getId());
    assertEquals(4, node.getNext().getNext().getNext().getNext().getId());

    assertEquals(3, node.getNext().getNext().getNext().getNext().getPrevious().getId());
    assertEquals(null, node.getNext().getNext().getNext().getNext().getNext());
  }

  @Test
  public void executionTest() throws IOException {
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
    Map<String, Serializable> values = new HashMap<>();
    values.put("sriBoolean", false);
    values.put("risk", "CAREFUL");
    values.put("sri", 1);
    values.put("sriDate", LocalDate.parse("2017-07-03"));

    DecisionTableFlatModel flatDecisionTable = TestDtConfig.dataTypeService.read()
        .classpath("dt.json").build(DecisionTableFlatModel.class).setHitPolicy(HitPolicy.ALL);
    DecisionTableAst decisionTable = decisionTableRepository.ast().from(flatDecisionTable).build();

    List<DecisionTableExecution> result = decisionTableRepository.execution().
      ast(decisionTable).
      input((type) -> values.get(type.getName())).
      build().blockingGet().getValue();

    assertEquals(2, result.size());
    assertEquals(0, result.get(0).getNode().getId());
    assertEquals(2, result.get(1).getNode().getId());
  }

  @Test
  public void nullEqualsNull() throws IOException {
    Map<String, Serializable> values = new HashMap<>();
    values.put("risk", null);

    DecisionTableFlatModel flatDecisionTable = TestDtConfig.dataTypeService.read().classpath("nullEqualsNull.json").build(DecisionTableFlatModel.class);
    DecisionTableAst decisionTable = decisionTableRepository.ast().from(flatDecisionTable).build();

    List<DecisionTableExecution> result = decisionTableRepository.execution().
      ast(decisionTable).
      input((type) -> values.get(type.getName())).
      build().blockingGet().getValue();

    assertEquals(1, result.size());
  }
}
