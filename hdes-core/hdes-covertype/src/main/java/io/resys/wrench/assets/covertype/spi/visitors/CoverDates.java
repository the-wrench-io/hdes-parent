package io.resys.wrench.assets.covertype.spi.visitors;

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
