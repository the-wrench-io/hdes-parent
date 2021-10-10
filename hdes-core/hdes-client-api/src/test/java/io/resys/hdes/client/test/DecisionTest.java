package io.resys.hdes.client.test;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.client.api.HdesClient;
import io.resys.hdes.client.api.programs.DecisionProgram.DecisionResult;
import io.resys.hdes.client.api.programs.DecisionProgram.DecisionRow;
import io.resys.hdes.client.spi.HdesClientImpl;
import io.resys.hdes.client.spi.util.DateParser;
import io.resys.hdes.client.spi.util.FileUtils;

public class DecisionTest {

  private HdesClient client = HdesClientImpl.builder().objectMapper(new ObjectMapper()).build();

  @Test
  public void readerNodeOrderTest() throws IOException {
    final var ast = client.ast().commands(FileUtils.toString(getClass(), "decision/dt.json")).decision();
    final var decisionTable = client.program().ast(ast);

    List<DecisionRow> rows = decisionTable.getRows();
    Assertions.assertEquals(0, rows.get(0).getOrder());
    Assertions.assertEquals(1, rows.get(1).getOrder());
    Assertions.assertEquals(2, rows.get(2).getOrder());
    Assertions.assertEquals(3, rows.get(3).getOrder());
    Assertions.assertEquals(4, rows.get(4).getOrder());
    Assertions.assertEquals(5, rows.size());
  }

  
//Match 1
//{
//   "id": "sriBoolean",
//   "value": "false"
//},
//{
//   "id": "risk",
//   "value": "in [\"CAREFUL\", \"NOT\"]"
//},
//{
//   "id": "sri",
//   "value": "[1..2]"
//},
//{
//   "id": "sriDate",
//   "value": "equals 2017-07-03T00:00:00"
//},
//
//Match 3
//{
//   "id": "sriBoolean",
//   "value": "false"
//},
//{
//   "id": "risk",
//   "value": "not in [\"MODERATE\"]"
//},
//{
//   "id": "sri",
//   "value": "[1..5]"
//},
//{
//   "id": "sriDate",
//   "value": "equals 2017-07-03T00:00:00"
//}
  @Test
  public void executionTest() throws IOException {
    final var ast = client.ast().commands(FileUtils.toString(getClass(), "decision/dt.json")).decision();
    final var program = client.program().ast(ast);

    Map<String, Object> values = new HashMap<>();
    values.put("sriBoolean", false);
    values.put("risk", "CAREFUL");
    values.put("sri", 1);
    values.put("sriDate", DateParser.parseLocalDate("2017-07-03"));
    
    DecisionResult result = client.executor().inputMap(values).decision(program).andGetBody();

    Assertions.assertEquals(2, result.getMatches().size());
    Assertions.assertEquals(0, result.getMatches().get(0).getOrder());
    Assertions.assertEquals(2, result.getMatches().get(1).getOrder());
  }

  @Test
  public void nullEqualsNull() throws IOException {
    final var ast = client.ast().commands(FileUtils.toString(getClass(), "decision/nullEqualsNull.json")).decision();
    final var program = client.program().ast(ast);
    
    Map<String, Object> values = new HashMap<>();
    values.put("risk", null);
    
    DecisionResult result = client.executor().inputMap(values).decision(program).andGetBody();

    Assertions.assertEquals(1, result.getMatches().size());
  }
  
  @Test
  public void firstHitPolicy() throws IOException {
    final var ast = client.ast().commands(FileUtils.toString(getClass(), "decision/firstHitPolicy.json")).decision();
    final var program = client.program().ast(ast);

    Map<String, Object> values = new HashMap<>();
    values.put("regionName", "FIN");
    DecisionResult result = client.executor().inputMap(values).decision(program).andGetBody();;
    Assertions.assertEquals(1, result.getMatches().size());
    Assertions.assertEquals(0, result.getMatches().get(0).getOrder());


    values = new HashMap<>();
    values.put("regionName", "X");
    result = client.executor().inputMap(values).decision(program).andGetBody();
    Assertions.assertEquals(1, result.getMatches().size());
    Assertions.assertEquals(1, result.getMatches().get(0).getOrder());
  }

  @Test
  public void all() throws IOException {
    final var ast = client.ast().commands(FileUtils.toString(getClass(), "decision/allHitPolicy.json")).decision();
    final var program = client.program().ast(ast);

    Map<String, Object> values = new HashMap<>();
    values.put("firstName", "Mark");
    DecisionResult result = client.executor().inputMap(values).decision(program).andGetBody();;
    Assertions.assertEquals(2, result.getMatches().size());
  }
  
  @Test
  public void csvImportCommand() throws IOException {
    final var ast = client.ast().commands(FileUtils.toString(getClass(), "decision/dt-import.json")).decision();
    final var program = client.program().ast(ast);
    Assertions.assertNotNull(program);
  }

}
