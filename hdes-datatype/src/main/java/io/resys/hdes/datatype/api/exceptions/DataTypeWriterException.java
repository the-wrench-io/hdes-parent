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

public class DataTypeWriterException extends HdesException {
  private static final long serialVersionUID = -3057343235911345568L;

  public DataTypeWriterException(String message, Exception e) {
    super(message, e);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private Object type;
    private Exception original;

    public Builder type(Object type) {
      this.type = type;
      return this;
    }
    public Builder original(Exception original) {
      this.original = original;
      return this;
    }
    public DataTypeWriterException build() {
      String message = String.format("Exception in writing type: %s ", type);
      if(original != null) {
        message += "," + System.lineSeparator() + "original message:" + original.getMessage();
      }
      return new DataTypeWriterException(message, original);
    }
  }
}
