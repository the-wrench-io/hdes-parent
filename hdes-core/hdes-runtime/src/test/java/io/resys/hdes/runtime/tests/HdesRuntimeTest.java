package io.resys.hdes.runtime.tests;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.compiler.api.DecisionTable;
import io.resys.hdes.compiler.api.DecisionTable.DecisionTableInput;
import io.resys.hdes.compiler.api.DecisionTable.DecisionTableOutput;
import io.resys.hdes.compiler.api.HdesCompiler;
import io.resys.hdes.compiler.api.HdesCompiler.Resource;
import io.resys.hdes.compiler.spi.java.JavaHdesCompiler;
import io.resys.hdes.runtime.api.HdesRuntime.RuntimeEnvir;
import io.resys.hdes.runtime.api.HdesRuntime.RuntimeTask;
import io.resys.hdes.runtime.spi.ImmutableHdesRuntime;

public class HdesRuntimeTest {
  private final HdesCompiler compiler = JavaHdesCompiler.config().build();
  private final ObjectMapper objectMapper = new ObjectMapper();
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Test 
  public void dtHitPolicyAll() throws ClassNotFoundException, JsonMappingException, JsonProcessingException {
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
    RuntimeEnvir runtime = ImmutableHdesRuntime.builder().from(resources).build();
    
    RuntimeTask task = runtime.get("ExpressionDT");
    
    DecisionTableInput input = (DecisionTableInput) objectMapper.readValue("{\"value0\": 11, \"value1\": 2}", task.getInput());
    DecisionTable dt = (DecisionTable) task.getValue();
    DecisionTableOutput output = (DecisionTableOutput) dt.apply(input);
    
    System.out.println(output);
  }
}
