package io.resys.hdes.ast.api;

/*-
 * #%L
 * hdes-datatype
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

import java.util.Collections;
import java.util.List;

import io.resys.hdes.ast.api.nodes.AstNode.ErrorNode;

public class AstNodeException extends RuntimeException {

  private static final long serialVersionUID = -7531661358793107460L;

  private List<ErrorNode> errors;

  public AstNodeException(String message) {
    super(message);
    errors = Collections.emptyList();
  }
  
  public AstNodeException(List<ErrorNode> errors) {
    super(build(errors));
    this.errors = errors;
  }  
  
  public List<ErrorNode> getErrors() {
    return errors;
  }
  
  public static String build(List<ErrorNode> errors) {
    StringBuilder result = new StringBuilder("Error(s)").append(System.lineSeparator());
    for(ErrorNode error : errors) {
      result.append("  - ")
      .append("at (")
      .append(error.getTarget().getToken().getLine()).append(":").append(error.getTarget().getToken().getCol())
      .append("). Source: ").append(error.getTarget().getToken().getText()).append(System.lineSeparator())
      .append(error.getMessage());
    }
    return result.toString();
  }
}
