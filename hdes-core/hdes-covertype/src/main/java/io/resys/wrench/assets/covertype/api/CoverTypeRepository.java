package io.resys.wrench.assets.covertype.api;

import java.time.LocalDate;
import java.util.List;

import org.immutables.value.Value;

import io.resys.wrench.assets.covertype.api.CoverBuilder.Cover;
import io.resys.wrench.assets.covertype.api.CoverBuilder.CoverDetail;
import io.resys.wrench.assets.covertype.api.CoverPeriodBuilder.CoverPeriod;
import io.resys.wrench.assets.covertype.api.CoverYearBuilder.CoverYear;

public interface CoverTypeRepository {

  CalculationBuilder calculation();
  
  interface CalculationBuilder {
    CoverYearBuilder year();
    CoverPeriodBuilder period();
    CoverBuilder cover();
    Calculation build();
  }
  
  @Value.Immutable
  interface Calculation {
    Cover getCover();
    CoverYear getCoverYear();
    CoverPeriod getCoverPeriod();
    List<CalculatedYear> getYears();
  }
  
  @Value.Immutable
  interface CalculatedYear {
    LocalDate getStartDate();
    LocalDate getEndDate();
    List<CalculatedMonths> getMonths();
  }

  @Value.Immutable
  interface CalculatedMonths {
    LocalDate getStartDate();
    LocalDate getEndDate();
    int getLength();
    List<CoverDetail> getDetails();
  }
}
