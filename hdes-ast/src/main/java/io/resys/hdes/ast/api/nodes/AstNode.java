package io.resys.hdes.ast.api.nodes;
/*-
 * #%L
 * hdes-ast
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

import org.immutables.value.Value;

public interface AstNode {
  Token getToken();
  
  @Value.Immutable
  interface ErrorNode {
    AstNode getTarget();
    String getMessage();
  }
  
  @Value.Immutable
  interface Token {
    int getId();
    String getText();
    int getLine();
    int getCol();
  }

  interface Envir {
    
  }
  
  enum NodeDataType {
    STRING, INTEGER, BOOLEAN, DECIMAL,
    DATE, DATE_TIME, TIME,
  }
}
