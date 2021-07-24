package io.resys.wrench.assets.covertype.spi.visitors;

import java.time.LocalDate;

import io.resys.wrench.assets.covertype.api.CoverBuilder.Cover;
import io.resys.wrench.assets.covertype.api.CoverPeriodBuilder.CoverPeriod;
import io.resys.wrench.assets.covertype.api.CoverTypeRepository.Calculation;
import io.resys.wrench.assets.covertype.api.CoverYearBuilder.CoverYear;
import io.resys.wrench.assets.covertype.api.ImmutableCalculation;

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
  
  public Calculation visit() {
    return ImmutableCalculation.builder()
        .cover(cover)
        .coverPeriod(coverPeriod)
        .coverYear(coverYear)
        .build();
  }
  
  public void visitCoverPeriod() {
    if(coverPeriod.getStartDate().isAfter(cover.getEndDate())) {
      return;
    }
    
    LocalDate start = coverPeriod.getStartDate();
    
    //1 how many years
  }
  
  public void visitYear(LocalDate startDate) {
    
  }
}
