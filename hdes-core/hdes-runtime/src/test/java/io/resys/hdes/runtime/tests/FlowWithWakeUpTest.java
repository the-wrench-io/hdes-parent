package io.resys.hdes.runtime.tests;

/*-
 * #%L
 * hdes-runtime
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

import static io.resys.hdes.runtime.tests.TestUtil.yaml;

import java.io.Serializable;
import java.util.HashMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.resys.hdes.executor.api.Trace.TraceEnd;
import io.resys.hdes.executor.api.TraceBody.Await;
import io.resys.hdes.executor.api.TraceBody.Suspends;
import io.resys.hdes.runtime.tests.TestUtil.TestRunner;


public class FlowWithWakeUpTest {
  
  @Test
  public void simpleFlow() {
    String src = """
        service-task FormPromise({}) : { dataId: STRING, userValue: INTEGER } {
          promise { timeout: 100 } 
          
          io.resys.hdes.runtime.test.CreateSessionCommand { formId: 'SuperForm' }
        }
        
        decision-table ScoringDT({ arg: INTEGER }) : { score: INTEGER } {
          findFirst({
            when( _ between 1 and 30 ) add({ 10 })
            when( ? ) add({ 20 })
          })
        }

        flow SimpleFlow({ arg1: INTEGER, arg2: INTEGER }):{ score: INTEGER, userValue: INTEGER }
        {
          InitialScoring() {
            ScoringDT({ arg: arg1 }) continue 
          }

          UserInput() {
            await FormPromise({}) continue 
          }

          DecisionStep() {
            if(UserInput.userValue > 10) continue
            else return { InitialScoring.score, UserInput.userValue }
          }
          ExtraScoring () { 
            ScoringDT({ arg: UserInput.userValue }) 
            return { _score, UserInput.userValue } 
          }
        }
        """;

    TestRunner runner = TestUtil.runtime().src(src).build("SimpleFlow");
    TraceEnd output = runner.accepts()
        .value("arg1", 11)
        .value("arg2", 2)
        .build();

    Suspends suspends = output.getSuspends();
    Assertions.assertNotNull(suspends);
    Assertions.assertNotNull(suspends.getValues().size() == 1);
    System.out.println(yaml(suspends));

    Await await = suspends.getValues().iterator().next();
    
    HashMap<String, Serializable> promiseData = new HashMap<>();
    promiseData.put("userValue", 100);
    output = runner.wakeup().accepts(await.getDataId(), promiseData).build(output);
    Assertions.assertEquals("""
---
score: 20
userValue: 100
        """, yaml(output.getBody()));
      
  }
}
