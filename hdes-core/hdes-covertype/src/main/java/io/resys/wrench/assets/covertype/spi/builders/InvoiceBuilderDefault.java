package io.resys.wrench.assets.covertype.spi.builders;

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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.resys.wrench.assets.covertype.api.CoverRepository.Invoice;
import io.resys.wrench.assets.covertype.api.CoverRepository.InvoiceBuilder;
import io.resys.wrench.assets.covertype.api.CoverRepository.InvoiceCalculation;
import io.resys.wrench.assets.covertype.api.CoverRepository.InvoiceCalculationBuilder;
import io.resys.wrench.assets.covertype.api.CoverRepository.InvoiceDetailBuilder;
import io.resys.wrench.assets.covertype.api.CoverRepository.ProjectionPeriodMonths;
import io.resys.wrench.assets.covertype.api.ImmutableInvoice;
import io.resys.wrench.assets.covertype.api.ImmutableInvoiceCalculation;
import io.resys.wrench.assets.covertype.api.ImmutableInvoiceDetail;
import io.resys.wrench.assets.covertype.spi.util.CoverAssert;

public class InvoiceBuilderDefault implements InvoiceBuilder {

  private String id;
  private LocalDate startDate;
  private LocalDate endDate;
  private final Map<String, ImmutableInvoiceDetail.Builder> detailsBuilders = new HashMap<>();
  
  @Override
  public InvoiceBuilder id(String id) {
    this.id = id;
    return this;
  }
  @Override
  public InvoiceBuilder startDate(LocalDate startDate) {
    this.startDate = startDate;
    return this;
  }
  @Override
  public InvoiceBuilder endDate(LocalDate endDate) {
    this.endDate = endDate;
    return this;
  }
  @Override
  public InvoiceDetailBuilder addDetail() {
    return new InvoiceDetailBuilder() {
      private String coverType;
      private ProjectionPeriodMonths projectionPeriodMonths;
      private final List<InvoiceCalculation> calculationDetails = new ArrayList<>();
      @Override
      public InvoiceDetailBuilder coverType(String coverType) {
        this.coverType = coverType;
        return this;
      }
      @Override
      public InvoiceDetailBuilder projectionPeriodMonths(ProjectionPeriodMonths projectionPeriodMonths) {
        this.projectionPeriodMonths = projectionPeriodMonths;
        return this;
      }
      @Override
      public InvoiceCalculationBuilder addCalculation() {
        return new InvoiceCalculationBuilder() {
          private String coverType;
          private LocalDate startDate;
          private LocalDate endDate;
          private final Map<String, Serializable> params = new HashMap<>();
          @Override
          public InvoiceCalculationBuilder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
          }
          @Override
          public InvoiceCalculationBuilder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
          }
          @Override
          public InvoiceCalculationBuilder coverType(String coverType) {
            this.coverType = coverType;
            return this;
          }
          @Override
          public InvoiceCalculationBuilder addParam(String name, Serializable value) {
            if(value != null) {
              params.put(name, value);
            }
            return this;
          }

          @Override
          public InvoiceCalculationBuilder addParams(Map<String, Serializable> values) {
            this.params.putAll(values);
            return this;
          }
          @Override
          public InvoiceCalculation build() {
            CoverAssert.notNull(id, () -> "id needs to be set!");
            CoverAssert.notNull(endDate, () -> "endDate needs to be set!");
            CoverAssert.notNull(startDate, () -> "startDate needs to be set!");
            CoverAssert.notNull(coverType, () -> "coverType needs to be set!");
            
            InvoiceCalculation invoiceCalculation = ImmutableInvoiceCalculation.builder()
              .startDate(startDate)
              .endDate(endDate)
              .coverType(coverType)
              .params(params)
              .build();
              
            calculationDetails.add(invoiceCalculation);
            
            return invoiceCalculation;
          }
        };
      }
      @Override
      public void build() {
        CoverAssert.notNull(coverType, () -> "coverType needs to be set!");
        CoverAssert.notNull(projectionPeriodMonths, () -> "projectionPeriodMonths needs to be set!");
        
        final ImmutableInvoiceDetail.Builder detail; 
        if(detailsBuilders.containsKey(coverType)) {
          detail = detailsBuilders.get(coverType);
        } else {
          detail = ImmutableInvoiceDetail.builder().coverType(coverType);
          detailsBuilders.put(coverType, detail);
        }
        
        
        detail.projectionPeriodMonths(projectionPeriodMonths).addAllInvoiceCalculations(calculationDetails);
      }
    };
  }
  @Override
  public Invoice build() {
    CoverAssert.notNull(id, () -> "id needs to be set!");
    CoverAssert.notNull(endDate, () -> "endDate needs to be set!");
    CoverAssert.notNull(startDate, () -> "startDate needs to be set!");

    return ImmutableInvoice.builder()
        .id(id)
        .startDate(startDate)
        .endDate(endDate)
        .addAllInvoiceDetails(detailsBuilders.values().stream().map(b -> b.build()).collect(Collectors.toList()))
        .build();
  }
}
