package io.resys.hdes.aproc.spi.generator.dt.expressions;

/*-
 * #%L
 * hdes-aproc
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

public class CellExpressionLogical {
  public static String and(String... values) {
    StringBuilder result = new StringBuilder("(");
    for (String value : values) {
      if (result.length() > 1) {
        result.append(" && ");
      }
      result.append(value);
    }
    return result.append(")").toString();
  }

  public static String not(String values) {
    return "!" + values;
  }
}
