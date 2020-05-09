package io.resys.hdes.compiler.api;

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

import java.util.List;

import org.immutables.value.Value;

public interface HdesCompiler {
  
  enum SourceType {FL, MT, DT}
  
  Parser parser();
  
  interface Parser {
    Parser add(String fileName, String src);
    Code build();
  }

  @Value.Immutable
  interface Code {
    List<CodeValue> getValues();
  }
  
  @Value.Immutable
  interface CodeValue {
    SourceType getType();
    String getPackageName();
    String getSimpleName();
    String getSource();
    String getTarget();
  }
}
