package io.resys.hdes.datatype.spi.mapper;

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

import io.resys.hdes.datatype.api.DataType;
import io.resys.hdes.datatype.api.DataTypeService;
import io.resys.hdes.datatype.spi.mapper.MapperFactory.Deserializer;
import io.resys.hdes.datatype.spi.mapper.MapperFactory.Serializer;

public class GenericDataTypeMapper implements DataTypeService.Mapper {

  private final MapperFactory.Serializer serializer;
  private final MapperFactory.Deserializer deserializer;
  
  public GenericDataTypeMapper(Serializer serializer, Deserializer deserializer) {
    super();
    this.serializer = serializer;
    this.deserializer = deserializer;
  }
  @Override
  public Serializable toValue(Object value, DataType dataType) {
    return deserializer.deserialize(dataType, value);
  }
  @Override
  public String toString(Object value, DataType dataType) {
    return serializer.serialize(dataType, value);
  }
}
