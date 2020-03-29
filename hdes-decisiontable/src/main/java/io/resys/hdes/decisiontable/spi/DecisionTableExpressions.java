package io.resys.hdes.decisiontable.spi;

/*-
 * #%L
 * hdes-decisiontable
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DecisionTableExpressions {
  private static final EvalBigDecimal BIG_DECIMAL = new EvalBigDecimal();
  private static final EvalLong LONG = new EvalLong();
  private static final EvalInteger INTEGER = new EvalInteger();
  private static final EvalBoolean BOOLEAN = new EvalBoolean();

  private static final EvalDateTime DATE_TIME = new EvalDateTime();
  private static final EvalDate DATE = new EvalDate();
  private static final EvalString STRING = new EvalString();

  public static EvalString evalString() {
    return STRING;
  }
 
  public static EvalBigDecimal evalDecimal() {
    return BIG_DECIMAL;
  }

  public static EvalLong evalLong() {
    return LONG;
  }

  public static EvalInteger evalInteger() {
    return INTEGER;
  }

  public static EvalBoolean evalBoolean() {
    return BOOLEAN;
  }

  public static EvalDate evalDate() {
    return DATE;
  }
  
  public static EvalDateTime evalDateTime() {
    return DATE_TIME;
  }
  
  public static class EvalString {
    public boolean in(Object value, String ... constants) {
      String parameter = (String) value;
      
      for(String constant : constants) {
        if(constant.equals(parameter)) {
          return true;
        }
      }
      
      return false;
    }
    public boolean notIn(Object value, String ... constants) {
      String parameter = (String) value;
      
      for(String constant : constants) {
        if(constant.equals(parameter)) {
          return false;
        }
      }
      return true;
    }
  }
  
  public static class EvalDateTime {
    public boolean eq(LocalDateTime constant, Object value) {
      LocalDateTime parameter = (LocalDateTime) value;
      return parameter.isEqual(constant);
    }
    public boolean before(LocalDateTime constant, Object value) {
      LocalDateTime parameter = (LocalDateTime) value;
      return parameter.isBefore(constant);
    }
    
    public boolean after(LocalDateTime constant, Object value) {
      LocalDateTime parameter = (LocalDateTime) value;
      return parameter.isAfter(constant);
    }    
  }
  
  public static class EvalDate {
    public boolean eq(LocalDate constant, Object value) {
      LocalDate parameter = (LocalDate) value;
      return parameter.isEqual(constant);
    }
    
    public boolean before(LocalDate constant, Object value) {
      LocalDate parameter = (LocalDate) value;
      return parameter.isBefore(constant);
    }
    
    public boolean after(LocalDate constant, Object value) {
      LocalDate parameter = (LocalDate) value;
      return parameter.isAfter(constant);
    }    
  }
  
  
  public static class EvalBoolean {
    public boolean eq(boolean constant, Object value) {
      boolean parameter = (boolean) value;
      return constant == parameter;
    }
  }

  public static class EvalBigDecimal {
    public boolean gt(BigDecimal constant, Object value) {
      BigDecimal parameter = (BigDecimal) value;
      return parameter.compareTo(constant) > 0;      
    }

    public boolean gte(BigDecimal constant, Object value) {
      BigDecimal parameter = (BigDecimal) value;
      return parameter.compareTo(constant) >= 0;
    }

    public boolean lt(BigDecimal constant, Object value) {
      BigDecimal parameter = (BigDecimal) value;
      return parameter.compareTo(constant) < 0;      
    }

    public boolean lte(BigDecimal constant, Object value) {
      BigDecimal parameter = (BigDecimal) value;
      return parameter.compareTo(constant) <= 0;
    }

    public boolean eq(BigDecimal constant, Object value) {
      BigDecimal parameter = (BigDecimal) value;
      return parameter.compareTo(constant) == 0;
    }
  }

  public static class EvalInteger {
    public boolean gt(int constant, Object value) {
      int parameter = (int) value;
      return parameter > constant;
    }

    public boolean gte(int constant, Object value) {
      int parameter = (int) value;
      return parameter >= constant;
    }

    public boolean lt(int constant, Object value) {
      int parameter = (int) value;
      return parameter < constant;      
    }

    public boolean lte(int constant, Object value) {
      int parameter = (int) value;
      return parameter <= constant;
    }

    public boolean eq(int constant, Object value) {
      int parameter = (int) value;
      return parameter == constant;
    }
  }

  public static class EvalLong {
    public boolean gt(long constant, Object value) {
      long parameter = (long) value;
      return parameter > constant;
    }

    public boolean gte(long constant, Object value) {
      long parameter = (long) value;
      return parameter >= constant;
    }

    public boolean lt(long constant, Object value) {
      long parameter = (long) value;
      return parameter < constant;   
    }

    public boolean lte(long constant, Object value) {
      long parameter = (long) value;
      return parameter <= constant;
    }

    public boolean eq(long constant, Object value) {
      long parameter = (long) value;
      return parameter == constant;
    }
  }
}
