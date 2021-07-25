package io.resys.wrench.assets.covertype.api;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.immutables.value.Value;

public interface CoverRepository {

  ProjectionBuilder projection();
  
  interface CoverBuilder {
    CoverBuilder id(String id);
    CoverBuilder startDate(LocalDate startDate);
    CoverBuilder endDate(LocalDate endDate);
    CoverBuilder type(String type);
    CoverBuilder origin(Serializable origin);
    CoverDetailBuilder addDetail();
    Cover build();    
  }
  interface CoverDetailBuilder {
    CoverDetailBuilder id(String id);
    CoverDetailBuilder startDate(LocalDate startDate);
    CoverDetailBuilder endDate(LocalDate endDate); 
    CoverDetailBuilder origin(Serializable origin);
    CoverDetailBuilder type(String type);
    CoverDetail build();
  }
  interface CoverPeriodBuilder {
    CoverPeriodBuilder startDate(LocalDate startDate);
    CoverPeriodBuilder endDate(LocalDate endDate);
    CoverPeriodBuilder length(int lengthInMonths); // 1-12
    CoverPeriod build();
  }
  interface CoverYearBuilder {
    CoverYearBuilder daysInMonth(int daysInMonth);
    CoverYearBuilder daysInYear(int daysInYear);
    CoverYear build();
  }
  interface ProjectionBuilder {
    CoverYearBuilder year();
    CoverPeriodBuilder period();
    CoverBuilder cover();
    Projection build();
  }
  
  @Value.Immutable
  interface Cover {
    String getId();
    LocalDate getStartDate();
    LocalDate getEndDate();
    String getType();
    Serializable getOrigin();
    List<CoverDetail> getDetails();
  }
  @Value.Immutable
  interface CoverDetail {
    String getId();
    LocalDate getStartDate();
    LocalDate getEndDate();
    String getType();
    Serializable getOrigin();
  }
  
  @Value.Immutable
  interface CoverPeriod {
    LocalDate getStartDate();
    LocalDate getEndDate();
    int getMonths();
  }
  @Value.Immutable
  interface CoverYear {
    CoverMonthType getMonth();
    CoverYearType getYear();
    Optional<Integer> getDaysInMonth();
    Optional<Integer> getDaysInYear();
  }
  
  enum CoverYearType {
    NATURAL, // includes leap year 365/366
    CUSTOM // year has fixed number of days
  }
  
  enum CoverMonthType {
    NATURAL, // however many days there are in month
    CUSTOM // months have fixed number of days in them
  }
  
  @Value.Immutable
  interface Projection {
    Cover getCover();
    CoverYear getCoverYear();
    CoverPeriod getCoverPeriod();
    List<ProjectionYear> getProjectedYears();
  }
  @Value.Immutable
  interface ProjectionYear {
    LocalDate getStartDate();
    LocalDate getEndDate();
    List<ProjectionDetail> getMonths();
  }
  @Value.Immutable
  interface ProjectionDetail {
    LocalDate getStartDate();
    LocalDate getEndDate();
    int getMonths();
    List<ProjectionParam> getParams();
  }
  
  @Value.Immutable
  interface ProjectionParam {
    LocalDate getStartDate();
    LocalDate getEndDate();
    CoverDetail getCoverDetail();
  }
}
