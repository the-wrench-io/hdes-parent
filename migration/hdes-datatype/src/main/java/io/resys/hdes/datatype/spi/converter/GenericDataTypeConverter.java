package io.resys.hdes.datatype.spi.converter;

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

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.datatype.api.DataType;
import io.resys.hdes.datatype.api.DataTypeInput;
import io.resys.hdes.datatype.api.DataTypeService;
import io.resys.hdes.datatype.api.DataTypeService.Converter;
import io.resys.hdes.datatype.api.exceptions.DataTypeConverterException;
import io.resys.hdes.datatype.spi.Assert;

public class GenericDataTypeConverter implements DataTypeService.Converter {

  private final ObjectMapper objectMapper;
  private Object inputObject;
  private DataTypeInput input;
  private Collection<DataType> dataTypes;
  
  public GenericDataTypeConverter(ObjectMapper objectMapper) {
    super();
    this.objectMapper = objectMapper;
  }
  
  @Override
  public Converter input(DataTypeInput input) {
    this.input = input;
    return this;
  }
  
  @Override
  public Converter input(Object entity) {
    this.inputObject = entity;
    return this;
  }
  
  @Override
  public Converter dataTypes(Collection<DataType> dataTypes) {
    this.dataTypes = dataTypes;
    return this;
  }

  @Override
  public <T> T build(Class<T> type) {
    Assert.isTrue(input != null || inputObject != null, () -> "input can't be null!");
    Assert.notNull(type, () -> "type can't be null!");
    
    try {
      if(input != null) {
        Assert.notNull(dataTypes, () -> "dataTypes can't be null!");
        Map<String, Object> mapped = dataTypes.stream().collect(Collectors.toMap(d -> d.getName(), d -> input.apply(d)));
        return objectMapper.convertValue(mapped, type);
      }
      return objectMapper.convertValue(inputObject, type);
    } catch(Exception e) {
      throw DataTypeConverterException.builder().type(dataTypes).original(e).build();
    }
  }
}
