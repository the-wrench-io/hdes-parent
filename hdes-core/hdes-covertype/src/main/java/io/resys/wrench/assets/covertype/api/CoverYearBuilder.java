package io.resys.wrench.assets.covertype.api;

import java.util.Optional;

import org.immutables.value.Value;

public interface CoverYearBuilder {
  CoverYearBuilder daysInMonth(int daysInMonth);
  CoverYearBuilder daysInYear(int daysInYear);
  CoverYear build();

  @Value.Immutable
  interface CoverYear {
    MonthType getMonth();
    YearType getYear();
    Optional<Integer> getDaysInMonth();
    Optional<Integer> getDaysInYear();
  }
  
  enum YearType {
    NATURAL, // includes leap year 365/366
    CUSTOM // year has fixed number of days
  }
  
  enum MonthType {
    NATURAL, // however many days there are in month
    CUSTOM // months have fixed number of days in them
  }
}