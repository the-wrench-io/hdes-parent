package io.resys.hdes.compiler.test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.compiler.api.HdesCompiler;
import io.resys.hdes.compiler.api.HdesCompiler.Resource;
import io.resys.hdes.compiler.spi.GenericHdesCompiler;

public class TestUtil {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(TestUtil.class);
  public static final HdesCompiler compiler = GenericHdesCompiler.config().build();

  
  public static void log(List<Resource> resources, String file) {
    if(!LOGGER.isDebugEnabled()) {
      return;
    }
    
    StringBuilder result = new StringBuilder();
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      result.append(System.lineSeparator()).append("############").append(System.lineSeparator())
        .append("{").append(System.lineSeparator())
        .append("src: ").append(objectMapper.writeValueAsString(file)).append(System.lineSeparator())
        .append("ast: {").append(System.lineSeparator());
      
      for (Resource r : resources) {
        String ast = objectMapper.writeValueAsString(r.getAst());
        result.append("    ").append(r.getName()).append(": ").append(ast).append(System.lineSeparator());
      }
      result
      .append("  }").append(System.lineSeparator())
      .append("}");
      
      LOGGER.debug(result.toString());
    } catch(IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
  
  public static String file(String name) {
    try {
      return IOUtils.toString(DtHdesCompilerTest.class.getClassLoader().getResourceAsStream(name), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
