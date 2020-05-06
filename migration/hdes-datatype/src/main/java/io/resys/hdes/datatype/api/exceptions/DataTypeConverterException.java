package io.resys.hdes.datatype.api.exceptions;

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
import java.util.stream.Collectors;

import io.resys.hdes.datatype.api.DataType;
import io.resys.hdes.datatype.spi.Assert;

public class DataTypeConverterException extends HdesException {
  private static final long serialVersionUID = -3057343235911345568L;

  public DataTypeConverterException(String message, Exception e) {
    super(message, e);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private Collection<DataType> types;
    private Exception original;

    public Builder type(Collection<DataType> types) {
      this.types = types;
      return this;
    }
    public Builder original(Exception original) {
      this.original = original;
      return this;
    }
    public DataTypeConverterException build() {
      Assert.notNull(types, () -> "types can't be null");
      String types = this.types.stream().map(d -> d.getName()).collect(Collectors.toList()).toString();
      
      String message = String.format("Exception in converting data types: %s ", types);
      if(original != null) {
        message += "," + System.lineSeparator() + "original message:" + original.getMessage();
      }
      return new DataTypeConverterException(message, original);
    }
  }
}
