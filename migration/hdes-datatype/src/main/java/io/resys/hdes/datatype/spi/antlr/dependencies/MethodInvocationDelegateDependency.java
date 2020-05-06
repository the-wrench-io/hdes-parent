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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.resys.hdes.datatype.api.DataType;
import io.resys.hdes.datatype.api.DataType.Direction;
import io.resys.hdes.datatype.api.DataType.ValueType;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.MethodInvocation;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.MethodInvocationDependency;
import io.resys.hdes.datatype.spi.builders.DataTypeBuilder;

public class MethodInvocationDelegateDependency implements MethodInvocationDependency {

  private final List<MethodInvocationDependency> delegates;
  
  public MethodInvocationDelegateDependency(List<MethodInvocationDependency> delegates) {
    super();
    this.delegates = delegates;
  }

  @Override
  public DataType apply(MethodInvocation arg0) {
    for(MethodInvocationDependency delegate : delegates) {
      DataType resolved = delegate.apply(arg0);
      if(resolved != null) {
        return resolved;
      }
    }
    return null;
  }
  
  public static MethodInvocationDependency from(List<MethodInvocationDependency> delegate) {
    List<MethodInvocationDependency> delegates = new ArrayList<>(delegate);
    delegates.add(new MethodInvocationDependencyReserved());
    return new MethodInvocationDelegateDependency(delegates);
  }
  
  
  public static class MethodInvocationDependencyReserved implements MethodInvocationDependency {

    private static final Map<String, DataType> RESERVED;
    
    static {
      DataType sum = new DataTypeBuilder()
      .valueType(ValueType.METHOD_INVOCATION)
      .name("sum")
      .property((property) -> property.direction(Direction.IN).valueType(ValueType.NUMERIC).array(true).build())
      .property((property) -> property.direction(Direction.OUT).valueType(ValueType.NUMERIC).build())
      .build();

      DataType avg = new DataTypeBuilder()
      .valueType(ValueType.METHOD_INVOCATION)
      .name("avg")
      .property((property) -> property.direction(Direction.IN).valueType(ValueType.NUMERIC).array(true).build())
      .property((property) -> property.direction(Direction.OUT).valueType(ValueType.NUMERIC).build())
      .build();
      
      Map<String, DataType> reserved = new HashMap<>();
      reserved.put(sum.getName(), sum);
      reserved.put(avg.getName(), avg);
      RESERVED = Collections.unmodifiableMap(reserved);
    }
   
    
    @Override
    public DataType apply(MethodInvocation t) {
      if(t.getTypeName() == null && RESERVED.containsKey(t.getName().getValue())) {
        return RESERVED.get(t.getName().getValue());
      }
      return null;
    }
  }

}
