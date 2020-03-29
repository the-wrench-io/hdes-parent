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

import io.resys.hdes.datatype.api.exceptions.HdesException;
import io.resys.hdes.datatype.spi.Assert;

public class FlowAstException extends HdesException {
  private static final long serialVersionUID = -3057343235911345568L;

  public FlowAstException(String message, Exception e) {
    super(message, e);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String msg;
    private FlowModel.Root root;
    private FlowModel.Task task;
    private Exception original;

    public Builder root(FlowModel.Root root) {
      this.root = root;
      return this;
    }

    public Builder task(FlowModel.Task task) {
      this.task = task;
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

    public FlowAstException build() {
      Assert.notNull(root, () -> "root can't be null");
      Assert.notNull(task, () -> "task can't be null");

      String message = String.format("Exception in creating flow ast for model: %s, rev: %s, task: %s",
        root.getId().getValue(), root.getRev(), task.getId().getValue());

      if (msg != null) {
        message += " because of: " + msg;
      }
      if (original != null) {
        message += "," + System.lineSeparator() + "original message:" + original.getMessage();
      }
      return new FlowAstException(message, original);
    }
  }
}
