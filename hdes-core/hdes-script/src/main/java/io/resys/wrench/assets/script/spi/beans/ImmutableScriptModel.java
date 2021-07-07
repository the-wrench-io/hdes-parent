package io.resys.wrench.assets.script.spi.beans;

import java.util.List;

import io.resys.wrench.assets.script.api.ScriptRepository.ScriptCommand;

/*-
 * #%L
 * wrench-assets-script
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

import io.resys.wrench.assets.script.api.ScriptRepository.ScriptMethodModel;
import io.resys.wrench.assets.script.api.ScriptRepository.ScriptModel;

public class ImmutableScriptModel implements ScriptModel {
  private static final long serialVersionUID = -8594229600313874884L;
  private final String id;
  private final String src;
  private final int rev;
  private final List<ScriptCommand> commands;
  private final Class<?> type;
  private final ScriptMethodModel method;

  public ImmutableScriptModel(String id, int rev, String src, List<ScriptCommand> commands, Class<?> type, ScriptMethodModel method) {
    this.id = id;
    this.rev = rev;
    this.src = src;
    this.commands = commands;
    this.type = type;
    this.method = method;
  }
  @Override
  public String getName() {
    return id;
  }
  @Override
  public String getSrc() {
    return src;
  }
  @Override
  public List<ScriptCommand> getCommands() {
    return commands;
  }
  @Override
  public Class<?> getType() {
    return type;
  }
  @Override
  public ScriptMethodModel getMethod() {
    return method;
  }
  @Override
  public int getRev() {
    return rev;
  }
  @Override
  public String getDescription() {
    return null;
  }
}
