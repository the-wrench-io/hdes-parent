package io.resys.wrench.assets.covertype.test;

import java.io.IOException;
import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import io.resys.wrench.assets.covertype.api.CoverRepository;
import io.resys.wrench.assets.covertype.api.CoverRepository.CoverBuilder;
import io.resys.wrench.assets.covertype.api.CoverRepository.Projection;
import io.resys.wrench.assets.covertype.api.CoverRepository.ProjectionBuilder;
import io.resys.wrench.assets.covertype.spi.CoverRepositoryDefault;


@RunWith(BlockJUnit4ClassRunner.class)
public class CalculationPeriodTest {
  private static CoverRepository repo = CoverRepositoryDefault.builder().build();
  
  @Test
  public void coverWith2AmountsAndNaturalCalendar() throws IOException {
    ProjectionBuilder builder = repo.projection();
  
    builder.year().build();
    
    builder.period()
      .length(6)
      .startDate(LocalDate.of(2000, 5, 1))
      .endDate(LocalDate.of(2002, 12, 31))
      .build();
    
    CoverBuilder coverBuilder = builder.cover()
      .id("1").origin(getClass())
      .startDate(LocalDate.of(2000, 1, 1))
      .endDate(LocalDate.of(2000, 12, 31))
      .type("insurance-cover");

    coverBuilder.addDetail()
      .id("").origin(getClass())
      .startDate(LocalDate.of(2000, 1, 1))
      .endDate(LocalDate.of(2000, 7, 4))
      .type("insurance-amount-1")
      .build();
  
    coverBuilder.addDetail()
      .id("").origin(getClass())
      .startDate(LocalDate.of(2000, 7, 5))
      .endDate(LocalDate.of(2000, 12, 31))
      .type("cover-amount-2")
      .build();
    coverBuilder.build();

    
    Projection calculation = builder.build();
    var actual = TestUtils.prettyPrint(calculation.getProjectionPeriods());
    var expected = TestUtils.toString(getClass(), "coverWith2AmountsAndNaturalCalendar.json");
    Assert.assertEquals(expected, actual); 
  }
  
  @Test
  public void coverWith2AmountsAndNaturalCalendarWith1DayDetails() throws IOException {
    ProjectionBuilder builder = repo.projection();
  
    builder.year().build();
    
    builder.period().length(6)
      .startDate(LocalDate.of(2000, 5, 1))
      .endDate(LocalDate.of(2002, 12, 31))
      .build();
    
    CoverBuilder coverBuilder = builder.cover()
      .id("1").origin(getClass())
      .startDate(LocalDate.of(2000, 1, 1))
      .endDate(LocalDate.of(2000, 12, 31))
      .type("insurance-cover");

    coverBuilder.addDetail()
      .id("").origin(getClass())
      .startDate(LocalDate.of(2000, 1, 1))
      .endDate(LocalDate.of(2000, 7, 4))
      .type("insurance-amount-1")
      .build();
  
    coverBuilder.addDetail()
      .id("").origin(getClass())
      .startDate(LocalDate.of(2000, 7, 4))
      .endDate(LocalDate.of(2000, 7, 4))
      .type("discount-1")
      .build();

    
    coverBuilder.addDetail()
      .id("").origin(getClass())
      .startDate(LocalDate.of(2000, 7, 5))
      .endDate(LocalDate.of(2000, 12, 31))
      .type("cover-amount-2")
      .build();
    coverBuilder.build();

    
    Projection calculation = builder.build();
    var actual = TestUtils.prettyPrint(calculation.getProjectionPeriods());
    var expected = TestUtils.toString(getClass(), "coverWith2AmountsAndNaturalCalendarWith1DayDetails.json");
    Assert.assertEquals(expected, actual); 
  }

  
  @Test
  public void coverWith2AmountsAndNaturalCalendarWith1DayDetailsAndFragments() throws IOException {
    ProjectionBuilder builder = repo.projection();
  
    builder.year().build();
    
    builder.period().length(6)
      .startDate(LocalDate.of(2000, 5, 20))
      .endDate(LocalDate.of(2002, 12, 9))
      .build();
    
    CoverBuilder coverBuilder = builder.cover()
      .id("1").origin(getClass())
      .startDate(LocalDate.of(2000, 1, 1))
      .endDate(LocalDate.of(2000, 12, 31))
      .type("insurance-cover");

    coverBuilder.addDetail()
      .id("").origin(getClass())
      .startDate(LocalDate.of(2000, 1, 1))
      .endDate(LocalDate.of(2000, 7, 4))
      .type("insurance-amount-1")
      .build();
  
    coverBuilder.addDetail()
      .id("").origin(getClass())
      .startDate(LocalDate.of(2000, 7, 4))
      .endDate(LocalDate.of(2000, 7, 4))
      .type("discount-1")
      .build();

    
    coverBuilder.addDetail()
      .id("").origin(getClass())
      .startDate(LocalDate.of(2000, 7, 5))
      .endDate(LocalDate.of(2000, 12, 31))
      .type("cover-amount-2")
      .build();
    coverBuilder.build();

    
    Projection calculation = builder.build();
    var actual = TestUtils.prettyPrint(calculation.getProjectionPeriods());
    var expected = TestUtils.toString(getClass(), "coverWith2AmountsAndNaturalCalendarWith1DayDetailsAndFragments.json");
    Assert.assertEquals(expected, actual); 
  }
}
