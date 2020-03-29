package io.resys.hdes.flow.spi.model.beans;

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
import io.resys.hdes.flow.api.FlowModel;


public class FlowModelSourceBean implements Comparable<FlowModelSourceBean>, FlowModel.Source {
  private static final long serialVersionUID = 7861465598795670509L;
  private int line;
  private List<DataTypeCommand> commands = new ArrayList<>();
  private KeywordAndValueBean keywordAndValueBean;

  public FlowModelSourceBean(int line) {
    super();
    this.line = line;
  }
  @Override
  public int getLine() {
    return line;
  }
  @Override
  public List<DataTypeCommand> getCommands() {
    return Collections.unmodifiableList(commands);
  }
  @Override
  public int compareTo(FlowModelSourceBean o) {
    return Integer.compare(getLine(), o.getLine());
  }
  @Override
  public String getValue() {
    if(commands.isEmpty()) {
      return null;
    }
    return commands.get(commands.size() - 1).getValue();
  }
  
  public KeywordAndValueBean getKeywordAndValue() {
    return keywordAndValueBean;
  }
  public FlowModelSourceBean add(DataTypeCommand command, KeywordAndValueBean keywordAndValue) {
    this.keywordAndValueBean = keywordAndValue;
    this.commands.add(command);
    return this;
  }
  public FlowModelSourceBean setLine(int line) {
    this.line = line;
    return this;
  }
}
