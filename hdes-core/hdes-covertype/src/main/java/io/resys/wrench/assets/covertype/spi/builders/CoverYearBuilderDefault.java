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

import java.util.Optional;

import io.resys.wrench.assets.covertype.api.CoverRepository.CoverMonthType;
import io.resys.wrench.assets.covertype.api.CoverRepository.CoverYear;
import io.resys.wrench.assets.covertype.api.CoverRepository.CoverYearBuilder;
import io.resys.wrench.assets.covertype.api.CoverRepository.CoverYearType;
import io.resys.wrench.assets.covertype.api.ImmutableCoverYear;

public class CoverYearBuilderDefault implements CoverYearBuilder {

  private Integer daysInMonth;
  private Integer daysInYear;
  
  @Override
  public CoverYearBuilder daysInMonth(int daysInMonth) {
    this.daysInMonth = daysInMonth;
    return this;
  }
  @Override
  public CoverYearBuilder daysInYear(int daysInYear) {
    this.daysInYear = daysInYear;
    return this;
  }
  @Override
  public CoverYear build() {
    return ImmutableCoverYear.builder()
        .daysInMonth(Optional.ofNullable(daysInMonth))
        .daysInYear(Optional.ofNullable(daysInYear))
        .year(daysInYear == null ? CoverYearType.NATURAL : CoverYearType.CUSTOM)
        .month(daysInMonth == null ? CoverMonthType.NATURAL : CoverMonthType.CUSTOM)
        .build();
  }
}
