package io.resys.hdes.runtime.tests;

import java.io.Serializable;
import java.util.HashMap;

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
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.compiler.api.DecisionTableMeta;
import io.resys.hdes.compiler.api.HdesCompiler;
import io.resys.hdes.compiler.api.HdesCompiler.Resource;
import io.resys.hdes.compiler.api.HdesExecutable;
import io.resys.hdes.compiler.api.HdesExecutable.DecisionTable;
import io.resys.hdes.compiler.api.HdesExecutable.OutputValue;
import io.resys.hdes.compiler.spi.java.JavaHdesCompiler;
import io.resys.hdes.runtime.api.HdesRuntime.RuntimeEnvir;
import io.resys.hdes.runtime.api.HdesRuntime.RuntimeTask;
import io.resys.hdes.runtime.spi.ImmutableHdesRuntime;

public class HdesRuntimeTest {
  private static final HdesCompiler compiler = JavaHdesCompiler.config().build();
  private static final ObjectMapper objectMapper = new ObjectMapper();
  

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
    
    Map<String, Serializable> data = new HashMap<>();
    data.put("value0", 11);
    data.put("value1", 2);
    
    HdesExecutable.Output<DecisionTableMeta, ? extends OutputValue> output = runDT("ExpressionDT", src, data);
    Assertions.assertEquals(output.getMeta().getValues().size(), 1);
    Assertions.assertEquals(output.getMeta().getValues().get(0).getIndex(), 0);
  }
  
  @Test 
  public void dtHitPolicyFirst() {
    String src = "define decision-table: SimpleHitPolicyFirstDt description: 'uber dt'\n" + 
        "headers: {\n" + 
        "  name STRING required IN,\n" + 
        "  lastName STRING required IN,\n" + 
        "  value INTEGER required OUT\n" + 
        "} FIRST: {\n" + 
        "  {         'sam',         ?,   20 },\n" + 
        "  {         'bob',   'woman', 4570 },\n" + 
        "  {not 'bob' \n" + 
        "   or 'same' \n" + 
        "   or 'professor',   'woman' \n" + 
        "                    or 'man', 4570 }\n" + 
        "}";
    
    Map<String, Serializable> data = new HashMap<>();
    data.put("name", "sam");
    data.put("lastName", "blah");
    
    HdesExecutable.Output<DecisionTableMeta, ? extends OutputValue> output = runDT("SimpleHitPolicyFirstDt", src, data);
    Assertions.assertEquals(output.getMeta().getValues().size(), 1);
    Assertions.assertEquals(output.getMeta().getValues().get(0).getIndex(), 0);
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private static HdesExecutable.Output<DecisionTableMeta, ? extends OutputValue> runDT(
      String name, 
      String src, 
      Map<String, Serializable> data) {
    try {
      List<Resource> resources = compiler.parser().add(name, src).build();
      RuntimeEnvir runtime = ImmutableHdesRuntime.builder().from(resources).build();
      RuntimeTask task = runtime.get(name);
      HdesExecutable.Input input = objectMapper.convertValue(data, task.getInput());
      DecisionTable dt = (DecisionTable) task.getValue();
      HdesExecutable.Output<DecisionTableMeta, ? extends OutputValue> output = dt.apply(input);
      
      return output;
    } catch(ClassNotFoundException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}