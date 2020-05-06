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

import io.resys.hdes.datatype.api.DataType.ValueType;
import io.resys.hdes.datatype.spi.Assert;

public class DataTypeExpressionException extends HdesException {
  private static final long serialVersionUID = -7022488916164046787L;

  public DataTypeExpressionException(String message, Exception e) {
    super(message, e);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private String msg;
    private String src;
    private ValueType valueType;
    private Exception original;

    public Builder src(String src) {
      this.src = src;
      return this;
    }
    public Builder valueType(ValueType valueType) {
      this.valueType = valueType;
      return this;
    }
    public Builder msg(String msg) {
      this.msg = msg;
      return this;
    }
    public Builder original(Exception original) {
      this.original = original;
      return this;
    }
    public DataTypeExpressionException build() {
      Assert.notNull(valueType, () -> "valueType can't be null!");
      
      String message = String.format("Exception while creating expression for type: %s", valueType);
      if(msg != null) {
        message += ", " + msg;
      }
      if(src != null) {
        message += ", expression: " + src;
      }
      if(original != null) {
        message += "," + System.lineSeparator() + "original message: " + original.getMessage();
      }

      return new DataTypeExpressionException(message, original);
    }
  }
}
