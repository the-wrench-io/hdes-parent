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

import java.util.List;
import java.util.Optional;

import org.immutables.value.Value;

public interface AstNode {
  Token getToken();
  
  @Value.Immutable
  interface TypeName extends AstNode {
    String getValue();
  }
  
  @Value.Immutable
  interface Literal extends AstNode {
    ScalarType getType();
    String getValue();
  }
  
  @Value.Immutable
  interface EmptyNode extends AstNode { }
  
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
  
  interface DataTypeConversion extends ExpressionNode {
    ExpressionNode getValue();
    ScalarType getToType();
  }

  @Value.Immutable
  interface DateConversion extends DataTypeConversion { }

  @Value.Immutable
  interface DateTimeConversion extends DataTypeConversion { }

  @Value.Immutable
  interface TimeConversion extends DataTypeConversion { }

  @Value.Immutable
  interface DecimalConversion extends DataTypeConversion { }

  
  
  interface InputNode extends AstNode {
    Boolean getRequired();    
    String getName();
  }
  
  @Value.Immutable
  interface ObjectInputNode extends InputNode {
    List<InputNode> getValues();
  }
  
  @Value.Immutable
  interface ArrayInputNode extends InputNode {
    InputNode getValue();
  }
  
  @Value.Immutable
  interface ScalarInputNode extends InputNode {
    Optional<String> getDebugValue();
    ScalarType getType();
  }
  
  enum ScalarType {
    STRING, INTEGER, BOOLEAN, DECIMAL,
    DATE, DATE_TIME, TIME,
  }
}
