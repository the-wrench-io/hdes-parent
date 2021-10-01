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

import com.fasterxml.jackson.databind.node.ArrayNode;

import io.resys.hdes.client.api.ast.AstType.AstCommandType;
import io.resys.hdes.client.api.ast.DecisionAstType;
import io.resys.hdes.client.api.ast.FlowAstType;
import io.resys.hdes.client.api.ast.ServiceAstType;
import io.resys.hdes.client.api.execution.DecisionTableResult;
import io.resys.hdes.client.api.execution.Flow;
import io.resys.hdes.client.api.model.DecisionTable;
import io.resys.hdes.client.api.model.FlowModel;

public interface HdesClient {
  AstBuilder ast();
  ModelBuilder model();
  ExecutorBuilder executor();
  HdesStore store();
  
  interface FlowExecutor {
    Object andGetTask(String task);
    Flow andGetBody();
  }
  interface DecisionExecutor {
    Map<String, Serializable> andGet();
    List<Map<String, Serializable>> andFind();
    DecisionTableResult andGetBody();
  }

  interface ServiceExecutor {
    Serializable andGetBody();
  }
  
  interface ExecutorBuilder {
    ExecutorBuilder withMap(Map<String, Object> input);
    ExecutorBuilder withEntity(Object inputObject);
    
    // From model or by Id
    FlowExecutor flow(String modelId);
    FlowExecutor flow(FlowModel model);

    // From model or by Id
    DecisionExecutor decision(String modelId);
    DecisionExecutor decision(DecisionTable model);
    
    // From model or by Id
    ServiceExecutor service(String modelId);
    ServiceExecutor service(ServiceAstType model);
  }
  
  interface ModelBuilder {
    FlowModel ast(FlowAstType ast);
    DecisionTable ast(DecisionAstType ast);
    ServiceAstType ast(ServiceAstType ast);
  }

  interface AstBuilder {
    AstBuilder commands(ArrayNode src, Integer version);
    AstBuilder commands(List<AstCommandType> src, Integer version);
    AstBuilder syntax(String src);

    FlowAstType flow();
    DecisionAstType decision();
    ServiceAstType service();
  }
}
