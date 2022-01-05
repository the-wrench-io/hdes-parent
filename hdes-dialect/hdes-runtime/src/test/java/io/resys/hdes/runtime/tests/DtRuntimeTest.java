package io.resys.hdes.runtime.tests;

/*-
 * #%L
 * hdes-runtime
 * %%
 * Copyright (C) 2020 - 2021 Copyright 2020 ReSys OÜ
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

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.resys.hdes.executor.api.Trace.TraceEnd;

public class DtRuntimeTest {
  
  @Test 
  public void dtHitPolicyAll() {
    
    TraceEnd output = TestUtil.runtime().src(fileSrc("dtHitPolicyAll")).build("ExpressionDT")
      .accepts()
      .value("value0", 11)
      .value("value1", 2)
      .build();
    
    Assertions.assertEquals(fileYaml("dtHitPolicyAll"), TestUtil.yaml(output.getBody()));
  }
  
  @Test 
  public void dtHitPolicyFirstBetween() {
    
    TraceEnd output = TestUtil.runtime().src(fileSrc("dtHitPolicyFirstBetween")).build("Scoring")
        .accepts()
        .value("arg", 11)
        .build();
    
    Assertions.assertEquals(fileYaml("dtHitPolicyFirstBetween"), TestUtil.yaml(output.getBody()));
  }
  
  
  @Test 
  public void dtHitPolicdtHitPolicyFirstyFirst() {
    TraceEnd output = TestUtil.runtime().src(fileSrc("dtHitPolicdtHitPolicyFirstyFirst")).build("SimpleHitPolicyFirstDt")
        .accepts()
        .value("name", "sam")
        .value("lastName", "blah")
        .build();

    Assertions.assertEquals(fileYaml("dtHitPolicdtHitPolicyFirstyFirst"), TestUtil.yaml(output.getBody()));
  }

  @Test 
  public void dtHitPolicyFirstFormula() {

    TraceEnd output = TestUtil.runtime().src(fileSrc("dtHitPolicyFirstFormula")).build("DtWithFormula")
        .accepts()
        .value("a", 10)
        .value("b", 100)
        .value("c", new BigDecimal(10.78).setScale(2, RoundingMode.HALF_UP))
        .build();
    
    Assertions.assertEquals(fileYaml("dtHitPolicyFirstFormula"), TestUtil.yaml(output.getBody()));
  }
  
  @Test 
  public void dtHitPolicyMatrix() {
    TraceEnd output = TestUtil.runtime().src(fileSrc("dtHitPolicyMatrix")).build("SimpleHitPolicyMatrixDt")
        .accepts()
        .value("name", "sam")
        .value("lastName", "blah")
        .build();

    Assertions.assertEquals(fileYaml("dtHitPolicyMatrix"), TestUtil.yaml(output.getBody()));
  }
  
  @Test 
  public void dtHitPolicyMatrixLambdas() {
        
    TraceEnd output = TestUtil.runtime().src(fileSrc("dtHitPolicyMatrixLambdas")).build("MatrixDT")
        .accepts()
        .value("lastName", "SAM")
        .value("name", "BOB")
        .build();

    Assertions.assertEquals(fileYaml("dtHitPolicyMatrixLambdas"), TestUtil.yaml(output.getBody()));
  }
  
  
  private static String fileSrc(String file) {
    String value = TestUtil.file("DtRuntimeTest/" + file + ".hdes");
    return value;
  }
  
  private static String fileYaml(String file) {
    String value = TestUtil.file("DtRuntimeTest/" + file + ".yml");
    return value;
  } 
}
