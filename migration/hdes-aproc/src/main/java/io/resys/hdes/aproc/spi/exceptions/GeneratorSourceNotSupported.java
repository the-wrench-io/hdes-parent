package io.resys.hdes.aproc.spi.exceptions;

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

import io.resys.hdes.datatype.api.exceptions.HdesException;
import io.resys.hdes.datatype.spi.Assert;

public class GeneratorSourceNotSupported extends HdesException {
  private static final long serialVersionUID = 9163955084870511877L;

  public GeneratorSourceNotSupported(String message) {
    super(message);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String label;
    
    public Builder label(String label) {
      this.label = label;
      return this;
    }

    public GeneratorSourceNotSupported build() {
      Assert.notNull(label, () -> "label can't be null");
      String message = String.format("Not supported label: %s!", label);
      return new GeneratorSourceNotSupported(message);
    }
  }
}
