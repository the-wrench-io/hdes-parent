package io.resys.hdes.datatype.spi.antlr.dependencies;

/*-
 * #%L
 * hdes-datatype
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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.resys.hdes.datatype.api.DataType;
import io.resys.hdes.datatype.api.DataType.Direction;
import io.resys.hdes.datatype.api.DataType.ValueType;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.EvalNode;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.LiteralType;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.MethodInvocation;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.TypeName;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.TypeNameDependency;
import io.resys.hdes.datatype.api.ImmutableErrorNode;
import io.resys.hdes.datatype.spi.antlr.errors.AstNodeException;
import io.resys.hdes.datatype.spi.builders.DataTypeBuilder;

public class InMemoryDependency implements TypeNameDependency {
  private final Map<String, DataType> spec;
  private final Map<String, Serializable> value;
  private final Map<String, MethodInvoker> methods;

  public InMemoryDependency(
      Map<String, DataType> spec, 
      Map<String, Serializable> value, 
      Map<String, MethodInvoker> methods) {
    super();
    this.spec = spec;
    this.value = value;
    this.methods = methods;
  }

  @Override
  public DataType apply(TypeName t) {
    return spec.get(t.getValue());
  }

  public Serializable invoke(TypeName t) {
    if(!value.containsKey(t.getValue())) {
      throw new AstNodeException(Arrays.asList(ImmutableErrorNode.builder()
          .message("Type name is undefined")
          .target(t)
          .build()));
    }
    
    return value.get(t.getValue());
  }

  public Serializable invoke(MethodInvocation t, List<EvalNode> args) {
    String name = t.getName().getValue();
    String typeName = t.getTypeName() == null ? "" :  t.getTypeName().getValue() + ".";
    String fullName = typeName + name;
    if(!methods.containsKey(fullName)) {
      throw new AstNodeException(Arrays.asList(ImmutableErrorNode.builder()
          .message("Method is undefined")
          .target(t)
          .build()));
    }
    
    return methods.get(fullName).invoke(t, args);
  }
  
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private final Map<String, DataType> spec = new HashMap<>();
    private final Map<String, Serializable> value = new HashMap<>();
    private final Map<String, MethodInvoker> methods = new HashMap<>();
    
    public Builder add(String name, ValueType valueType, Serializable value) {
      DataType type = new DataTypeBuilder().direction(Direction.IN).name(name).valueType(valueType).build();
      this.spec.put(type.getName(), type);
      this.value.put(name, value);
      return this;
    }
    
    public InMemoryDependency build() {
      this.methods.put("sum", new SumMethodInvoker());
      return new InMemoryDependency(spec, value, methods);
    }
  }
  
  public static class SumMethodInvoker implements MethodInvoker {

    @Override
    public Serializable invoke(MethodInvocation t, List<EvalNode> args) {

      if(t.getLiteralType() == LiteralType.INTEGER) {
        int sum = 0;
        for(EvalNode node : args) {
          sum += (Integer) node.getValue();
        }
        return sum;
      } 
      
      BigDecimal sum = BigDecimal.ZERO;
      for(EvalNode node : args) {
        sum = sum.add((BigDecimal) node.getValue());
      }
      return sum;
    }
  }
  
  public interface MethodInvoker {
    Serializable invoke(MethodInvocation t, List<EvalNode> args);
  }
}
