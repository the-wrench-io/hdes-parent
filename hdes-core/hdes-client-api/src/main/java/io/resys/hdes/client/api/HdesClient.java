package io.resys.hdes.client.api;

/*-
 * #%L
 * hdes-client-api
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

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.resys.hdes.client.api.ast.AstCommand;
import io.resys.hdes.client.api.ast.AstDecision;
import io.resys.hdes.client.api.ast.AstFlow;
import io.resys.hdes.client.api.ast.AstService;
import io.resys.hdes.client.api.ast.TypeDef;
import io.resys.hdes.client.api.programs.DecisionProgram;
import io.resys.hdes.client.api.programs.DecisionProgram.DecisionResult;
import io.resys.hdes.client.api.programs.FlowProgram;
import io.resys.hdes.client.api.programs.FlowResult;
import io.resys.hdes.client.api.programs.ServiceResult;

public interface HdesClient {
  AstBuilder ast();
  HdesAstTypes astTypes();
  ProgramBuilder program();
  HdesStore store();
  ExecutorBuilder executor();
  CSVBuilder csv();
  
  
  interface CSVBuilder {
    String ast(AstDecision ast);
  }
  
//  ExportBuilder export();
//  
//  interface ExportBuilder {
//    String build();
//  }

  interface ProgramBuilder {
    FlowProgram ast(AstFlow ast);
    DecisionProgram ast(AstDecision ast);
    AstService ast(AstService ast);
  }
  
  interface FlowExecutor {
    Object andGetTask(String task);
    FlowResult andGetBody();
  }
  interface DecisionExecutor {
    Map<String, Serializable> andGet();
    List<Map<String, Serializable>> andFind();
    DecisionResult andGetBody();
  }

  interface ServiceExecutor {
    ServiceResult andGetBody();
  }
  
  interface ExecutorInput extends Function<TypeDef, Object> {};
  
  interface ExecutorBuilder {
    ExecutorBuilder inputMap(Map<String, Object> input);
    ExecutorBuilder inputEntity(Object inputObject);
    ExecutorBuilder inputList(List<Object> inputObject);
    ExecutorBuilder inputJson(JsonNode json);
    ExecutorBuilder input(ExecutorInput input);
    
    // From model or by Id
    FlowExecutor flow(String modelId);
    FlowExecutor flow(FlowProgram model);

    // From model or by Id
    DecisionExecutor decision(String modelId);
    DecisionExecutor decision(DecisionProgram model);
    
    // From model or by Id
    ServiceExecutor service(String modelId);
    ServiceExecutor service(AstService model);
  }
  
  interface AstBuilder {
    AstBuilder commands(ArrayNode src, Integer version);
    AstBuilder commands(ArrayNode src);
    AstBuilder commands(String src);
    AstBuilder commands(List<AstCommand> src, Integer version);
    AstBuilder commands(List<AstCommand> src);
    AstBuilder syntax(String src);


    AstFlow flow();
    AstDecision decision();
    AstService service();
  }
}