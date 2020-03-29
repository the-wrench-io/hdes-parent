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

import io.resys.hdes.datatype.spi.Assert;

public class DataTypeReaderException extends HdesException {
  private static final long serialVersionUID = -7022488916164046787L;

  public DataTypeReaderException(String message, Exception e) {
    super(message, e);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private Class<?> type;
    private String pattern;
    private String src;
    private Exception original;

    public Builder classpath(String pattern) {
      this.pattern = pattern;
      return this;
    }
    public Builder src(String src) {
      this.src = src;
      return this;
    }
    public Builder type(Class<?> type) {
      this.type = type;
      return this;
    }
    public Builder original(Exception original) {
      this.original = original;
      return this;
    }
    public DataTypeReaderException build() {
      Assert.notNull(type, () -> "type can't be null!");
      String message;
      if(pattern != null) {
        message = String.format("Exception in reading from classpath: %s and converting into: %s ", pattern, type.getSimpleName());
      } else {
        message = String.format("Exception in reading: %s and converting into: %s ", src, type.getSimpleName());
      }

      if(original != null) {
        message += "," + System.lineSeparator() + "original message: " + original.getMessage();
      }

      return new DataTypeReaderException(message, original);
    }
  }
}
