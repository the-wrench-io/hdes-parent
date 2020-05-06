package io.resys.hdes.aproc.spi.generator;

/*-
 * #%L
 * hdes-aproc
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

public class NameFactory {

  public static ExecutableNameBuilder executable() {
    return new ExecutableNameBuilder();
  }

  public static class ExecutableNameBuilder {
    private String label;
    private String model;
    private String tagId;

    public ExecutableNameBuilder tagId(String tagId) {
      this.tagId = tagId;
      return this;
    }
    public ExecutableNameBuilder label(String label) {
      this.label = label;
      return this;
    }
    public ExecutableNameBuilder model(String model) {
      this.model = model;
      return this;
    }
    public String build() {
      Assert.notNull(tagId, () -> "tagId can't be null!");
      Assert.notNull(label, () -> "label can't be null!");
      Assert.notNull(model, () -> "model can't be null!");
      return "ImmutableExecutable_" + tagId + "_" + label +  "_" + model;
    }
  }
}
