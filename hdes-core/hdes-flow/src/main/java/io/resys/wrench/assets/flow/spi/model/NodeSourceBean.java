package io.resys.wrench.assets.flow.spi.model;

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

import io.resys.wrench.assets.flow.api.FlowAstFactory.NodeSource;
import io.resys.wrench.assets.flow.api.model.FlowAst.FlowCommand;


public class NodeSourceBean implements Comparable<NodeSourceBean>, NodeSource {
  private static final long serialVersionUID = 7861465598795670509L;
  private int line;
  private List<FlowCommand> commands = new ArrayList<>();

  public NodeSourceBean(int line) {
    super();
    this.line = line;
  }
  public NodeSourceBean(int line, FlowCommand command) {
    super();
    this.line = line;
    this.commands.add(command);
  }
  @Override
  public int getLine() {
    return line;
  }
  @Override
  public List<FlowCommand> getCommands() {
    return Collections.unmodifiableList(commands);
  }
  public NodeSourceBean add(FlowCommand command) {
    commands.add(command);
    return this;
  }
  public void setLine(int line) {
    this.line = line;
  }
  @Override
  public int compareTo(NodeSourceBean o) {
    return Integer.compare(getLine(), o.getLine());
  }
  public String getValue() {
    if(commands.isEmpty()) {
      return null;
    }
    return commands.get(commands.size() - 1).getValue();
  }
}