package io.resys.hdes.runtime.tests;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.resys.hdes.compiler.api.HdesCompiler;
import io.resys.hdes.compiler.api.HdesCompiler.Resource;
import io.resys.hdes.compiler.spi.java.JavaHdesCompiler;
import io.resys.hdes.runtime.spi.ImmutableHdesRuntime;

public class HdesRuntimeTest {
  private final HdesCompiler compiler = JavaHdesCompiler.config().build();
  
  @Test 
  public void dtHitPolicyAll() {
    String src = "define decision-table: ExpressionDT description: 'uber dt'\n" + 
        "headers: {\n" + 
        "  value0 INTEGER required IN,\n" + 
        "  value1 INTEGER required IN,\n" + 
        "  value INTEGER required OUT\n" + 
        "} ALL: {\n" + 
        "  { > 10, <= 20,          4570 },\n" + 
        "  { > 10, <= 20 and > 10, 4570 },\n" + 
        "  { = 6 , != 20 and > 10, 4570 }\n" + 
        "}";
    
    List<Resource> resources = compiler.parser().add("ExpressionDT", src).build();
    
    ImmutableHdesRuntime.builder().from(resources).build();
    
  }
}
