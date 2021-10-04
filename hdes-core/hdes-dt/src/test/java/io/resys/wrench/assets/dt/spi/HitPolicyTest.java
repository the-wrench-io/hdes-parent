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
import io.resys.hdes.client.api.model.DecisionTableModel;
import io.resys.hdes.client.spi.util.FileUtils;
import io.resys.wrench.assets.dt.api.DecisionTableRepository;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.DecisionTableFixedValue;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.DecisionTableFormat;
import io.resys.wrench.assets.dt.spi.config.TestDtConfig;

@RunWith(BlockJUnit4ClassRunner.class)
public class HitPolicyTest {


  private DecisionTableRepository decisionTableRepository = TestDtConfig.decisionTableRepository();

  @Test
  public void firstHitPolicy() throws IOException {
    InputStream stream = FileUtils.toInputStream(getClass(), "firstHitPolicy.json");

    DecisionTableModel decisionTable = decisionTableRepository.createBuilder()
        .format(DecisionTableFormat.JSON)
        .src(stream)
        .build();

    Map<String, Object> values = new HashMap<>();
    values.put("regionName", "FIN");
    DecisionTableResult result = execute(decisionTable, values);
    Assert.assertEquals(1, result.getMatches().size());
    Assert.assertEquals(0, result.getMatches().get(0).getNode().getId());


    values = new HashMap<>();
    values.put("regionName", "X");
    result = execute(decisionTable, values);
    Assert.assertEquals(1, result.getMatches().size());
    Assert.assertEquals(1, result.getMatches().get(0).getNode().getId());
  }

  @Test
  public void all() throws IOException {
    InputStream stream = FileUtils.toInputStream(getClass(), "allHitPolicy.json");

    DecisionTableModel decisionTable = decisionTableRepository.createBuilder()
        .format(DecisionTableFormat.JSON)
        .src(stream)
        .build();

    Map<String, Object> values = new HashMap<>();
    values.put("firstName", "Mark");
    DecisionTableResult result = execute(decisionTable, values);
    Assert.assertEquals(2, result.getMatches().size());
  }

  @Test
  public void allWithFixedOverrideEvaluation() throws IOException {
    InputStream stream = FileUtils.toInputStream(getClass(), "allHitPolicy.json");

    DecisionTableModel decisionTable = decisionTableRepository.createBuilder()
        .format(DecisionTableFormat.JSON)
        .src(stream)
        .build();

    Map<String, Object> values = new HashMap<>();
    values.put("firstName", DecisionTableFixedValue.ALWAYS_TRUE);
    DecisionTableResult result = execute(decisionTable, values);
    Assert.assertEquals(2, result.getMatches().size());
  }

  
  public DecisionTableResult execute(DecisionTableModel decisionTable, Map<String, Object> values) {
    return decisionTableRepository.createExecutor().
        decisionTable(decisionTable).
        context((type) -> values.get(type.getName())).
        execute();
  }
}
