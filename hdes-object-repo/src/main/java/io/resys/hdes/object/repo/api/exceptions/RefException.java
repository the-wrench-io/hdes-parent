package io.resys.hdes.object.repo.api.exceptions;

/*-
 * #%L
 * hdes-object-repo
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

public class RefException extends RepoException {
  private static final long serialVersionUID = -2123781385633987779L;

  public RefException(String msg) {
    super(msg);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    public String duplicateTag(String tag) {
      return new StringBuilder()
          .append("Tag with name: ").append(tag)
          .append(" already exists!")
          .toString();
    }

    public String refNameMatch(String tag) {
      return new StringBuilder()
          .append("Tag with name: ").append(tag)
          .append(" matches with one of the REF names!")
          .toString();
    }

    public String refUnknown(String ref) {
      return new StringBuilder()
          .append("Ref with name: ").append(ref)
          .append(" is unknown!")
          .toString();
    }
  }
}
