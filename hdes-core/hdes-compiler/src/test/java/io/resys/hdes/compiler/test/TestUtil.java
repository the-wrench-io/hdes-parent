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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.compiler.api.HdesCompiler;
import io.resys.hdes.compiler.api.HdesCompiler.Resource;
import io.resys.hdes.compiler.spi.GenericHdesCompiler;

public class TestUtil {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(TestUtil.class);
  public static final HdesCompiler compiler = GenericHdesCompiler.config().build();

  
  public static void log(List<Resource> resources, String file) {
    if(!LOGGER.isDebugEnabled()) {
      return;
    }
    
    StringBuilder result = new StringBuilder();
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      result.append(System.lineSeparator()).append("############").append(System.lineSeparator())
        .append("{").append(System.lineSeparator())
        .append("src: ").append(objectMapper.writeValueAsString(file)).append(System.lineSeparator())
        .append("ast: {").append(System.lineSeparator());
      
      for (Resource r : resources) {
        String ast = objectMapper.writeValueAsString(r.getAst());
        result.append("    ").append(r.getName()).append(": ").append(ast).append(System.lineSeparator());
      }
      result
      .append("  }").append(System.lineSeparator())
      .append("}");
      
      LOGGER.debug(result.toString());
    } catch(IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
  
  public static String file(String name) {
    try {
      return IOUtils.toString(DtHdesCompilerTest.class.getClassLoader().getResourceAsStream(name), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
