package io.resys.hdes.compiler.spi;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import io.resys.hdes.compiler.api.HdesWhen;

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

public class HdesWhenGen implements HdesWhen {
  private final static HdesWhenGen IMPL = new HdesWhenGen();
  
  public static HdesWhen get() {
    return IMPL;
  }
  
  //string
  @Override
  public List<String> asList(String ... values) {
    return Arrays.asList(values);
  }
  
  //integer
  @Override
  public boolean lt(int arg0, int arg1) {
    return arg0 < arg1;
  }
  @Override
  public boolean lte(int arg0, int arg1) {
    return arg0 <= arg1; 
  }
  @Override
  public boolean gt(int arg0, int arg1) {
    return arg0 > arg1;
  }
  @Override
  public boolean gte(int arg0, int arg1) {
    return arg0 >= arg1;
  }
  @Override
  public boolean eq(int arg0, int arg1) {
    return arg0 == arg1;
  }
  @Override
  public boolean neq(int arg0, int arg1) {
    return arg0 != arg1;
  }
  @Override
  public boolean between(int arg, int left, int right) {
    return left <= arg && right >= arg;
  }
  
  //big decimal
  @Override
  public boolean lt(BigDecimal arg0, BigDecimal arg1) {
    return arg0.compareTo(arg1) == -1;
  }
  @Override
  public boolean lte(BigDecimal arg0, BigDecimal arg1) {
    return arg0.compareTo(arg1) <= 0;    
  }
  @Override
  public boolean gt(BigDecimal arg0, BigDecimal arg1) {
    return arg0.compareTo(arg1) > -1;
  }
  @Override
  public boolean gte(BigDecimal arg0, BigDecimal arg1) {
    return arg0.compareTo(arg1) >= 0;
  }
  @Override
  public boolean eq(BigDecimal arg0, BigDecimal arg1) {
    return arg0.compareTo(arg1) == 0;
  }
  @Override
  public boolean neq(BigDecimal arg0, BigDecimal arg1) {
    return arg0.compareTo(arg1) != 0;
  }
  @Override
  public boolean between(BigDecimal arg, BigDecimal left, BigDecimal right) {
    return lte(left, arg) && gte(right, arg);
  }
  
  //date
  @Override
  public boolean lt(LocalDate arg0, LocalDate arg1) {
    return arg0.isBefore(arg1);
  }
  @Override
  public boolean lte(LocalDate arg0, LocalDate arg1) {
    return arg0.isBefore(arg1) || arg0.equals(arg1);
  }
  @Override
  public boolean gt(LocalDate arg0, LocalDate arg1) {
    return arg0.isAfter(arg1);
  }
  @Override
  public boolean gte(LocalDate arg0, LocalDate arg1) {
    return arg0.isAfter(arg1) || arg0.equals(arg1);
  }
  @Override
  public boolean eq(LocalDate arg0, LocalDate arg1) {
    return arg0.equals(arg1);
  }
  @Override
  public boolean neq(LocalDate arg0, LocalDate arg1) {
    return !arg0.equals(arg1);
  }
  @Override
  public boolean between(LocalDate arg, LocalDate left, LocalDate right) {
    return lte(left, arg) && gte(right, arg);
  }
  
  //time
  @Override
  public boolean lt(LocalTime arg0, LocalTime arg1) {
    return arg0.isBefore(arg1);
  }
  @Override
  public boolean lte(LocalTime arg0, LocalTime arg1) {
    return arg0.isBefore(arg1) || arg0.equals(arg1);
  }
  @Override
  public boolean gt(LocalTime arg0, LocalTime arg1) {
    return arg0.isAfter(arg1);
  }
  @Override
  public boolean gte(LocalTime arg0, LocalTime arg1) {
    return arg0.isAfter(arg1) || arg0.equals(arg1);
  }
  @Override
  public boolean eq(LocalTime arg0, LocalTime arg1) {
    return arg0.equals(arg1);
  }
  @Override
  public boolean neq(LocalTime arg0, LocalTime arg1) {
    return !arg0.equals(arg1);
  }
  @Override
  public boolean between(LocalTime arg, LocalTime left, LocalTime right) {
    return lte(left, arg) && gte(right, arg);
  }
  
  //date time
  @Override
  public boolean lt(LocalDateTime arg0, LocalDateTime arg1) {
    return arg0.isBefore(arg1);    
  }
  @Override
  public boolean lte(LocalDateTime arg0, LocalDateTime arg1) {
    return arg0.isBefore(arg1) || arg0.equals(arg1);
  }
  @Override
  public boolean gt(LocalDateTime arg0, LocalDateTime arg1) {
    return arg0.isAfter(arg1);
  }
  @Override
  public boolean gte(LocalDateTime arg0, LocalDateTime arg1) {
    return arg0.isAfter(arg1) || arg0.equals(arg1); 
  }
  @Override
  public boolean eq(LocalDateTime arg0, LocalDateTime arg1) {
    return arg0.equals(arg1);
  }
  @Override
  public boolean neq(LocalDateTime arg0, LocalDateTime arg1) {
    return !arg0.equals(arg1);
  }
  @Override
  public boolean between(LocalDateTime arg, LocalDateTime left, LocalDateTime right) {
    return lte(left, arg) && gte(right, arg);
  }
}
