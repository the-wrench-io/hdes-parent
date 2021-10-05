package io.resys.hdes.client.api;

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
