package io.resys.hdes.compiler.test;

import static io.resys.hdes.compiler.test.TestUtil.compiler;
import static io.resys.hdes.compiler.test.TestUtil.log;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import io.resys.hdes.compiler.api.HdesCompiler.Resource;

public class FlJavaHdesCompilerTest {

  @Test
  public void simpleFlow() {
    final var file= file("SimpleFlow.hdes");
    List<Resource> code = compiler.parser()
      .add("SimpleFlow.hdes", file)
      //.add("SimpleHitPolicyFirstDt.hdes", file("SimpleHitPolicyFirstDt.hdes"))
      .build();
    
    
    log(code, file);
  }

  public static String file(String name) {
    try {
      return IOUtils.toString(FlJavaHdesCompilerTest.class.getClassLoader().getResourceAsStream(name), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
