package io.resys.hdes.compiler.test;

/*-
 * #%L
 * hdes-compiler
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

import static io.resys.hdes.compiler.test.TestUtil.compiler;
import static io.resys.hdes.compiler.test.TestUtil.file;
import static io.resys.hdes.compiler.test.TestUtil.log;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.resys.hdes.compiler.api.HdesCompiler.Resource;


public class HdesCompilerTest {
  
  @Test
  public void simpleFlow() {
    final var file= file("SimpleFlow.hdes");
    List<Resource> code = compiler.parser()
      .add("SimpleFlow.hdes", file)
      //.add("SimpleHitPolicyFirstDt.hdes", file("SimpleHitPolicyFirstDt.hdes"))
      .build();
    
    
    log(code, file);
  }
  
  @Test
  public void simpleHitPolicyMatrixDt() {
    final var file = file("SimpleHitPolicyMatrixDt.hdes");
    List<Resource> code = compiler.parser()
        .add("SimpleHitPolicyMatrixDt.hdes", file)
    .build();
    log(code, file);
  }
  
  
  @Test
  public void simpleHitPolicyAllDt() {
    final var file = file("SimpleHitPolicyAllDt.hdes");
    List<Resource> code = compiler.parser()
        .add("SimpleHitPolicyAllDt.hdes", file)
    .build();
    log(code, file);
  }
  
  @Test
  public void simpleHitPolicyFirstDt() {
    final var file = file("SimpleHitPolicyFirstDt.hdes");
    List<Resource> code = compiler.parser()
        .add("SimpleHitPolicyFirstDt.hdes", file)
    .build();
    log(code, file);
  }
  
  @Test
  public void formulaHitPolicyFirstDt() {
    final var file = file("FormulaHitPolicyFirstDt.hdes");
    List<Resource> code = compiler.parser()
        .add("FormulaHitPolicyFirstDt.hdes", file)
    .build();
    log(code, file);
  }

}
