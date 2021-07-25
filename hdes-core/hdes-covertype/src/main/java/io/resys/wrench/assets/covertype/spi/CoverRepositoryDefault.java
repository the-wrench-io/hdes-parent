package io.resys.wrench.assets.covertype.spi;

import io.resys.wrench.assets.covertype.api.CoverRepository;
import io.resys.wrench.assets.covertype.spi.builders.CoverBuilderDefault;
import io.resys.wrench.assets.covertype.spi.builders.CoverPeriodBuilderDefault;
import io.resys.wrench.assets.covertype.spi.builders.CoverYearBuilderDefault;
import io.resys.wrench.assets.covertype.spi.util.CoverAssert;
import io.resys.wrench.assets.covertype.spi.visitors.CoverVisitor;

public class CoverRepositoryDefault implements CoverRepository {

  @Override
  public ProjectionBuilder projection() {
    return new ProjectionBuilder() {
      private Cover cover;
      private CoverYear coverYear;
      private CoverPeriod coverPeriod;
      @Override
      public CoverYearBuilder year() {
        return new CoverYearBuilderDefault() {
          @Override
          public CoverYear build() {
            coverYear = super.build();
            return coverYear;
          }
        };
      }
      @Override
      public CoverPeriodBuilder period() {
        return new CoverPeriodBuilderDefault() {
          @Override
          public CoverPeriod build() {
            coverPeriod = super.build();
            return coverPeriod;
          }
        };
      }
      @Override
      public CoverBuilder cover() {
        return new CoverBuilderDefault() {
          @Override
          public Cover build() {
            cover = super.build();
            return cover;
          }
        };
      }
      
      @Override
      public Projection build() {
        CoverAssert.notNull(cover, () -> "cover needs to be added!");
        CoverAssert.notNull(coverPeriod, () -> "coverPeriod needs to be added!");
        CoverAssert.notNull(coverYear, () -> "coverYear needs to be added!");
        
        return new CoverVisitor(cover, coverYear, coverPeriod).visit();
      }
    };
  }

  public static Builder builder() {
    return new Builder();
  }
  
  public static class Builder {
    public CoverRepository build() {
      return new CoverRepositoryDefault();
    }
  }
}
