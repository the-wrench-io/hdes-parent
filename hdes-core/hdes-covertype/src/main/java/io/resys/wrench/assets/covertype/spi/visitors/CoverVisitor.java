package io.resys.wrench.assets.covertype.spi.visitors;

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
import io.resys.wrench.assets.covertype.api.CoverRepository.CoverYear;
import io.resys.wrench.assets.covertype.api.CoverRepository.Projection;
import io.resys.wrench.assets.covertype.api.CoverRepository.ProjectionDetail;
import io.resys.wrench.assets.covertype.api.CoverRepository.ProjectionPeriod;
import io.resys.wrench.assets.covertype.api.CoverRepository.ProjectionPeriodMonths;
import io.resys.wrench.assets.covertype.api.ImmutableProjection;
import io.resys.wrench.assets.covertype.api.ImmutableProjectionDetail;
import io.resys.wrench.assets.covertype.api.ImmutableProjectionPeriod;
import io.resys.wrench.assets.covertype.api.ImmutableProjectionPeriodMonths;


public class CoverVisitor {

  private final Cover cover;
  private final CoverYear coverYear;
  private final CoverPeriod coverPeriod;
  
  public CoverVisitor(Cover cover, CoverYear coverYear, CoverPeriod coverPeriod) {
    super();
    this.cover = cover;
    this.coverYear = coverYear;
    this.coverPeriod = coverPeriod;
  }
  
  public Projection visit() {
    return ImmutableProjection.builder()
        .cover(cover)
        .coverPeriod(coverPeriod)
        .coverYear(coverYear)
        .projectionPeriods(visitProjectionPeriods(coverPeriod))
        .build();
  }
  
  
  private List<ProjectionPeriod> visitProjectionPeriods(CoverPeriod coverPeriod) {
    if(coverPeriod.getStartDate().isAfter(cover.getEndDate())) {
      return Collections.emptyList();
    }
    
    final var endDate = visitProjectionEndDate(coverPeriod);
    final List<ProjectionPeriod> result = new ArrayList<>();
    
    var startDate = coverPeriod.getStartDate();
    while(!startDate.isAfter(endDate)) {
      ProjectionPeriod period = visitProjectionPeriod(startDate, endDate, coverPeriod.getMonths());
      result.add(period);
      startDate = period.getEndDate().plusDays(1);
    }
    
    return result;
  }
  
  private LocalDate visitProjectionEndDate(CoverPeriod coverPeriod) {
    return coverPeriod.getEndDate().isBefore(cover.getEndDate()) ? coverPeriod.getEndDate() : cover.getEndDate();
  }
  
  private ProjectionPeriod visitProjectionPeriod(LocalDate startDate, LocalDate limitEndDate, int months) {
    var endDate = startDate.plusMonths(months).minusDays(1);
    if(endDate.isAfter(limitEndDate)) {
      endDate = limitEndDate;
    }
    
    return ImmutableProjectionPeriod.builder()
        .startDate(startDate)
        .endDate(endDate)
        .projectionMonths(visitProjectionPeriodMonths(startDate, endDate))
        .projectionDetails(visitProjectionDetails(startDate, endDate))
        .build();
  }
  
  private ProjectionPeriodMonths visitProjectionPeriodMonths(LocalDate startDate, LocalDate endDate) {
    final var rolledEndDate = endDate.plusDays(1);
    final var period = Period.between(startDate, rolledEndDate);
    final var totalDays = Duration.between(startDate.atStartOfDay(), rolledEndDate.atStartOfDay()).toDays();
    

    Optional<BigDecimal> startPerc = Optional.empty();
    if(startDate.getDayOfMonth() != 1) {
      startPerc = Optional.of(visitPercOfMonth(endDate));
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
    final var diff = lengthOfMonth - dayOfMonth + 1;
    
    final var result = BigDecimal.valueOf(diff)
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
        if(in(coverDetail, detailStartDate, detailEndDate)) {
          details.add(coverDetail);
        }
      }
      
      result.add(ImmutableProjectionDetail.builder().startDate(detailStartDate).endDate(detailEndDate).coverDetails(details).build());
      index++;
    }
    
    return result;
  }
  

  
  private boolean in(CoverDetail detail, LocalDate startDate, LocalDate endDate) {
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
}
