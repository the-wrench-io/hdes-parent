package io.resys.wrench.assets.datatype.spi.deserializers;

/*-
 * #%L
 * wrench-assets-datatype
 * %%
 * Copyright (C) 2016 - 2019 Copyright 2016 ReSys OÜ
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

import io.resys.wrench.assets.datatype.api.DataTypeRepository.DataType;

public class DataTypeException extends RuntimeException {
  private static final long serialVersionUID = 1479713119727436525L;
  private final DataType dataType;
  private final Object value;

  public DataTypeException(DataType dataType, Object value, Exception e) {
    super(e.getMessage(), e);
    this.dataType = dataType;
    this.value = value;
  }

  public DataType getDataType() {
    return dataType;
  }

  public Object getValue() {
    return value;
  }
}
