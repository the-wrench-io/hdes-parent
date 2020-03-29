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

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CellExpressionString {
  public static Builder builder(ObjectMapper objectMapper) {
    return new Builder(objectMapper);
  }

  public static class Builder {
    private final ObjectMapper objectMapper;

    private Builder(ObjectMapper objectMapper) {
      super();
      this.objectMapper = objectMapper;
    }

    @SuppressWarnings("unchecked")
    public String build(String src, String value) {
      try {
        if (src.indexOf("[") > -1) {
          List<String> values = objectMapper.readValue(src.substring(src.indexOf("[")), List.class);
          boolean contains = src.startsWith("in") ? true : false;
          return contains ? in(values, value) : notIn(values, value);
        } else {
          return in(Arrays.asList(src), value);
        }
      } catch (IOException e) {
        throw new RuntimeException(e.getMessage(), e);
      }
    }

    private static String in(Collection<String> constant, String input) {
      return "evalString().in(" + input + ", " + values(constant) + ")";
    }

    private static String notIn(Collection<String> constant, String input) {
      return "evalString().notIn(" + input + ", " + values(constant) + ")";
    }
    private static String values(Collection<String> constants) {
      StringBuilder result = new StringBuilder();
      for (String constant : constants) {
        if (result.length() > 0) {
          result.append(", ");
        }
        result.append("\"").append(constant).append("\"");
      }
      return result.toString();
    }
  }
}
