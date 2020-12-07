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

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.resys.hdes.executor.api.Trace.TraceEnd;

public class FlowWithSimpleLoopTest {
  
  @Test
  public void loop() {
    String src = """ 
        decision-table Scoring ({ arg: INTEGER }) : { score: INTEGER } { 
          findFirst({
            when( _ between 1 and 10 ).add({ 10 }) 
            when( ? ).add({ 20 })
          })
        } 
        
        flow SimpleFlow ({ classifiers: INTEGER[] }): { total: INTEGER }
        { 
          InitialScoring() {
            map classifiers to {
              Scoring ({ arg: _ })
            }
            return { total: sum(_.map(scoring -> scoring.score)) }
          }
        }
      """;
    
    /*

    map(classifiers).to({
      Scoring ({ arg: _ })
    })
    
*/

    TraceEnd output = TestUtil.runtime().src(src).build("SimpleFlow")
        .accepts()
        .value("classifiers", new ArrayList<>(Arrays.asList(10, 12)))
        .build();
    
    yaml(output.getBody());
    
    Assertions.assertEquals("""
      ---
      total: 30
      """, yaml(output.getBody()));
    
  }

}
