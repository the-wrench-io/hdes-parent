package io.resys.hdes.client.api;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.resys.hdes.client.api.ast.AstCommand;
import io.resys.hdes.client.api.ast.AstDecision;
import io.resys.hdes.client.api.ast.AstFlow;
import io.resys.hdes.client.api.ast.AstService;
import io.resys.hdes.client.api.ast.TypeDef;
import io.resys.hdes.client.api.ast.TypeDef.Direction;
import io.resys.hdes.client.api.ast.TypeDef.ValueType;

public interface HdesAstTypes {

  DecisionAstBuilder decision();
  FlowAstBuilder flow();
  ServiceAstBuilder service();
  DataTypeAstBuilder dataType();

  
  interface DataTypeAstBuilder {
    DataTypeAstBuilder id(String id);
    DataTypeAstBuilder order(Integer order);
    DataTypeAstBuilder valueType(ValueType valueType);
    
    DataTypeAstBuilder ref(String ref, TypeDef dataType);
    DataTypeAstBuilder required(boolean required);
    DataTypeAstBuilder data(boolean data);
    DataTypeAstBuilder name(String name);
    DataTypeAstBuilder script(String script);
    DataTypeAstBuilder direction(Direction direction);
    DataTypeAstBuilder description(String description);
    
    DataTypeAstBuilder beanType(Class<?> beanType);
    DataTypeAstBuilder values(String values);
    DataTypeAstBuilder property();
    TypeDef build();
  }
  
  interface DecisionAstBuilder {
    DecisionAstBuilder src(List<AstCommand> src);
    DecisionAstBuilder src(JsonNode src);
    DecisionAstBuilder rev(Integer version);
    AstDecision build();
  }
  
  interface FlowAstBuilder {
    FlowAstBuilder src(List<AstCommand> src);
    FlowAstBuilder src(ArrayNode src);
    FlowAstBuilder srcAdd(int line, String value);
    FlowAstBuilder srcDel(int line);
    FlowAstBuilder rev(Integer version);
    AstFlow build();
  }

  interface ServiceAstBuilder {
    ServiceAstBuilder src(List<AstCommand> src);
    ServiceAstBuilder src(ArrayNode src);
    ServiceAstBuilder rev(Integer version);
    AstService build();
  }
}
