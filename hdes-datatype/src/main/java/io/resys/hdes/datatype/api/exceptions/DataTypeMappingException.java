package io.resys.hdes.datatype.api.exceptions;

import io.resys.hdes.datatype.api.DataType.ValueType;

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

public class DataTypeMappingException extends HdesException {
  private static final long serialVersionUID = 1479713119727436525L;

  public DataTypeMappingException(String message, Exception e) {
    super(message, e);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private Object value;
    private ValueType valueType;
    private Exception original;

    public Builder type(ValueType valueType) {
      this.valueType = valueType;
      return this;
    }
    public Builder value(Object value) {
      this.value = value;
      return this;
    }
    public Builder original(Exception original) {
      this.original = original;
      return this;
    }
    public DataTypeMappingException build() {
      String message = String.format("Exception in mapping value type: %s, entity: $s!", valueType, value);
      if(original != null) {
        message += "," + System.lineSeparator() + "original message:" + original.getMessage();
      }
      return new DataTypeMappingException(message, original);
    }
  }
}
