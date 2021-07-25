package io.resys.wrench.assets.covertype.spi.builders;

import java.time.LocalDate;

import io.resys.wrench.assets.covertype.api.CoverRepository.CoverPeriod;
import io.resys.wrench.assets.covertype.api.CoverRepository.CoverPeriodBuilder;
import io.resys.wrench.assets.covertype.api.ImmutableCoverPeriod;
import io.resys.wrench.assets.covertype.spi.util.CoverAssert;

public class CoverPeriodBuilderDefault implements CoverPeriodBuilder {

  private LocalDate startDate;
  private LocalDate endDate;
  private int lengthInMonths;
  
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
  public CoverPeriodBuilder length(int lengthInMonths) {
    this.lengthInMonths = lengthInMonths;
    return this;
  }
  @Override
  public CoverPeriod build() {
    CoverAssert.notNull(startDate, () -> "startDate needs to be set!");
    CoverAssert.notNull(endDate, () -> "endDate needs to be set!");
    CoverAssert.isTrue(startDate.isBefore(endDate), () -> "startDate needs to be before endDate!");
    CoverAssert.isTrue(lengthInMonths > 0 && lengthInMonths < 13, () -> "lengthInMonths needs to be between 1-12 but was: " + lengthInMonths + "!");
    
    return ImmutableCoverPeriod.builder()
        .endDate(endDate)
        .startDate(startDate)
        .months(lengthInMonths)
        .build();
  }

}
