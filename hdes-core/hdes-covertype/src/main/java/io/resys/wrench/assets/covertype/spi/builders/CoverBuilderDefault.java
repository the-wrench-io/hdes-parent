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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.resys.wrench.assets.covertype.api.CoverRepository.Cover;
import io.resys.wrench.assets.covertype.api.CoverRepository.CoverBuilder;
import io.resys.wrench.assets.covertype.api.CoverRepository.CoverDetail;
import io.resys.wrench.assets.covertype.api.CoverRepository.CoverDetailBuilder;
import io.resys.wrench.assets.covertype.api.CoverRepository.CoverPeriod;
import io.resys.wrench.assets.covertype.api.CoverRepository.CoverPeriodBuilder;
import io.resys.wrench.assets.covertype.api.ImmutableCover;
import io.resys.wrench.assets.covertype.spi.util.CoverAssert;

public class CoverBuilderDefault implements CoverBuilder {

  private String id;
  private LocalDate startDate;
  private LocalDate endDate;
  private String type;
  private Serializable origin;
  private List<CoverDetail> details = new ArrayList<>();
  private List<CoverPeriod> periods = new ArrayList<>();

  @Override
  public CoverBuilder id(String id) {
    this.id = id;
    return this;
  }
  @Override
  public CoverBuilder startDate(LocalDate startDate) {
    this.startDate = startDate;
    return this;
  }
  @Override
  public CoverBuilder endDate(LocalDate endDate) {
    this.endDate = endDate;
    return this;
  }
  @Override
  public CoverBuilder type(String type) {
    this.type = type;
    return this;
  }
  @Override
  public CoverBuilder origin(Serializable origin) {
    this.origin = origin;
    return this;
  }
  @Override
  public CoverDetailBuilder addDetail() {
    return new CoverDetailBuilderDefault() {
      @Override
      public CoverDetail build() {
        CoverDetail detail = super.build();
        details.add(detail);
        return detail;
      }
    };
  }
  @Override
  public CoverPeriodBuilder addPeriod() {
    return new CoverPeriodBuilderDefault() {
      @Override
      public CoverPeriod build() {
        CoverPeriod coverPeriod = super.build();
        periods.add(coverPeriod);
        return coverPeriod;
      }
    };
  
  }
  
  @Override
  public Cover build() {
    CoverAssert.notNull(id, () -> "id needs to be set!");
    CoverAssert.notNull(type, () -> "type needs to be set!");
    CoverAssert.notNull(origin, () -> "origin needs to be set!");
    CoverAssert.notNull(startDate, () -> "startDate needs to be set!");
    CoverAssert.notNull(endDate, () -> "endDate needs to be set!");
    
    Collections.sort(periods, new Comparator<CoverPeriod>() {
      @Override
      public int compare(CoverPeriod o1, CoverPeriod o2) {
        return o1.getStartDate().compareTo(o2.getStartDate());
      }
    });
    
    return ImmutableCover.builder()
        .id(id)
        .type(type)
        .origin(origin)
        .startDate(startDate)
        .endDate(endDate)
        .details(details)
        .periods(periods)
        .build();
  }
}
