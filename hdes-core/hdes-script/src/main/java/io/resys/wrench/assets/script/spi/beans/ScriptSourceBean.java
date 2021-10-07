package io.resys.wrench.assets.script.spi.beans;

import java.io.Serializable;

/*-
 * #%L
 * wrench-assets-flow
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.resys.hdes.client.api.ast.AstCommandType;


public class ScriptSourceBean implements Comparable<ScriptSourceBean>, Serializable {
  private static final long serialVersionUID = 7861465598795670509L;
  private int line;
  private List<AstCommandType> commands = new ArrayList<>();

  public ScriptSourceBean(int line) {
    super();
    this.line = line;
  }
  public ScriptSourceBean(int line, AstCommandType command) {
    super();
    this.line = line;
    this.commands.add(command);
  }
  public int getLine() {
    return line;
  }
  public List<AstCommandType> getCommands() {
    return Collections.unmodifiableList(commands);
  }
  public ScriptSourceBean add(AstCommandType command) {
    commands.add(command);
    return this;
  }
  public void setLine(int line) {
    this.line = line;
  }
  @Override
  public int compareTo(ScriptSourceBean o) {
    return Integer.compare(getLine(), o.getLine());
  }
  public String getValue() {
    if(commands.isEmpty()) {
      return null;
    }
    return commands.get(commands.size() - 1).getValue();
  }
}
