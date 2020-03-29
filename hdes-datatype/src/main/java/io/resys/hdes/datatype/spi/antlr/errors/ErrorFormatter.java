package io.resys.hdes.datatype.spi.antlr.errors;

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

import java.util.List;

import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.ErrorNode;

public class ErrorFormatter {
  
  public static String build(List<ErrorNode> errors) {
    StringBuilder result = new StringBuilder("Error(s)").append(System.lineSeparator());
    for(ErrorNode error : errors) {
      result.append("  - ")
      .append("at (")
      .append(error.getTarget().getPos().getStart().getLine()).append(":").append(error.getTarget().getPos().getStart().getCol())
      .append("). ")
      .append(error.getMessage())
      .append(" -> ")
      .append(error.getTarget().getPos().getText())
      .append(System.lineSeparator());
    }
    return result.toString();
  }
}
