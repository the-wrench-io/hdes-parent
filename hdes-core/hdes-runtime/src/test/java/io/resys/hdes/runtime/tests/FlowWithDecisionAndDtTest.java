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
        flow EmptyFlow ({ arg1: INTEGER, arg2: INTEGER }) : { }
        {
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
        decision-table Scoring({ arg: integer }) : { score: integer } {
          findFirst({
            when( _ between 1 and 30) add({ 10 })
            when( ? ) add({ 20 })
          })
        }

        flow SimpleFlow ({ arg1: INTEGER, arg2: INTEGER }) : { score: INTEGER }
        {
            
          InitialScoring() {
            Scoring({ arg: arg1 }) return Decision()
          }
          
          Decision() {
            if( InitialScoring.score > 10 ) return ExtraScoring()
            else return { InitialScoring.score } 
          }
        
          ExtraScoring() {
            Scoring({ arg: arg2 }) return { _score }
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
        decision-table Scoring ({ arg: INTEGER }) : { score: INTEGER } {
          findFirst({
            when ( _ between 1 and 30 ).add({ 10 })
            when ( ? ).add({ 20 })
          })
        }
        
        flow SimpleFlow({ arg1: INTEGER, arg2: INTEGER }) : { total: INTEGER }
        {
            
          InitialScoring() {
            Scoring ({ arg: arg1 })
            Scoring ({ arg: arg2 })
            as({ total: _0.score + _1.score })
            return Decision()
          }
          
          Decision() {
            if (InitialScoring.total > 10) return ExtraScoring()
            else return { InitialScoring.total } 
          }
        
          ExtraScoring() {
            Scoring({ arg: arg2 })
            return { total: _score + InitialScoring.total }
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
      total: 30
      """, yaml(output.getBody()));
  }
}
