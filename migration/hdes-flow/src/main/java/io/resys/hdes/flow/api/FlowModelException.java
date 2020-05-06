package io.resys.hdes.flow.api;

/*-
 * #%L
 * hdes-flow
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

import java.util.List;

import io.resys.hdes.datatype.api.DataTypeCommand;
import io.resys.hdes.datatype.api.exceptions.HdesException;

public class FlowModelException extends HdesException {
  private static final long serialVersionUID = -3057343235911345568L;

  public FlowModelException(String message, Exception e) {
    super(message, e);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class FlowModelLineErrorDescription {
    private final int line;
    private final String value;
    private final DataTypeCommand command;

    public FlowModelLineErrorDescription(int line, String value, DataTypeCommand command) {
      super();
      this.line = line;
      this.value = value;
      this.command = command;
    }

    public int getLine() {
      return line;
    }

    public String getValue() {
      return value;
    }
    
    public DataTypeCommand getCommand() {
      return command;
    }
  }

  public static class Builder {
    private String msg;
    private String model;
    private List<FlowModelLineErrorDescription> messages;
    private Exception original;

    public Builder model(String model) {
      this.model = model;
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

    public Builder msg(List<FlowModelLineErrorDescription> messages) {
      this.messages = messages;
      return this;
    }

    public FlowModelException build() {
      
      String message = "";
      if(msg != null) {
        message = msg;
      }
      if(model != null) {
        message += String.format("Exception in creating flow model: %s ", model);
      }
      if(messages != null) {
        StringBuilder builder = new StringBuilder();
        this.messages.forEach(m -> builder.append(String.format("line: %s, error: %s", m.getLine(), m.getValue())).append(System.lineSeparator()));
        message += System.lineSeparator() + builder.toString();
      }
      if (original != null) {
        message += "," + System.lineSeparator() + "original message:" + original.getMessage();
      }
      return new FlowModelException(message, original);
    }
  }
}
