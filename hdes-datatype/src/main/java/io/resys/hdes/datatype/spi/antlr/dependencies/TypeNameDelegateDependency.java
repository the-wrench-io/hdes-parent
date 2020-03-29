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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.resys.hdes.datatype.api.DataType;
import io.resys.hdes.datatype.api.DataType.Direction;
import io.resys.hdes.datatype.api.DataType.ValueType;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.TypeName;
import io.resys.hdes.datatype.api.DataTypeExpressionAstNode.TypeNameDependency;
import io.resys.hdes.datatype.spi.builders.DataTypeBuilder;

public class TypeNameDelegateDependency implements TypeNameDependency {
  private final List<TypeNameDependency> delegates;

  public TypeNameDelegateDependency(List<TypeNameDependency> delegates) {
    super();
    this.delegates = delegates;
  }

  @Override
  public DataType apply(TypeName arg0) {
    for (TypeNameDependency delegate : delegates) {
      DataType resolved = delegate.apply(arg0);
      if (resolved != null) {
        return resolved;
      }
    }
    return null;
  }

  public static TypeNameDependency from(List<TypeNameDependency> delegate) {
    List<TypeNameDependency> delegates = new ArrayList<>(delegate);
    return new TypeNameDelegateDependency(delegates);
  }

  public static class SimpleTypeNameDependency implements TypeNameDependency {
    private final Map<String, DataType> dependencies;

    public SimpleTypeNameDependency(Map<String, DataType> dependencies) {
      super();
      this.dependencies = dependencies;
    }

    @Override
    public DataType apply(TypeName t) {
      return dependencies.get(t.getValue());
    }

    public static Builder builder() {
      return new Builder();
    }

    public static class Builder {
      private final Map<String, DataType> dependencies = new HashMap<>();
      
      public Builder add(String name, ValueType valueType) {
        DataType type = new DataTypeBuilder().direction(Direction.IN).name(name).valueType(valueType).build();
        dependencies.put(type.getName(), type);
        return this;
      }
      
      public SimpleTypeNameDependency build() {
        return new SimpleTypeNameDependency(dependencies);
      }
    }
  }
}
