package io.resys.hdes.ast.test;

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

import io.resys.hdes.ast.spi.ImmutableAstEnvir;

public class AstEnvirTest {

  @Test
  public void simpleFlow() {
    ImmutableAstEnvir.builder()
    .add().src(file("basicDt.hdes"))
    .add().src(file("basicFl.hdes"))
    .add().src(file("basicMt.hdes"))
    .add().src(file("matrixDt.hdes"))
    .build();
  }

  public static String file(String name) {
    try {
      return IOUtils.toString(AstEnvirTest.class.getClassLoader().getResourceAsStream(name), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
