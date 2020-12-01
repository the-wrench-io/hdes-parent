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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.resys.hdes.executor.api.Trace.TraceEnd;


public class FlowWithDecisionAndDtTest {
  @Test
  public void flowWithNoSteps() {
    String src = """
        flow EmptyFlow {
        
          accepts { arg1 INTEGER, arg2 INTEGER }
          returns { }
          
          steps {
          }
        }
      """;
    
    TraceEnd output = TestUtil.runtime().src(src).build("EmptyFlow")
        .accepts()
        .value("arg1", 11)
        .value("arg2", 2)
        .build();

    Assertions.assertEquals("--- {}\n", yaml(output.getBody()));
  }
  
  @Test
  public void flowWithDecisionAndSplit() {
    String src = """
        decision-table Scoring {
          accepts { arg INTEGER } returns { score INTEGER }
          matches FIRST {
            when { _ between 1 and 30 } then { 10 }
            when { ? } then { 20 }
          }
        }

        flow SimpleFlow {
          accepts { arg1 INTEGER, arg2 INTEGER } returns { score INTEGER }
          
          steps {
            
            InitialScoring {
              call Scoring { arg: arg1 } then Decision
            }
            
            Decision {
              when { InitialScoring.score > 10 } then ExtraScoring
              then end-as { InitialScoring.score } 
            }
          
            ExtraScoring {
              call Scoring { arg: arg2 } then end-as { _score }
            }
          }
        }
        """;
    
    
    TraceEnd output = TestUtil.runtime().src(src).build("SimpleFlow")
        .accepts()
        .value("arg1", 11)
        .value("arg2", 2)
        .build();
    
    Assertions.assertEquals("""
      ---
      score: 10
      """, yaml(output.getBody()));
  }
  
  @Test
  public void flowWithParalDecisionAndSplit() {
    String src = """
        decision-table Scoring {
          accepts { arg INTEGER } returns { score INTEGER }
          matches FIRST {
            when { _ between 1 and 30 } then { 10 }
            when { ? } then { 20 }
          }
        }
        
        flow SimpleFlow {
        
          accepts { arg1 INTEGER, arg2 INTEGER }
          returns { total INTEGER }
          
          steps {
            
            InitialScoring {
              call Scoring { arg: arg1 }
              call Scoring { arg: arg2 }
              then Decision
            } as {
              total: _0.score + _1.score
            }
            
            Decision {
              when { InitialScoring.total > 10 } then ExtraScoring
              then end-as { InitialScoring.total } 
            }
          
            ExtraScoring {
              call Scoring { arg: arg2 }
              then end-as { total: _score }
            }
          }
        }
        """;
    
    
    TraceEnd output = TestUtil.runtime().src(src).build("SimpleFlow")
        .accepts()
        .value("arg1", 11)
        .value("arg2", 2)
        .build();
    
    Assertions.assertEquals("""
      ---
      score: 10
      """, yaml(output.getBody()));
  }
}
