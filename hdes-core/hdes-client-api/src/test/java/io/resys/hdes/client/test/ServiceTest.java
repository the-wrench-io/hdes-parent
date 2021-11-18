package io.resys.hdes.client.test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.immutables.value.Value;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.resys.hdes.client.api.ast.AstCommand.AstCommandValue;
import io.resys.hdes.client.api.ast.ImmutableAstCommand;
import io.resys.hdes.client.api.programs.ServiceData;
import io.resys.hdes.client.api.programs.ServiceProgram.ServiceResult;
import io.resys.hdes.client.spi.util.FileUtils;

public class ServiceTest {

  @Test
  public void type0() throws IOException {
    final String src = TestUtils.objectMapper.writeValueAsString(Arrays.asList(ImmutableAstCommand.builder()
        .type(AstCommandValue.SET_BODY)
        .value(FileUtils.toString(getClass(), "service/Type1Service.txt"))
        .build()));
    final var envir = TestUtils.client.envir().addCommand().id("test1").service(src).build().build();

    
  
    // map conversion
    ServiceResult result = TestUtils.client.executor(envir)
      .inputMap(Map.of("a", 5, "b", 10))
      .service("Type1Service")
      .andGetBody();
    
    Assertions.assertEquals("{\"sum\":15}", TestUtils.objectMapper.writeValueAsString(result.getValue()));
    
    // data object conversion
    result = TestUtils.client.executor(envir)
        .inputEntity(ImmutableTestServiceInput.builder().a(5).b(10).build())
        .service("Type1Service")
        .andGetBody();
    // object to object conversion
    Assertions.assertEquals("{\"sum\":15}", TestUtils.objectMapper.writeValueAsString(result.getValue()));
  }
  
  
  @ServiceData
  @Value.Immutable
  public interface TestServiceInput {
    Integer getA();
    Integer getB();
  }
}
