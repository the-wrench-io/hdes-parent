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

public class RepoException extends RuntimeException {

  private static final long serialVersionUID = -5933566310053854060L;

  public RepoException() {
    super();
  }

  public RepoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public RepoException(String message, Throwable cause) {
    super(message, cause);
  }

  public RepoException(String message) {
    super(message);
  }

  public RepoException(Throwable cause) {
    super(cause);
  }
  
}
