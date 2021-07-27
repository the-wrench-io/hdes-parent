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

import io.resys.wrench.assets.covertype.api.CoverRepository.CoverDetail;
import io.resys.wrench.assets.covertype.api.CoverRepository.CoverDetailBuilder;
import io.resys.wrench.assets.covertype.api.ImmutableCoverDetail;
import io.resys.wrench.assets.covertype.spi.util.CoverAssert;

public class CoverDetailBuilderDefault implements CoverDetailBuilder {

  private String id;
  private LocalDate startDate;
  private LocalDate endDate;
  private String type;
  private Serializable origin;
  
  @Override
  public CoverDetailBuilder id(String id) {
    this.id = id;
    return this;
  }
  @Override
  public CoverDetailBuilder startDate(LocalDate startDate) {
    this.startDate = startDate;
    return this;
  }
  @Override
  public CoverDetailBuilder endDate(LocalDate endDate) {
    this.endDate = endDate;
    return this;
  }
  @Override
  public CoverDetailBuilder origin(Serializable origin) {
    this.origin = origin;
    return this;
  }
  @Override
  public CoverDetailBuilder type(String type) {
    this.type = type;
    return this;
  }
  @Override
  public CoverDetail build() {
    CoverAssert.notNull(id, () -> "id needs to be set!");
    CoverAssert.notNull(type, () -> "type needs to be set!");
    CoverAssert.notNull(origin, () -> "origin needs to be set!");
    CoverAssert.notNull(startDate, () -> "startDate needs to be set!");
    CoverAssert.notNull(endDate, () -> "endDate needs to be set!");
    
    return ImmutableCoverDetail.builder()
        .id(id)
        .type(type)
        .origin(origin)
        .startDate(startDate)
        .endDate(endDate)
        .build();
  
  }
}
