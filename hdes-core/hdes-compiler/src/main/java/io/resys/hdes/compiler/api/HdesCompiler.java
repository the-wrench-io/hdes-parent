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
  
  interface HdesExecutable {}

  Parser parser();
  
  interface Parser {
    Parser add(String name, String src);
    List<Resource> build();
  }

  @Value.Immutable
  interface Resource {
    String getName();
    SourceType getType();
    String getSource();
    List<TypeName> getTypes();
    List<TypeDeclaration> getDeclarations();
  }
  
  @Value.Immutable
  interface TypeDeclaration {
    TypeName getType();
    String getValue();
    
    // Is main executable
    boolean isExecutable(); 
  }
  
  @Value.Immutable
  interface TypeName {
    String getPkg();
    String getName();
  }
}
