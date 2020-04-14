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

import io.resys.hdes.ast.api.AstEnvir;
import io.resys.hdes.ast.spi.ImmutableAstEnvir;
import io.resys.hdes.compiler.api.HdesCompiler;
import io.resys.hdes.compiler.api.HdesCompilerException;
import io.resys.hdes.compiler.spi.java.visitors.JavaAstEnvirVisitor;

public class JavaHdesCompiler implements HdesCompiler {

  @Override
  public Parser parser() {
    AstEnvir.Builder builder = ImmutableAstEnvir.builder();
    return new Parser() {
      @Override
      public Code build() {
        return new JavaAstEnvirVisitor().visit(builder.build());
      }
      @Override
      public Parser add(String filename, String src) {
        if(filename.endsWith(".dt")) {
          builder.add().externalId(filename).decisionTable(src);
        } else if(filename.endsWith(".fl")) {
          builder.add().externalId(filename).flow(src);
        } else if(filename.endsWith(".mt")) {
          builder.add().externalId(filename).manualTask(src);
        } else {
          throw new HdesCompilerException(HdesCompilerException.builder().unknownFileExtension(filename));
        }         
        return this;
      }
    };
  }
  
  public static Config config() {
    return new Config();
  }
  
  public static class Config {
    public JavaHdesCompiler build() {
      return new JavaHdesCompiler();
    }
  }
}
