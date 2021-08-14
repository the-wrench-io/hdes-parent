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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import io.resys.wrench.assets.covertype.api.CoverRepository.Cover;
import io.resys.wrench.assets.covertype.api.CoverRepository.CoverDetail;
import io.resys.wrench.assets.covertype.api.CoverRepository.CoverPeriod;
import io.resys.wrench.assets.covertype.api.CoverRepository.Projection;
import io.resys.wrench.assets.covertype.api.CoverRepository.ProjectionDetail;
import io.resys.wrench.assets.covertype.api.CoverRepository.ProjectionPeriod;
import io.resys.wrench.assets.covertype.api.CoverRepository.ProjectionPeriodMonths;
import io.resys.wrench.assets.covertype.api.ImmutableCoverPeriod;
import io.resys.wrench.assets.covertype.api.ImmutableProjection;
import io.resys.wrench.assets.covertype.api.ImmutableProjectionDetail;
import io.resys.wrench.assets.covertype.api.ImmutableProjectionPeriod;
import io.resys.wrench.assets.covertype.api.ImmutableProjectionPeriodMonths;


public class CoverVisitor {

  private final Cover cover;
  private final LocalDate markerDate;
  
  public CoverVisitor(Cover cover, LocalDate markerDate) {
    super();
    this.cover = cover;
    this.markerDate = markerDate;
  }
  
  public Projection visit() {
    return ImmutableProjection.builder()
        .cover(cover)
        .projectionPeriods(visitCoverPeriods(cover.getPeriods()))
        .build();
  }
  
  private List<ProjectionPeriod> visitCoverPeriods(List<CoverPeriod> periods) {
    final var result = new ArrayList<ProjectionPeriod>();
    final var year = markerDate.getYear();
    final var yearNext = year + 1;

    
    CoverPeriod previous = null;
    for(final var current : periods) {
      final var startDate = visitStartDate(previous, current); 
      final var endDate = visitEndDate(current);
 
      final var inYear = startDate.getYear() <= year;
      final var inNextYear = startDate.getYear() <= yearNext;      
      
      LocalDate failsafeEnd = endDate;
      if(failsafeEnd.isAfter(cover.getEndDate())) {
        failsafeEnd = cover.getEndDate();
      }


      if(inYear || inNextYear) {
        result.addAll(visitCoverPeriod(
            ImmutableCoverPeriod.builder()
            .startDate(startDate)
            .endDate(failsafeEnd)
            .dueDate(current.getDueDate())
            .months(current.getMonths())
            .build()));
      }
      
      if(startDate.getYear() > yearNext) {
        break;
      }
      previous = current;
    }
    return result;
  }

  private LocalDate visitStartDate(CoverPeriod previous, CoverPeriod current) {
    // no history, get start date is null
    if(previous == null) {
      return current.getStartDate();
    }
    
    return CoverDates.toDueDate(previous.getDueDate(), 
        previous.getEndDate().withDayOfMonth(1).plusMonths(previous.getMonths()))
        .plusDays(1);
  }
  
  private LocalDate visitEndDate(CoverPeriod current) {
    if(current.getDueDate().getDayOfMonth() == current.getEndDate().getDayOfMonth()) {
      return current.getEndDate();
    }
    return CoverDates.toDueDate(current.getDueDate(), 
        current.getEndDate().withDayOfMonth(1).plusMonths(current.getMonths()));
  }

  private List<ProjectionPeriod> visitCoverPeriod(CoverPeriod coverPeriod) {
    final List<ProjectionPeriod> result = new ArrayList<>();
    ProjectionPeriod period = null;
    while(period == null || period.getEndDate().compareTo(coverPeriod.getEndDate()) < 0) {

      final LocalDate dueDateDelta;
      if(period == null && coverPeriod.getStartDate().equals(coverPeriod.getDueDate())) {
        dueDateDelta = coverPeriod.getStartDate().withDayOfMonth(1).plusMonths(coverPeriod.getMonths());
      } else {
        dueDateDelta = period == null ? coverPeriod.getStartDate() : period.getEndDate().plusMonths(coverPeriod.getMonths());
      }
      
      
      final var dueDate = CoverDates.toDueDate(coverPeriod.getDueDate(), dueDateDelta);
      final var startDate = period == null ? coverPeriod.getStartDate() : period.getEndDate().plusDays(1);
      
      period = ImmutableProjectionPeriod.builder()
          .startDate(startDate)
          .endDate(dueDate)
          .projectionMonths(visitProjectionPeriodMonths(startDate, dueDate))
          .projectionDetails(visitProjectionDetails(startDate, dueDate))
          .build();
      
      result.add(period);
    }
    
    return result;
  }
  
    
  private ProjectionPeriodMonths visitProjectionPeriodMonths(LocalDate startDate, LocalDate endDate) {
    final var rolledEndDate = endDate.plusDays(1);
    final var period = Period.between(startDate, rolledEndDate);
    final var totalDays = Duration.between(startDate.atStartOfDay(), rolledEndDate.atStartOfDay()).toDays();
    

    Optional<BigDecimal> startPerc = Optional.empty();
    if(startDate.getDayOfMonth() != 1) {
      startPerc = Optional.of(visitPercOfMonth(startDate));
    }
    
    Optional<BigDecimal> endPerc = Optional.empty();
    if(startDate.getMonthValue() != endDate.getMonthValue() && endDate.getDayOfMonth() != endDate.lengthOfMonth()) {
      endPerc = Optional.of(visitPercOfMonth(endDate));
    }
    
    return ImmutableProjectionPeriodMonths.builder()
        .totalDays(totalDays)
        .days(period.getDays())
        .startPerc(startPerc)
        .endPerc(endPerc)
        .months(period.getMonths())
        .build();
  }
  
  private BigDecimal visitPercOfMonth(LocalDate target) {
    final var dayOfMonth = target.getDayOfMonth();
    final var lengthOfMonth = target.lengthOfMonth();
    
    final var result = BigDecimal.valueOf(dayOfMonth)
        .multiply(BigDecimal.valueOf(100))
        .divide(BigDecimal.valueOf(lengthOfMonth), RoundingMode.HALF_EVEN)
        .setScale(2, RoundingMode.HALF_EVEN);
    return result;
  }

  private List<ProjectionDetail> visitProjectionDetails(LocalDate startDate, LocalDate endDate) {
    final List<ProjectionDetail> result = new ArrayList<>();
    final var rolledEndDate = endDate.plusDays(1);
    
    // gather crossing date
    final List<LocalDate> distortions = new ArrayList<>();
    distortions.add(startDate);
    for(final var coverDetail : cover.getDetails()) {

      // start date
      final var detailStart = coverDetail.getStartDate();
      if( detailStart.isAfter(startDate) && detailStart.isBefore(rolledEndDate) && !distortions.contains(detailStart) ) {
        distortions.add(detailStart);
      }

      // end date
      final var detailEnd = coverDetail.getEndDate();
      if( detailEnd.isAfter(startDate) && detailEnd.isBefore(rolledEndDate) && !distortions.contains(detailEnd)) {
        if(!endDate.equals(detailEnd)) {
          // start of next distortion
          distortions.add(detailEnd.plusDays(1));
        }
        
      }
    }
    Collections.sort(distortions);
    
    int index = 0;
    final Iterator<LocalDate> iterator = distortions.iterator();
    while(iterator.hasNext()) {

      final var detailStartDate = iterator.next();
      var detailEndDate = (iterator.hasNext() ? distortions.get(index+1).minusDays(1) : endDate);
      if(detailStartDate.isAfter(detailEndDate)) {
        detailEndDate = detailStartDate;
      }
      
      final List<CoverDetail> details = new ArrayList<>();
      for(final var coverDetail : cover.getDetails()) {
        
        // has start logn before or on the distortion date AND has not ended
        if(CoverDates.in(coverDetail, detailStartDate, detailEndDate)) {
          details.add(coverDetail);
        }
      }
      
      result.add(ImmutableProjectionDetail.builder().startDate(detailStartDate).endDate(detailEndDate).coverDetails(details).build());
      index++;
    }
    
    return result;
  }
}
