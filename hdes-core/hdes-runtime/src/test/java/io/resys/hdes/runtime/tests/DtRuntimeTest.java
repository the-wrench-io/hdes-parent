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

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.resys.hdes.executor.api.Trace.TraceEnd;

public class DtRuntimeTest {
  
  @Test 
  public void dtHitPolicyAll() {
    String src = """
        decision-table ExpressionDT({ value0: INTEGER, value1: INTEGER }): { score: INTEGER } {  
        
        findAll({ 
          when ( _ > 10, _ <= 20 )            add ({ 4571 }) 
          when ( _ > 10, _ <= 20 and _ > 10 ) add ({ 4572 })
          when ( _ = 6 , _ != 20 and _ > 10 ) add ({ 4573 }) 
        })
        }
      """;
    
    TraceEnd output = TestUtil.runtime().src(src).build("ExpressionDT")
      .accepts()
      .value("value0", 11)
      .value("value1", 2)
      .build();
    
    Assertions.assertEquals("""
      --- 
      values: 
      - score: 4571
      """, yaml(output.getBody()));
  }
  
  @Test 
  public void dtHitPolicyFirstBetween() {
    String src = """
        decision-table Scoring({ arg: INTEGER }) : { score: INTEGER } { 
               
          findFirst({
            when( _ between 1 and 30).add({ 10 }) 
            when( ? ).add({ 20 })
          })
        }
      """;
    
    TraceEnd output = TestUtil.runtime().src(src).build("Scoring")
        .accepts()
        .value("arg", 11)
        .build();
    
    Assertions.assertEquals("""
      --- 
      score: 10
      """, yaml(output.getBody()));
  }
  
  
  @Test 
  public void dtHitPolicdtHitPolicyFirstyFirst() {
    String src = """ 
        decision-table SimpleHitPolicyFirstDt({ name: STRING, lastName: STRING }): 
        {
          value: INTEGER, 
          totalHit: INTEGER = sum(_constants.map(row -> row.value))
        } {
        
         findFirst({ 
           when(_ = 'sam', ? )           add({ 20 })
           when(_ = 'bob', _ = 'woman')  add({ 4570 })
           when(_ != 'bob' or _ !='same' or _ = 'professor', _ = 'woman' or _ = 'man') add({ 4571 }) 
         })
        
        }
      """;

    TraceEnd output = TestUtil.runtime().src(src).build("SimpleHitPolicyFirstDt")
        .accepts()
        .value("name", "sam")
        .value("lastName", "blah")
        .build();

    Assertions.assertEquals("""
      --- 
      value: 20 
      totalHit: 9161
      """, yaml(output.getBody()));
  }
  
  @Test 
  public void dtHitPolicyMatrix() {
    String src = """
        decision-table SimpleHitPolicyMatrixDt({ name: string, lastName: string }) : {}
        {
          map(string) to(integer)
          when( _ = 'BOB', _ = 'SAM', ?)
            lastName({  10,    20,   30 })
            name    ({  20,    50,   60 }) 
          
        }
      """;

    TraceEnd output = TestUtil.runtime().src(src).build("SimpleHitPolicyMatrixDt")
        .accepts()
        .value("name", "sam")
        .value("lastName", "blah")
        .build();

    Assertions.assertEquals("""
      --- 
      lastName: 30 
      name: 60
      """, yaml(output.getBody()));
  }
  
  
  @Test 
  public void dtHitPolicyFirstFormula() {
    String src = """ 
        decision-table DtWithFormula
        ({ 
          a: INTEGER, b: INTEGER, c: DECIMAL,
          total: DECIMAL = a + b + c }):
        {
          totalOut: DECIMAL = total,
          score: STRING } {
        
          findFirst({ 
            when(?, ?, ?, _ > 100)  add({'high-risk'}) 
            when(?, ?, ?, ?)        add({'low-risk'}) 
          })
        }
      """;

    TraceEnd output = TestUtil.runtime().src(src).build("DtWithFormula")
        .accepts()
        .value("a", 10)
        .value("b", 100)
        .value("c", new BigDecimal(10.78).setScale(2, RoundingMode.HALF_UP))
        .build();
    
    Assertions.assertEquals("""
      --- 
      totalOut: 120.78 
      score: "high-risk" 
      """, yaml(output.getBody()));
  }
  
  @Test 
  public void dtHitPolicyMatrixLambdas() {
    String src = """ 
        decision-table MatrixDT(  
        { name: STRING, lastName: STRING }):
        {
                score: INTEGER = sum(_matched), // total score of hit columns  
          maxPossible: INTEGER = sum(_constants.map( row -> max(row) )) // sum max possible score of defined fields 
        } {
        
        map(STRING)
          .to(INTEGER)
          .when(_ = 'BOB', _ = 'SAM', ?)
            lastName({  10,    20,   30 }) 
            name    ({  20,    50,   60 })
        }
      """;
        
    TraceEnd output = TestUtil.runtime().src(src).build("MatrixDT")
        .accepts()
        .value("lastName", "SAM")
        .value("name", "BOB")
        .build();

    Assertions.assertEquals("""
      --- 
      score: 40 
      maxPossible: 90 
      lastName: 20 
      name: 20
      """, yaml(output.getBody()));
  }
}
