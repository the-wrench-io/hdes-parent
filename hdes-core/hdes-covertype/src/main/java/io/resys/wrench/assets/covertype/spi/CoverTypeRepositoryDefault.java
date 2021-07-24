package io.resys.wrench.assets.covertype.spi;

import io.resys.wrench.assets.covertype.api.CoverBuilder;
import io.resys.wrench.assets.covertype.api.CoverBuilder.Cover;
import io.resys.wrench.assets.covertype.api.CoverPeriodBuilder;
import io.resys.wrench.assets.covertype.api.CoverPeriodBuilder.CoverPeriod;
import io.resys.wrench.assets.covertype.api.CoverTypeRepository;
import io.resys.wrench.assets.covertype.api.CoverYearBuilder;
import io.resys.wrench.assets.covertype.api.CoverYearBuilder.CoverYear;
import io.resys.wrench.assets.covertype.spi.builders.CoverBuilderDefault;
import io.resys.wrench.assets.covertype.spi.builders.CoverPeriodBuilderDefault;
import io.resys.wrench.assets.covertype.spi.builders.CoverYearBuilderDefault;
import io.resys.wrench.assets.covertype.spi.util.CoverAssert;

public class CoverTypeRepositoryDefault implements CoverTypeRepository {

  @Override
  public CalculationBuilder calculation() {
    return new CalculationBuilder() {
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
      public Calculation build() {
        CoverAssert.notNull(cover, () -> "cover needs to be added!");
        CoverAssert.notNull(coverPeriod, () -> "coverPeriod needs to be added!");
        CoverAssert.notNull(coverYear, () -> "coverYear needs to be added!");
        
        
        return null;
      }
    };
  }

  public static Builder builder() {
    return new Builder();
  }
  
  public static class Builder {
    public CoverTypeRepository build() {
      return new CoverTypeRepositoryDefault();
    }
  }
}
