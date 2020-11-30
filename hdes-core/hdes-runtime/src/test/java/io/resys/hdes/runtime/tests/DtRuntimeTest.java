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
        decision-table ExpressionDT {  
        accepts { value0 INTEGER, value1 INTEGER } 
        returns { score INTEGER }  
        matches ALL { 
          when { _ > 10, _ <= 20 }            then { 4571 } 
          when { _ > 10, _ <= 20 and _ > 10 } then { 4572 } 
          when { _ = 6 , _ != 20 and _ > 10 } then { 4573 } 
        }}
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
        decision-table Scoring { 
              accepts { arg INTEGER } 
              returns { score INTEGER } 
               
              matches FIRST { 
                when { _ between 1 and 30 } then { 10 } 
                when { ? } then { 20 } 
              } 
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
        decision-table SimpleHitPolicyFirstDt { 
        accepts { name STRING, lastName STRING } 
        returns {
          value INTEGER, 
          totalHit INTEGER: sum(_constants.map(row -> row.value))
        }
         matches FIRST { 
          when {_ = 'sam', ? }           then { 20 }
          when { _ = 'bob', _ = 'woman'} then { 4570 }
          when { _ != 'bob' or _ !='same' or _ = 'professor', _ = 'woman' or _ = 'man'} then { 4571 } 
        }}
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
        decision-table SimpleHitPolicyMatrixDt { 
        accepts { name STRING, lastName STRING }   
        returns {}
        
        maps STRING { _ = 'BOB', _ = 'SAM', ? }
        to INTEGER { 
          lastName {  10,    20,   30 } 
          name     {  20,    50,   60 } 
        }}
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
        decision-table DtWithFormula { 
        accepts { 
          a INTEGER, b INTEGER, c DECIMAL,
          total DECIMAL: a + b + c }
        returns {
          totalOut DECIMAL: total,
          score STRING }
        
        matches FIRST { 
          when { ?, ?, ?, _ > 100}  then {'high-risk'} 
          when { ?, ?, ?, ?}        then {'low-risk'} 
        }}
        
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
        decision-table MatrixDT {   
        accepts { name STRING, lastName STRING }
        returns {
          score INTEGER: sum(_matched), // total score of hit columns  
          max   INTEGER: sum(_constants.map( row -> max(row) )) // sum max possible score of defined fields 
        }
        
        maps STRING { _ = 'BOB', _ = 'SAM', ? }
        to INTEGER {  
          lastName {  10,    20,   30 } 
          name     {  20,    50,   60 } 
        }}
      """;
        
    TraceEnd output = TestUtil.runtime().src(src).build("MatrixDT")
        .accepts()
        .value("lastName", "SAM")
        .value("name", "BOB")
        .build();

    Assertions.assertEquals("""
      --- 
      score: 40 
      max: 90 
      lastName: 20 
      name: 20
      """, yaml(output.getBody()));
  }
}
