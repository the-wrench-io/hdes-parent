package io.resys.hdes.compiler.spi.java;

/*-
 * #%L
 * hdes-compiler
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
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import io.resys.hdes.compiler.api.HdesCompiler;
import io.resys.hdes.compiler.api.HdesCompiler.Code;

public class DtHdesCompilerTest {
  private final HdesCompiler compiler = JavaHdesCompiler.config().build();

  @Test
  public void simpleDt() {
    Code code = compiler.parser()
        .add("SimpleDt.hdes", file("SimpleDt.hdes"))
    .build();
    
    System.out.println(code.getValues().get(0).getTarget());
    
  }
  @Test
  public void expressionDt() {
    Code code = compiler.parser()
        .add("ExpressionDt.hdes", file("ExpressionDt.hdes"))
    .build();
    
    //System.out.println(code.getValues().get(0).getTarget());
    System.out.println(code.getValues().get(1).getTarget());
    
  }
  
  public static String file(String name) {
    try {
      return IOUtils.toString(DtHdesCompilerTest.class.getClassLoader().getResourceAsStream(name), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
