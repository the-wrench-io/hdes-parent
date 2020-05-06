package io.resys.hdes.storage.api;

/*-
 * #%L
 * hdes-storage
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

public class ChangesRevisionException extends HdesException {
  private static final long serialVersionUID = 9163955084870511877L;

  public ChangesRevisionException(String message) {
    super(message);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private int revision;
    private Changes changes;
    
    public Builder revision(int revision) {
      this.revision = revision;
      return this;
    }

    public Builder changes(Changes changes) {
      this.changes = changes;
      return this;
    }

    public ChangesRevisionException build() {
      Assert.notNull(changes, () -> "changes can't be null");
      String message = String.format("Can't update changes because revision does not match, id: %s, rev: %s != %s", 
          changes.getId(), changes.getValues().size(), revision);
      return new ChangesRevisionException(message);
    }
  }
}
