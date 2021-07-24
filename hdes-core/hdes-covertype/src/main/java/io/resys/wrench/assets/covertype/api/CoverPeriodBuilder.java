package io.resys.wrench.assets.covertype.api;

import java.time.LocalDate;

import org.immutables.value.Value;

public interface CoverPeriodBuilder {
  CoverPeriodBuilder startDate(LocalDate startDate);
  CoverPeriodBuilder endDate(LocalDate endDate);
  CoverPeriodBuilder length(int lengthInMonths); // 1-12
  CoverPeriod build();
  
  @Value.Immutable
  interface CoverPeriod {
    LocalDate getStartDate();
    LocalDate getEndDate();
    int getLength();
  }
}
