package io.resys.hdes.aproc.spi.generator.dt.expressions;

import java.math.BigDecimal;

/*-
 * #%L
 * wrench-assets-dt
 * %%
 * Copyright (C) 2016 - 2019 Copyright 2016 ReSys OÜ
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

import io.resys.hdes.datatype.api.DataType.ValueType;

public class CellExpressionNumber {


  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private Builder() {
      super();
    }

    public String build(String src, ValueType valueType, String input) {
      // Comparison
      if(!(src.startsWith("[") || src.startsWith("("))) {
        String number = parseNumber(src);
        if(src.startsWith("<=")) {
          return lteThen(valueType, number, input);  
        } else if(src.startsWith("<")) {
          return ltThen(valueType, number, input);
        } else if(src.startsWith(">=")) {
          return gteThen(valueType, number, input);
        } else if(src.startsWith(">")) {
          return gtThen(valueType, number, input);
        }
        return eq(valueType, number, input);
      }

      // Range
      String[] segments = src.substring(1, src.length()-1).split("\\..");
      String start = segments[0];
      String end = segments[1];
      boolean startInclude = src.startsWith("[");
      boolean endInclude = src.endsWith("]");

      return CellExpressionLogical.and(
          startInclude ? gteThen(valueType, start, input) : gtThen(valueType, start, input),
          endInclude ? lteThen(valueType, end, input) : ltThen(valueType, end, input));
    } 
  }

  public static String parseNumber(String value) {
    try {
      String possibleNumber = null;
      if(value.startsWith("<=") || value.startsWith(">=")) {
        possibleNumber = value.substring(2);
      } else if(value.startsWith("<") || value.startsWith(">")) {
        possibleNumber = value.substring(1);
      } else if(value.startsWith("=")) {
        possibleNumber = value.substring(1);
      }
      if(possibleNumber.length() > 0) {
        return possibleNumber.trim();
      }
    } catch(Exception e) {
    }
    return null;
  }

  
  private static String gtThen(ValueType valueType, String constant, String input) {
    switch(valueType) {
    case DECIMAL: return gtThen(new BigDecimal(constant), input);
    case LONG: return gtThen(Long.parseLong(constant), input);
    case INTEGER: return gtThen(Integer.parseInt(constant), input);
    default: throw new IllegalArgumentException(String.format("unknown value type: %s", valueType));
    }
  }
  private static String gteThen(ValueType valueType, String constant, String input) {
    switch(valueType) {
    case DECIMAL: return gteThen(new BigDecimal(constant), input);
    case LONG: return gteThen(Long.parseLong(constant), input);
    case INTEGER: return gteThen(Integer.parseInt(constant), input);
    default: throw new IllegalArgumentException(String.format("unknown value type: %s", valueType));
    }
  }
  private static String ltThen(ValueType valueType, String constant, String input) {
    switch(valueType) {
    case DECIMAL: return ltThen(new BigDecimal(constant), input);
    case LONG: return ltThen(Long.parseLong(constant), input);
    case INTEGER: return ltThen(Integer.parseInt(constant), input);
    default: throw new IllegalArgumentException(String.format("unknown value type: %s", valueType));
    }   
  }
  private static String lteThen(ValueType valueType, String constant, String input) {
    switch(valueType) {
    case DECIMAL: return lteThen(new BigDecimal(constant), input);
    case LONG: return lteThen(Long.parseLong(constant), input);
    case INTEGER: return lteThen(Integer.parseInt(constant), input);
    default: throw new IllegalArgumentException(String.format("unknown value type: %s", valueType));
    }
  }
  private static String eq(ValueType valueType, String constant, String input) {
    switch(valueType) {
    case DECIMAL: return eq(new BigDecimal(constant), input);
    case LONG: return eq(Long.parseLong(constant), input);
    case INTEGER: return eq(Integer.parseInt(constant), input);
    default: throw new IllegalArgumentException(String.format("unknown value type: %s", valueType));
    }
  }


  private static String gtThen(long constant, String input) {
    return "evalLong().gt(" + constant + ", " + input + ")";
  }
  private static String gteThen(long constant, String input) {
    return "evalLong().gte(" + constant + ", " + input + ")";
  }
  private static String ltThen(long constant, String input) {
    return "evalLong().lt(" + constant + ", " + input + ")";
  }
  private static String lteThen(long constant, String input) {
    return "evalLong().lte(" + constant + ", " + input + ")";
  }
  private static String eq(long constant, String input) {
    return "evalLong().eq(" + constant + ", " + input + ")";
  }

  private static String gtThen(int constant, String input) {
    return "evalInteger().gt(" + constant + ", " + input + ")";
    //return (Integer parameter) -> parameter > constant;      
  }
  private static String gteThen(int constant, String input) {
    return "evalInteger().gte(" + constant + ", " + input + ")";
    //return (Integer parameter) -> parameter >= constant;
  }
  private static String ltThen(int constant, String input) {
    return "evalInteger().lt(" + constant + ", " + input + ")";
    //return (Integer parameter) -> parameter < constant;      
  }
  private static String lteThen(int constant, String input) {
    return "evalInteger().lte(" + constant + ", " + input + ")";
    //return (Integer parameter) -> parameter <= constant;
  }
  private static String eq(int constant, String input) {
    return "evalInteger().eq(" + constant + ", " + input + ")"; 
    //return (Integer parameter) -> parameter == constant;
  }

  private static String gtThen(BigDecimal constant, String input) {
    return "evalDecimal().gt(new BigDecimal(\"" + constant.toString() + "\"), " + input + ")"; 
  }
  private static String gteThen(BigDecimal constant, String input) {
    return "evalDecimal().gte(new BigDecimal(\"" + constant.toString() + "\"), " + input + ")";
  }
  private static String ltThen(BigDecimal constant, String input) {
    return "evalDecimal().lt(new BigDecimal(\"" + constant.toString() + "\"), " + input + ")";
  }
  private static String lteThen(BigDecimal constant, String input) {
    return "evalDecimal().lte(new BigDecimal(\"" + constant.toString() + "\"), " + input + ")";
  }
  private static String eq(BigDecimal constant, String input) {
    return "evalDecimal().eq(new BigDecimal(\"" + constant.toString() + "\"), " + input + ")";
  }  
}
