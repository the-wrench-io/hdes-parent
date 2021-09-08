package io.resys.wrench.assets.covertype.api;

/*-
 * #%L
 * hdes-covertype
 * %%
 * Copyright (C) 2020 - 2021 Copyright 2020 ReSys OÜ
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public interface CoverRepository {
  
  ProjectionBuilder projection();
  InvoicesBuilder invoices();
  
  interface InvoicesBuilder {
    InvoiceBuilder addInvoice();
    List<Invoice> build();
  }
  
  interface InvoiceBuilder {
    InvoiceBuilder id(String id);
    InvoiceBuilder startDate(LocalDate startDate);
    InvoiceBuilder endDate(LocalDate endDate);
    InvoiceDetailBuilder addDetail();
    Invoice build();
  }
  interface InvoiceDetailBuilder {
    InvoiceDetailBuilder coverType(String coverType); 
    InvoiceDetailBuilder projectionPeriodMonths(ProjectionPeriodMonths projectionPeriodMonths);
    InvoiceCalculationBuilder addCalculation();
    void build();
  }
  interface InvoiceCalculationBuilder {
    InvoiceCalculationBuilder coverType(String coverType);
    InvoiceCalculationBuilder startDate(LocalDate startDate);
    InvoiceCalculationBuilder endDate(LocalDate endDate);
    InvoiceCalculationBuilder addParam(String name, Serializable value);
    InvoiceCalculationBuilder addParams(Map<String, Serializable> values);
    InvoiceCalculation build();
  }
  
  
  interface CoverBuilder {
    CoverBuilder id(String id);
    CoverBuilder startDate(LocalDate startDate);
    CoverBuilder endDate(LocalDate endDate);
    CoverBuilder type(String type);
    CoverBuilder origin(Serializable origin);
    CoverDetailBuilder addDetail();
    CoverPeriodBuilder addPeriod();
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
    CoverPeriodBuilder dueDate(LocalDate dueDate);
    CoverPeriodBuilder lengthInMonths(Integer lengthInMonths); // 1-12
    CoverPeriod build();
  }
  interface ProjectionBuilder {
    ProjectionBuilder lastDueDate(LocalDate marker);
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
    List<CoverPeriod> getPeriods();
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
    LocalDate getDueDate();
    int getMonths();
  }
  @JsonSerialize(as = ImmutableProjection.class)
  @JsonDeserialize(as = ImmutableProjection.class)  
  @Value.Immutable
  interface Projection {
    Cover getCover();
    List<ProjectionPeriod> getProjectionPeriods();
  }
  @JsonSerialize(as = ImmutableProjectionPeriod.class)
  @JsonDeserialize(as = ImmutableProjectionPeriod.class)
  @Value.Immutable
  interface ProjectionPeriod {
    LocalDate getStartDate();
    LocalDate getEndDate();
    ProjectionPeriodMonths getProjectionMonths();
    List<ProjectionDetail> getProjectionDetails();
  }
  @JsonSerialize(as = ImmutableProjectionPeriodMonths.class)
  @JsonDeserialize(as = ImmutableProjectionPeriodMonths.class)
  @Value.Immutable
  interface ProjectionPeriodMonths {
    int getDays();
    int getMonths();
    long getTotalDays();
    Optional<BigDecimal> getStartPerc(); // how many days from start as percentage
    Optional<BigDecimal> getEndPerc(); // how many days from end as percentage
  }
 
  @JsonSerialize(as = ImmutableProjectionDetail.class)
  @JsonDeserialize(as = ImmutableProjectionDetail.class)
  @Value.Immutable
  interface ProjectionDetail {
    LocalDate getStartDate();
    LocalDate getEndDate();
    List<CoverDetail> getCoverDetails();
  }
  
  
  @JsonSerialize(as = ImmutableInvoice.class)
  @JsonDeserialize(as = ImmutableInvoice.class)
  @Value.Immutable
  interface Invoice {
    String getId();
    LocalDate getStartDate();
    LocalDate getEndDate();
    List<InvoiceDetail> getInvoiceDetails();
  }
  @JsonSerialize(as = ImmutableInvoiceDetail.class)
  @JsonDeserialize(as = ImmutableInvoiceDetail.class)
  @Value.Immutable
  interface InvoiceDetail {
    String getCoverType();
    ProjectionPeriodMonths getProjectionPeriodMonths();
    List<InvoiceCalculation> getInvoiceCalculations();
  }
  @JsonSerialize(as = ImmutableInvoiceCalculation.class)
  @JsonDeserialize(as = ImmutableInvoiceCalculation.class)
  @Value.Immutable
  interface InvoiceCalculation {
    String getCoverType();
    LocalDate getStartDate();
    LocalDate getEndDate();
    Map<String, Serializable> getParams();
  }
  
}
