package io.resys.hdes.runtime.tests;

/*-
 * #%L
 * hdes-runtime
 * %%
 * Copyright (C) 2020 Copyright 2020 ReSys OÜ
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
