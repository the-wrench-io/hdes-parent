package io.resys.wrench.assets.script.spi.beans;

/*-
 * #%L
 * wrench-assets-script
 * %%
 * Copyright (C) 2016 - 2019 Copyright 2016 ReSys OÜ
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

import io.resys.wrench.assets.script.api.ScriptRepository.ScriptCommand;
import io.resys.wrench.assets.script.api.ScriptRepository.ScriptCommandType;

public class ScriptSourceCommandBean implements ScriptCommand {
  private static final long serialVersionUID = -7377195609584445220L;
  private final int id;
  private final String value;
  private final ScriptCommandType type;

  public ScriptSourceCommandBean(int id, String value, ScriptCommandType type) {
    super();
    this.id = id;
    this.value = value;
    this.type = type;
  }
  public ScriptSourceCommandBean(int id, ScriptCommandType type) {
    super();
    this.id = id;
    this.value = null;
    this.type = type;
  }
  @Override
  public int getId() {
    return id;
  }
  @Override
  public String getValue() {
    return value;
  }
  @Override
  public ScriptCommandType getType() {
    return type;
  }
}
