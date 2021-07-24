package io.resys.wrench.assets.covertype.api;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import org.immutables.value.Value;

public interface CoverBuilder {
  
  CoverBuilder id(String id);
  CoverBuilder startDate(LocalDate startDate);
  CoverBuilder endDate(LocalDate endDate);
  CoverBuilder type(String type);
  CoverBuilder origin(Serializable origin);
  CoverDetailBuilder addDetail();
  Cover build();
  

  
  interface CoverDetailBuilder {
    CoverDetailBuilder id(String id);
    CoverDetailBuilder startDate(LocalDate startDate);
    CoverDetailBuilder endDate(LocalDate endDate); 
    CoverDetailBuilder origin(Serializable origin);
    CoverDetailBuilder type(String type);
    CoverDetail build();
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
}