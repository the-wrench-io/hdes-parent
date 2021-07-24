package io.resys.wrench.assets.covertype.spi.builders;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import io.resys.wrench.assets.covertype.api.CoverBuilder;
import io.resys.wrench.assets.covertype.api.ImmutableCover;
import io.resys.wrench.assets.covertype.spi.util.CoverAssert;

public class CoverBuilderDefault implements CoverBuilder {

  private String id;
  private LocalDate startDate;
  private LocalDate endDate;
  private String type;
  private Serializable origin;
  private List<CoverDetail> details = new ArrayList<>();

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
  public Cover build() {
    CoverAssert.notNull(id, () -> "id needs to be set!");
    CoverAssert.notNull(type, () -> "type needs to be set!");
    CoverAssert.notNull(origin, () -> "origin needs to be set!");
    CoverAssert.notNull(startDate, () -> "startDate needs to be set!");
    CoverAssert.notNull(endDate, () -> "endDate needs to be set!");
    
    return ImmutableCover.builder()
        .id(id)
        .type(type)
        .origin(origin)
        .startDate(startDate)
        .endDate(endDate)
        .details(details)
        .build();
  }
}
