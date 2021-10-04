package io.resys.hdes.client.api;

/*-
 * #%L
 * hdes-datatype
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

import io.resys.hdes.client.api.ast.AstType.Direction;
import io.resys.hdes.client.api.ast.AstType.ValueType;
import io.resys.hdes.client.api.model.DataType;

public interface HdesTypes {
  DataTypeBuilder create();

  interface DataTypeBuilder {
    DataTypeBuilder ref(String ref, DataType dataType);
    DataTypeBuilder required(boolean required);
    DataTypeBuilder name(String name);

    DataTypeBuilder valueType(ValueType valueType);
    DataTypeBuilder direction(Direction direction);
    DataTypeBuilder beanType(Class<?> beanType);
    DataTypeBuilder description(String description);
    DataTypeBuilder values(String values);
    DataTypeBuilder property();
    DataType build();
  }
}
