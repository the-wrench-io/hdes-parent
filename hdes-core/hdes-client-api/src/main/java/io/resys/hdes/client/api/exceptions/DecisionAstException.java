package io.resys.hdes.client.api.exceptions;

/*-
 * #%L
 * hdes-client-api
 * %%
 * Copyright (C) 2020 - 2021 Copyright 2020 ReSys OÜ
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

import io.resys.hdes.client.api.ast.AstCommandType;

public class DecisionAstException extends RuntimeException {

  private static final long serialVersionUID = -7154685569622201632L;

  private final AstCommandType command;

  public DecisionAstException(AstCommandType command, String message, Throwable cause) {
    super(message, cause);
    this.command = command;
  }

  public DecisionAstException(String message) {
    super(message);
    this.command = null;
  }
  public DecisionAstException(String message, Throwable cause) {
    super(message, cause);
    this.command = null;
  }

  public DecisionAstException(AstCommandType command, String message) {
    super(message);
    this.command = command;
  }

  public AstCommandType getCommand() {
    return command;
  }

}
