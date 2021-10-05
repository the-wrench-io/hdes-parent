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

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.resys.hdes.client.api.ast.AstDataType;
import io.resys.hdes.client.api.ast.AstType.AstCommandType;
import io.resys.hdes.client.api.ast.AstType.Direction;
import io.resys.hdes.client.api.ast.AstType.ValueType;
import io.resys.hdes.client.api.ast.DecisionAstType;
import io.resys.hdes.client.api.ast.FlowAstType;
import io.resys.hdes.client.api.ast.FlowAstType.NodeFlowVisitor;
import io.resys.hdes.client.api.ast.ServiceAstType;

public interface HdesAstTypes {

  DecisionAstBuilder decision();
  FlowAstBuilder flow();
  ServiceAstBuilder service();
  DataTypeAstBuilder dataType();

  
  interface DataTypeAstBuilder {
    DataTypeAstBuilder ref(String ref, AstDataType dataType);
    DataTypeAstBuilder required(boolean required);
    DataTypeAstBuilder name(String name);

    DataTypeAstBuilder valueType(ValueType valueType);
    DataTypeAstBuilder direction(Direction direction);
    DataTypeAstBuilder beanType(Class<?> beanType);
    DataTypeAstBuilder description(String description);
    DataTypeAstBuilder values(String values);
    DataTypeAstBuilder property();
    AstDataType build();
  }
  
  interface DecisionAstBuilder {
    DecisionAstBuilder src(List<AstCommandType> src);
    DecisionAstBuilder src(JsonNode src);
    DecisionAstBuilder rev(Integer version);
    DecisionAstType build();
  }
  
  interface FlowAstBuilder {
    FlowAstBuilder src(List<AstCommandType> src);
    FlowAstBuilder src(ArrayNode src);
    FlowAstBuilder srcAdd(int line, String value);
    FlowAstBuilder srcDel(int line);
    FlowAstBuilder rev(Integer version);
    FlowAstBuilder autocomplete(boolean autocomplete);
    FlowAstBuilder visitors(NodeFlowVisitor ... visitors);
    FlowAstType build();
  }

  interface ServiceAstBuilder {
    ServiceAstBuilder src(List<AstCommandType> src);
    ServiceAstBuilder src(ArrayNode src);
    ServiceAstBuilder rev(Integer version);
    ServiceAstType build();
  }
}
