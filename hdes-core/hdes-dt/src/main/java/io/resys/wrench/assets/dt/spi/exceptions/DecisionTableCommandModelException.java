package io.resys.wrench.assets.dt.spi.exceptions;

/*-
 * #%L
 * wrench-assets-dt
 * %%
 * Copyright (C) 2016 - 2018 Copyright 2016 ReSys OÜ
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

import io.resys.wrench.assets.dt.api.model.DecisionTableAst.Command;

public class DecisionTableCommandModelException extends RuntimeException {

  private static final long serialVersionUID = -7154685569622201632L;

  private final Command command;

  public DecisionTableCommandModelException(Command command, String message, Throwable cause) {
    super(message, cause);
    this.command = command;
  }

  public DecisionTableCommandModelException(String message) {
    super(message);
    this.command = null;
  }

  public DecisionTableCommandModelException(Command command, String message) {
    super(message);
    this.command = command;
  }

  public Command getCommand() {
    return command;
  }

}
