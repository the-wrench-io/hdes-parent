package io.resys.hdes.compiler.test;

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
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import io.resys.hdes.compiler.api.HdesCompiler;
import io.resys.hdes.compiler.api.HdesCompiler.Resource;
import io.resys.hdes.compiler.spi.java.JavaHdesCompiler;

public class DtHdesCompilerTest {
  private final HdesCompiler compiler = JavaHdesCompiler.config().build();

  @Test
  public void simpleDt() {
    List<Resource> code = compiler.parser()
        .add("SimpleDt.hdes", file("SimpleDt.hdes"))
    .build();
  }
  @Test
  public void expressionDt() {
    List<Resource> code = compiler.parser()
        .add("ExpressionDt.hdes", file("ExpressionDt.hdes"))
    .build();
  }
  
  public static String file(String name) {
    try {
      return IOUtils.toString(DtHdesCompilerTest.class.getClassLoader().getResourceAsStream(name), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
