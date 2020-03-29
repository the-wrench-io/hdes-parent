package io.resys.hdes.datatype.spi;

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

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.datatype.api.DataTypeService;
import io.resys.hdes.datatype.api.exceptions.DataTypeReaderException;

public class ObjectMapperTypeBuilder implements DataTypeService.Reader {
  private final ObjectMapper objectMapper;
  private String src;
  private String classpath;

  public ObjectMapperTypeBuilder(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }
  @Override
  public DataTypeService.Reader src(String src) {
    this.src = src;
    return this;
  }
  @Override
  public DataTypeService.Reader classpath(String pattern) {
    this.classpath = pattern;
    return this;
  }

  @Override
  public <T> List<T> list(Class<T> targetType) {
    String src = null;
    try {
      if(classpath != null) {
        src = IOUtils.toString(getClass().getClassLoader().getResourceAsStream(classpath), StandardCharsets.UTF_8);
      } else {
        src = this.src;
      }
      
      JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, targetType);
      return objectMapper.readValue(src, javaType);
    } catch (Exception e) {
      throw DataTypeReaderException.builder().type(targetType).classpath(classpath).src(src).original(e).build();
    }
  }

  @Override
  public <T> T build(Class<T> targetType) {
    String src = null;
    try {
      if(classpath != null) {
        src = IOUtils.toString(getClass().getClassLoader().getResourceAsStream(classpath), StandardCharsets.UTF_8);
      } else {
        src = this.src;
      }
      return objectMapper.readValue(src, targetType);
    } catch (Exception e) {
      throw DataTypeReaderException.builder().type(targetType).classpath(classpath).src(src).original(e).build();
    }
  }
}
