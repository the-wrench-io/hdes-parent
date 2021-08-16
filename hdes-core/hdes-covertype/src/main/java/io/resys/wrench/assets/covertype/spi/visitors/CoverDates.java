package io.resys.wrench.assets.covertype.spi.visitors;

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

import io.resys.wrench.assets.covertype.api.CoverRepository.CoverDetail;

public class CoverDates {
  public static boolean in(CoverDetail detail, LocalDate startDate, LocalDate endDate) {
    // ended details
    if(detail.getStartDate().isAfter(endDate)) {
      return false;
    }
    if(detail.getEndDate().isBefore(startDate)) {
      return false;
    }

    if(detail.getStartDate().isBefore(startDate)) {
      return true;
    }
    
    if(detail.getStartDate().isEqual(startDate)) {
      return true;
    }
    
    return false;
  }
  
  public static LocalDate toDueDate(LocalDate dueDate, LocalDate targetDate) {
    int interestDay = dueDate.getDayOfMonth();
    int maxDay = targetDate.lengthOfMonth();
    int dueDay = maxDay > interestDay ? interestDay : maxDay;
    return targetDate.withDayOfMonth(dueDay);
  }
}
