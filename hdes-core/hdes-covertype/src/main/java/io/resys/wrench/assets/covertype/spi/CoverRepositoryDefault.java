package io.resys.wrench.assets.covertype.spi;

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
