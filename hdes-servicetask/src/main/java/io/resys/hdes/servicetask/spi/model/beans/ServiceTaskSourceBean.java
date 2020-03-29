package io.resys.hdes.servicetask.spi.model.beans;

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

import io.resys.hdes.datatype.api.DataTypeCommand;


public class ServiceTaskSourceBean implements Comparable<ServiceTaskSourceBean> {
  private int line;
  private List<DataTypeCommand> commands = new ArrayList<>();

  public ServiceTaskSourceBean(int line) {
    super();
    this.line = line;
  }
  public ServiceTaskSourceBean(int line, DataTypeCommand command) {
    super();
    this.line = line;
    this.commands.add(command);
  }
  public int getLine() {
    return line;
  }
  public List<DataTypeCommand> getCommands() {
    return Collections.unmodifiableList(commands);
  }
  public ServiceTaskSourceBean add(DataTypeCommand command) {
    commands.add(command);
    return this;
  }
  public void setLine(int line) {
    this.line = line;
  }
  public String getValue() {
    if(commands.isEmpty()) {
      return null;
    }
    return commands.get(commands.size() - 1).getValue();
  }
  @Override
  public int compareTo(ServiceTaskSourceBean o) {
    return Integer.compare(getLine(), o.getLine());
  }
}
