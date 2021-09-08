package io.resys.wrench.assets.covertype.test;

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

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import io.resys.wrench.assets.covertype.api.CoverRepository;
import io.resys.wrench.assets.covertype.api.CoverRepository.CoverBuilder;
import io.resys.wrench.assets.covertype.api.CoverRepository.Projection;
import io.resys.wrench.assets.covertype.api.CoverRepository.ProjectionBuilder;
import io.resys.wrench.assets.covertype.spi.CoverRepositoryDefault;


@RunWith(BlockJUnit4ClassRunner.class)
public class CalculationPeriodTest {
  private static CoverRepository repo = CoverRepositoryDefault.builder().build();
  
  @Test
  public void coverWith2AmountsAndNaturalCalendar() throws IOException {
    ProjectionBuilder builder = repo.projection();
    
    CoverBuilder coverBuilder = builder.cover()
      .id("1").origin(getClass())
      .startDate(LocalDate.of(2000, 1, 1))
      .endDate(LocalDate.of(2000, 12, 31))
      .type("insurance-cover");

    coverBuilder.addPeriod()
      .lengthInMonths(6)
      .startDate(LocalDate.of(2000, 5, 1))
      .endDate(LocalDate.of(2002, 12, 31))
      .dueDate(LocalDate.of(2000, 5, 1))
      .build();
    
    coverBuilder.addDetail()
      .id("").origin(getClass())
      .startDate(LocalDate.of(2000, 1, 1))
      .endDate(LocalDate.of(2000, 7, 4))
      .type("insurance-amount-1")
      .build();
  
    coverBuilder.addDetail()
      .id("").origin(getClass())
      .startDate(LocalDate.of(2000, 7, 5))
      .endDate(LocalDate.of(2000, 12, 31))
      .type("cover-amount-2")
      .build();
    coverBuilder.build();

    
    Projection calculation = builder.lastDueDate(LocalDate.of(1999, 12, 31)).build();
    var actual = prettyPrint(calculation);
    var expected = TestUtils.toString(getClass(), "policy-period-with-2-amounts.txt");
    Assert.assertEquals(expected, actual); 
  }
  
  @Test
  public void coverWith2AmountsAndNaturalCalendarWith1DayDetails() throws IOException {
    ProjectionBuilder builder = repo.projection();
    
    CoverBuilder coverBuilder = builder.cover()
      .id("1").origin(getClass())
      .startDate(LocalDate.of(2000, 1, 1))
      .endDate(LocalDate.of(2000, 12, 31))
      .type("insurance-cover");

    coverBuilder.addPeriod().lengthInMonths(6)
    .startDate(LocalDate.of(2000, 5, 1))
    .dueDate(LocalDate.of(2000, 5, 20))
    .endDate(LocalDate.of(2002, 12, 31))
    .build();
    
    coverBuilder.addDetail()
      .id("").origin(getClass())
      .startDate(LocalDate.of(2000, 1, 1))
      .endDate(LocalDate.of(2000, 7, 4))
      .type("insurance-amount-1")
      .build();
  
    coverBuilder.addDetail()
      .id("").origin(getClass())
      .startDate(LocalDate.of(2000, 7, 4))
      .endDate(LocalDate.of(2000, 7, 4))
      .type("discount-1")
      .build();

    
    coverBuilder.addDetail()
      .id("").origin(getClass())
      .startDate(LocalDate.of(2000, 7, 5))
      .endDate(LocalDate.of(2000, 12, 31))
      .type("cover-amount-2")
      .build();
    coverBuilder.build();

    
    Projection calculation = builder.lastDueDate(LocalDate.of(1999, 12, 31)).build();
    var actual = prettyPrint(calculation);
    var expected = TestUtils.toString(getClass(), "policy-period-with-splits.txt");
    Assert.assertEquals(expected, actual); 
  }

  
  @Test
  public void coverWith2AmountsAndNaturalCalendarWith1DayDetailsAndFragments() throws IOException {
    ProjectionBuilder builder = repo.projection();
  
    
    CoverBuilder coverBuilder = builder.cover()
      .id("1").origin(getClass())
      .startDate(LocalDate.of(2000, 1, 1))
      .endDate(LocalDate.of(2000, 12, 31))
      .type("insurance-cover");

    coverBuilder.addPeriod().lengthInMonths(6)
    .startDate(LocalDate.of(2000, 5, 20))
    .endDate(LocalDate.of(2002, 12, 9))
    .dueDate(LocalDate.of(2002, 12, 19))
    .build();
  
    coverBuilder.addDetail()
      .id("").origin(getClass())
      .startDate(LocalDate.of(2000, 1, 1))
      .endDate(LocalDate.of(2000, 7, 4))
      .type("insurance-amount-1")
      .build();
  
    coverBuilder.addDetail()
      .id("").origin(getClass())
      .startDate(LocalDate.of(2000, 7, 4))
      .endDate(LocalDate.of(2000, 7, 4))
      .type("discount-1")
      .build();

    
    coverBuilder.addDetail()
      .id("").origin(getClass())
      .startDate(LocalDate.of(2000, 7, 5))
      .endDate(LocalDate.of(2000, 12, 31))
      .type("cover-amount-2")
      .build();
    coverBuilder.build();

    
    Projection calculation = builder.lastDueDate(LocalDate.of(1999, 12, 31)).build();
    var actual = prettyPrint(calculation);
    var expected = TestUtils.toString(getClass(), "policy-period-with-2-amounts-and-1-day-details.txt");
    Assert.assertEquals(expected, actual); 
  }
  
  private static String prettyPrint(Projection calculation) {
    StringBuilder result = new StringBuilder();
    final var periods = new ArrayList<>(calculation.getProjectionPeriods());
    Collections.sort(periods, (a, b) -> a.getStartDate().compareTo(b.getStartDate()));
    
    int index = 0;
    for(final var period : periods) {
      if(index != period.getStartDate().getYear()) {
        index = period.getStartDate().getYear();
        result.append("  - ").append(index)
        .append(System.lineSeparator());
        
      }
      result
        .append("    ")
        .append(period.getStartDate().getMonthValue()).append(": ")
        .append(period.getStartDate()).append(" - ").append(period.getEndDate())
        .append(System.lineSeparator());
        
      for(final var detail : period.getProjectionDetails()) {
        result
        .append("       - ")
        .append(detail.getStartDate()).append(" - ").append(detail.getEndDate())
        .append(System.lineSeparator());
      }
    }
    return result.toString();
  }
}
