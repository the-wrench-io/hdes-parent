package io.resys.wrench.assets.covertype.spi.builders;

/*-
 * #%L
 * hdes-covertype
 * %%
 * Copyright (C) 2020 - 2021 Copyright 2020 ReSys OÜ
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

import java.time.LocalDate;

import io.resys.wrench.assets.covertype.api.CoverRepository.CoverPeriod;
import io.resys.wrench.assets.covertype.api.CoverRepository.CoverPeriodBuilder;
import io.resys.wrench.assets.covertype.api.ImmutableCoverPeriod;
import io.resys.wrench.assets.covertype.spi.util.CoverAssert;

public class CoverPeriodBuilderDefault implements CoverPeriodBuilder {

  private LocalDate startDate;
  private LocalDate endDate;
  private LocalDate dueDate;
  private Integer lengthInMonths;
  
  @Override
  public CoverPeriodBuilder startDate(LocalDate startDate) {
    this.startDate = startDate;
    return this;
  }
  @Override
  public CoverPeriodBuilder endDate(LocalDate endDate) {
    this.endDate = endDate;
    return this;
  }
  @Override
  public CoverPeriodBuilder lengthInMonths(Integer lengthInMonths) {
    this.lengthInMonths = lengthInMonths;
    return this;
  }
  @Override
  public CoverPeriodBuilder dueDate(LocalDate dueDate) {
    this.dueDate = dueDate;
    return this;
  }
  @Override
  public CoverPeriod build() {
    CoverAssert.notNull(startDate, () -> "startDate needs to be set!");
    CoverAssert.notNull(endDate, () -> "endDate needs to be set!");
    CoverAssert.notNull(dueDate, () -> "dueDate needs to be set!");
    CoverAssert.isTrue(startDate.isEqual(endDate) || startDate.isBefore(endDate), () -> "startDate needs to be before endDate!");
    CoverAssert.isTrue(lengthInMonths > 0 && lengthInMonths < 13, () -> "lengthInMonths needs to be between 1-12 but was: " + lengthInMonths + "!");
    
    return ImmutableCoverPeriod.builder()
        .endDate(endDate)
        .startDate(startDate)
        .dueDate(dueDate)
        .months(lengthInMonths)
        .build();
  }

}
