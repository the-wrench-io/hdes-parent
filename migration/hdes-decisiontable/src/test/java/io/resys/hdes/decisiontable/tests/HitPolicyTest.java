package io.resys.hdes.decisiontable.tests;

/*-
 * #%L
 * hdes-decisiontable
 * %%
 * Copyright (C) 2020 Copyright 2020 ReSys OÜ
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
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.resys.hdes.datatype.api.DataTypeCommand;
import io.resys.hdes.decisiontable.api.DecisionTableAst;
import io.resys.hdes.decisiontable.api.DecisionTableExecution;
import io.resys.hdes.decisiontable.api.DecisionTableModel;
import io.resys.hdes.decisiontable.tests.config.TestDtConfig;

public class HitPolicyTest {
  @Test
  public void firstHitPolicy() throws IOException {
    Map<String, Serializable> values = new HashMap<>();
    values.put("regionName", "FIN");
    List<DecisionTableExecution> result = execute("firstHitPolicy.json", values);
    Assertions.assertEquals(1, result.size());
    Assertions.assertEquals(0, result.get(0).getNode().getId());


    values = new HashMap<>();
    values.put("regionName", "X");
    result = execute("firstHitPolicy.json", values);
    Assertions.assertEquals(1, result.size());
    Assertions.assertEquals(1, result.get(0).getNode().getId());
  }

  @Test
  public void all() throws IOException {
    Map<String, Serializable> values = new HashMap<>();
    values.put("firstName", "Mark");
    
    List<DecisionTableExecution> result = execute("allHitPolicy.json", values);
    Assertions.assertEquals(2, result.size());
  }

  public List<DecisionTableExecution> execute(String filename, Map<String, Serializable> values) {
    List<DataTypeCommand> commands = TestDtConfig.dataTypeService.read().classpath(filename)
        .list(DataTypeCommand.class);
    
    DecisionTableModel model = TestDtConfig.decisionTableRepository.model().src(commands).build();
    DecisionTableAst ast = TestDtConfig.decisionTableRepository.ast().from(model).build();
    return TestDtConfig.decisionTableRepository.execution()
        .ast(ast)
        .input((type) -> values.get(type.getName()))
        .build().blockingGet().getValue();
  }
}
