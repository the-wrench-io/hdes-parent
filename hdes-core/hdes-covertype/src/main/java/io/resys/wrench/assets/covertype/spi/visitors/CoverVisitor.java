package io.resys.wrench.assets.covertype.spi.visitors;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import io.resys.wrench.assets.covertype.api.CoverRepository.Cover;
import io.resys.wrench.assets.covertype.api.CoverRepository.CoverPeriod;
import io.resys.wrench.assets.covertype.api.CoverRepository.CoverYear;
import io.resys.wrench.assets.covertype.api.CoverRepository.Projection;
import io.resys.wrench.assets.covertype.api.CoverRepository.ProjectionDetail;
import io.resys.wrench.assets.covertype.api.CoverRepository.ProjectionParam;
import io.resys.wrench.assets.covertype.api.CoverRepository.ProjectionYear;
import io.resys.wrench.assets.covertype.api.ImmutableProjection;
import io.resys.wrench.assets.covertype.api.ImmutableProjectionDetail;
import io.resys.wrench.assets.covertype.api.ImmutableProjectionParam;
import io.resys.wrench.assets.covertype.api.ImmutableProjectionYear;

public class CoverVisitor {

  private final Cover cover;
  private final CoverYear coverYear;
  private final CoverPeriod coverPeriod;
  private final List<ProjectionYear> years = new ArrayList<>();
  
  public CoverVisitor(Cover cover, CoverYear coverYear, CoverPeriod coverPeriod) {
    super();
    this.cover = cover;
    this.coverYear = coverYear;
    this.coverPeriod = coverPeriod;
  }
  
  public Projection visit() {
    visitCoverPeriod();
    
    return ImmutableProjection.builder()
        .cover(cover)
        .coverPeriod(coverPeriod)
        .coverYear(coverYear)
        .projectedYears(years)
        .build();
  }
  
  public void visitCoverPeriod() {
    if(coverPeriod.getStartDate().isAfter(cover.getEndDate())) {
      return;
    }
    final LocalDate end = coverPeriod.getEndDate().isBefore(cover.getEndDate()) ? coverPeriod.getEndDate() : cover.getEndDate();
    LocalDate start = coverPeriod.getStartDate();
    while(start.isBefore(end)) {
      start = visitProjectionYear(start);
    }
  }
  
  public LocalDate visitProjectionYear(LocalDate startDate) {
    LocalDate endDate = LocalDate.of(startDate.getYear(), 12, 31);
    if(cover.getEndDate().isBefore(endDate)) {
      endDate = cover.getEndDate();
    }
    years.add(ImmutableProjectionYear.builder()
        .startDate(startDate)
        .endDate(endDate)
        .months(visitProjectionMonth(startDate))
        .build());

    return LocalDate.of(startDate.getYear() + 1, 1, 1);
  }
  
  public List<ProjectionDetail> visitProjectionMonth(LocalDate startDate) {
    final List<ProjectionDetail> result = new ArrayList<>();
    
    LocalDate runningDate = startDate;
    int start = startDate.getMonthValue();
    int end = 0;
    
    do {
      end += coverPeriod.getMonths();
      if(end > 12) {
        end = 12;
      }
      
      LocalDate endDate = end == 12 ? startDate.withDayOfMonth(31).withMonth(12) : startDate.withDayOfMonth(1).withMonth(end+1).minusDays(1);
      
      final var detail = ImmutableProjectionDetail.builder()
          .startDate(runningDate)
          .endDate(endDate)
          .months(Math.max(endDate.getMonthValue()-runningDate.getMonthValue()+1, 1))
          .params(visitProjectionParams(runningDate, endDate))
          .build();
      result.add(detail);
      start = end;
      runningDate = endDate.plusDays(1);
    } while(end < 12);
    
    return result;
  }
  
  public List<ProjectionParam> visitProjectionParams(LocalDate startDate, LocalDate endDate) {
    final List<ProjectionParam> result = new ArrayList<>();
    
    for(var detail : cover.getDetails()) {
      if(detail.getStartDate().isAfter(endDate)) {
        continue;
      }
      
      final var param = ImmutableProjectionParam.builder()
          .startDate(detail.getStartDate().isBefore(startDate) || detail.getStartDate().isEqual(startDate) ? startDate : detail.getStartDate())
          .endDate(detail.getEndDate().isAfter(endDate) || detail.getEndDate().isEqual(endDate) ? endDate : detail.getEndDate())
          .coverDetail(detail)
          .build();
      result.add(param);
    }
    return result;
  } 
}
