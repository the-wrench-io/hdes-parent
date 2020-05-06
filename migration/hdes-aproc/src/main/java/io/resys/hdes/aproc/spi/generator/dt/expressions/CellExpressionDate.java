package io.resys.hdes.aproc.spi.generator.dt.expressions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

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

public class CellExpressionDate extends CellExpressionLogical {
  private final static String AFTER = "after";
  private final static String BEFORE = "before";
  private final static String BETWEEN = "between";
  private final static String EQUALS = "equals";
  
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    public String build(String value, ValueType valueType, String input) {
      if(value.startsWith(BETWEEN)) {
        String[] segments = value.substring(7).split("and");

        String start = segments[0].trim();
        String end = segments[1].trim();
        return CellExpressionLogical.and(
            CellExpressionLogical.not(isBefore(valueType, start, input)),
            CellExpressionLogical.not(isAfter(valueType, end, input)));
      }

      if(value.startsWith(EQUALS)) {
        String date = value.substring(EQUALS.length()).trim();
        return isEqual(valueType, date, input);
      } else if(value.startsWith(BEFORE)) {
        String date = value.substring(BEFORE.length()).trim();
        return isBefore(valueType, date, input);
      } else if(value.startsWith(AFTER)) {
        String date = value.substring(AFTER.length()).trim();
        return isAfter(valueType, date, input);
      }
      throw new IllegalArgumentException(String.format("unknown value type: %s", valueType));
    }
  }
  
  private static String isEqual(ValueType valueType, String constant, String input) {
    switch(valueType) {
    case DATE_TIME: return isEqual(parseLocalDateTime(constant), input);
    case DATE: return isEqual(parseLocalDate(constant), input);
    default: throw new IllegalArgumentException(String.format("unknown value type: %s", valueType));
    }
  }
  private static String isEqual(LocalDateTime constant, String input) {
    return "evalDateTime().eq(LocalDateTime.parse(\"" + constant + "\"), " + input + ")";
  }
  private static String isEqual(LocalDate constant, String input) {
    return "evalDate().eq(LocalDate.parse(\"" + constant + "\"), " + input + ")";
  }
  
  
  private static String isBefore(ValueType valueType, String constant, String input) {
    switch(valueType) {
    case DATE_TIME: return isBefore(parseLocalDateTime(constant), input);
    case DATE: return isBefore(parseLocalDate(constant), input);
    default: throw new IllegalArgumentException(String.format("unknown value type: {}", valueType));
    }
  }
  private static String isBefore(LocalDateTime constant, String input) {
    return "evalDateTime().before(LocalDateTime.parse(\"" + constant + "\"), " + input + ")";
  }
  private static String isBefore(LocalDate constant, String input) {
    return "evalDate().before(LocalDate.parse(\"" + constant + "\"), " + input + ")";
  }
  
  private static String isAfter(ValueType valueType, String constant, String input) {
    switch(valueType) {
    case DATE_TIME: return isAfter(parseLocalDateTime(constant), input);
    case DATE: return isAfter(parseLocalDate(constant), input);
    default: throw new IllegalArgumentException(String.format("unknown value type: {}", valueType));
    }
  }
  private static String isAfter(LocalDateTime constant, String input) {
    return "evalDateTime().after(LocalDateTime.parse(\"" + constant + "\"), " + input + ")";
  }
  private static String isAfter(LocalDate constant, String input) {
    return "evalDate().after(LocalDate.parse(\"" + constant + "\"), " + input + ")";
  }


  public static LocalDateTime parseLocalDateTime(String date) {
    try {
      if(date.length() == 16) {
        return LocalDateTime.parse(date);
      }
      if(date.length() == 10) {
        return LocalDate.parse(date).atStartOfDay();
      }
      return LocalDateTime.ofInstant(ZonedDateTime.parse(date).toInstant(), ZoneId.systemDefault());
    } catch(Exception e) {
      throw new IllegalArgumentException("Incorrect date time: '" + date + "', correct format: YYYY-MM-DDThh:mm:ssTZD, example: 2017-07-03T00:00:00Z!");
    }
  }

  public static LocalDate parseLocalDate(String date) {
    try {
      if(date.length() > 10) {
        return LocalDate.parse(date.substring(0, 10));
      }
      return LocalDate.parse(date);
    } catch(Exception e) {
      throw new IllegalArgumentException("Incorrect date: '" + date + "', correct format: YYYY-MM-DD, example: 2017-07-03!");
    }
  }
}
