package io.resys.hdes.servicetask.tests;

/*-
 * #%L
 * hdes-servicetask
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

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.resys.hdes.datatype.api.ImmutableDataTypeInputMap;
import io.resys.hdes.servicetask.api.ServiceTaskAst;
import io.resys.hdes.servicetask.api.ServiceTaskModel;
import io.resys.hdes.servicetask.tests.config.TestServiceConfig;

public class GroovyServiceTaskTest {

  @Test
  public void aPlusBEqualsSum() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {

    ServiceTaskModel model = TestServiceConfig.builder()
      .name("X")
      .input("int a").input("int b")
      .output("int sum")
      .body("output.sum = input.a + input.b")
      .build(Object.class);
    ServiceTaskAst task = TestServiceConfig.serviceTaskService.ast().from(model).build();
    
    Map<String, Object> outputMap = TestServiceConfig.serviceTaskService.execution().ast(task).input(ImmutableDataTypeInputMap.builder()
        .putValues("a", 10)
        .putValues("b", 20)
        .build())
    .build().blockingGet().getOutput().getMap();

    //System.out.println(model.getSrc().getValue());
    Assertions.assertEquals(30, outputMap.get("sum"));
  }
}
