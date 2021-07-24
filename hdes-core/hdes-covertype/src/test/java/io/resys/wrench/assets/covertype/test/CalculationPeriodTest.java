package io.resys.wrench.assets.covertype.test;

import java.io.IOException;
import java.time.LocalDate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.wrench.assets.covertype.api.CoverBuilder;
import io.resys.wrench.assets.covertype.api.CoverTypeRepository;
import io.resys.wrench.assets.covertype.api.CoverTypeRepository.Calculation;
import io.resys.wrench.assets.covertype.api.CoverTypeRepository.CalculationBuilder;
import io.resys.wrench.assets.covertype.spi.CoverTypeRepositoryDefault;

@RunWith(BlockJUnit4ClassRunner.class)
public class CalculationPeriodTest {
  private static CoverTypeRepository repo = CoverTypeRepositoryDefault.builder().build();
  private static ObjectMapper objectMapper = new ObjectMapper();
  
  @Test
  public void coverWith2AmountsAndNaturalCalendar() throws IOException {
    CalculationBuilder builder = repo.calculation();
  
    builder.year().build();
    
    builder.period()
      .length(1)
      .startDate(LocalDate.of(2009, 1, 1))
      .endDate(LocalDate.of(2012, 12, 31))
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
      .type("insurance-amount-1");
  
    coverBuilder.addDetail()
      .id("").origin(getClass())
      .startDate(LocalDate.of(2000, 7, 5))
      .endDate(LocalDate.of(2000, 12, 31))
      .type("cover-amount-2")
      .build();
    
    coverBuilder.build();

    
    Calculation calculation = builder.build();
    System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(calculation));
    
  }

}
