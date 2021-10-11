package io.resys.wrench.assets.flow.spi;

/*-
 * #%L
 * hdes-flow
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

import io.resys.hdes.client.api.programs.FlowProgram.Step;

public class FlowDefinitionException extends RuntimeException {

  private static final long serialVersionUID = 2978117894572351791L;

  private final Step node;

  public FlowDefinitionException(String message, Step node, Throwable cause) {
    super(message, cause);
    this.node = node;
  }

  public FlowDefinitionException(String message, Throwable cause) {
    super(message, cause);
    this.node = null;
  }

  public FlowDefinitionException(String message, Step node) {
    super(message);
    this.node = node;
  }
  public FlowDefinitionException(String message) {
    super(message);
    this.node = null;
  }
  public Step getNode() {
    return node;
  }
}
