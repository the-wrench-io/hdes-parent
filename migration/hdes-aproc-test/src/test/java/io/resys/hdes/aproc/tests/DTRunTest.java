package io.resys.hdes.aproc.tests;

/*-
 * #%L
 * hdes-aproc-test
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
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.resys.hdes.aproc.spi.ImmutableTags;
import io.resys.hdes.decisiontable.api.DecisionTableAst;
import io.resys.hdes.decisiontable.api.DecisionTableModel;
import io.resys.hdes.execution.HdesService.Executable;
import io.resys.hdes.storage.api.ImmutableChanges;

public class DTRunTest {
  @Test
  public void readerNodeOrderTestDynamic() throws IOException {
    
    ImmutableChanges changes = TestConfig.dataTypeService.read().classpath("assets/dt/allHitPolicy.json").build(ImmutableChanges.class);
    DecisionTableModel model = TestConfig.decisionTableRepository.model().src(changes.getValues()).build();
    DecisionTableAst ast = TestConfig.decisionTableRepository.ast().from(model).build();
    
    long start = System.nanoTime();
    
    Map<String, Serializable> values = new HashMap<>();
    values.put("firstName", "Mark");
    TestConfig.decisionTableRepository.execution().ast(ast).input((type) -> values.get(type.getName())).build().blockingGet();
    
    System.out.println("total time for dynamic: " + (System.nanoTime() - start));
  }
  
  @Test
  public void readerNodeOrderTestCompiled() throws IOException {
    Executable executable = new ImmutableTags().getValues().get("master").get().stream().filter(e -> e.getName().equals("hitPolicyExample")).findFirst().get();
    long start = System.nanoTime();
    
    Map<String, Serializable> values = new HashMap<>();
    values.put("firstName", "Mark");
    executable.run("testRun", (type) -> values.get(type.getName()));
    
    System.out.println("total time for compiled: " + (System.nanoTime() - start));
    
//    System.out.println(new BigDecimal("39480703")
//        .divide(new BigDecimal("744430"), RoundingMode.UP));     
//    System.out.println(new BigDecimal("28824944")
//        .multiply(new BigDecimal(100))
//        .divide(new BigDecimal("355194075"), RoundingMode.UP));
  }
}
