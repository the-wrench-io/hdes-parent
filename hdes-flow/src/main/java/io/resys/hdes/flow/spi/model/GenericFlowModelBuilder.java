package io.resys.hdes.flow.spi.model;

/*-
 * #%L
 * hdes-flow
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.datatype.api.DataTypeCommand;
import io.resys.hdes.datatype.spi.Assert;
import io.resys.hdes.flow.api.FlowModel;
import io.resys.hdes.flow.api.FlowModelException;
import io.resys.hdes.flow.api.FlowService;
import io.resys.hdes.flow.api.FlowService.ModelBuilder;
import io.resys.hdes.flow.spi.model.beans.FlowModelBean;
import io.resys.hdes.flow.spi.model.beans.FlowModelRootBean;
import io.resys.hdes.flow.spi.model.beans.FlowModelSourceBean;
import io.resys.hdes.flow.spi.model.beans.KeywordAndValueBean;

public class GenericFlowModelBuilder implements FlowService.ModelBuilder {
  private final static String LINE_SEPARATOR = System.lineSeparator();
  private final FlowModelSourceBeanBuilder sourceBuilder;  

  private final FlowModelRootBean result;

  private final List<FlowModelException.FlowModelLineErrorDescription> errors = new ArrayList<>();
  private final List<DataTypeCommand> src = new ArrayList<>();


  public GenericFlowModelBuilder(ObjectMapper yamlMapper, FlowModelRootBean result) {
    this.sourceBuilder = new FlowModelSourceBeanBuilder(LINE_SEPARATOR, yamlMapper);
    this.result = result;
  }

  @Override
  public ModelBuilder src(Collection<DataTypeCommand> src) {
    Assert.notNull(src, ()-> "src can't be null");
    this.src.addAll(src);
    return this;
  }

  @Override
  public ModelBuilder rev(Integer version) {
    this.sourceBuilder.rev(version);
    return this;
  }

  @Override
  public FlowModel.Root build() {
    List<FlowModelSourceBean> added = sourceBuilder.build(src);
    Iterator<FlowModelSourceBean> iterator = added.iterator();
    FlowModelBean parent = result;
    StringBuilder value = new StringBuilder();

    int previousLineNumber = 0;
    int lineNumber = 0;
    while(iterator.hasNext()) {
      FlowModelSourceBean src = iterator.next();
      DataTypeCommand command = src.getCommands().get(src.getCommands().size() - 1);
      String lineContent = command.getValue();
      lineNumber = src.getLine();

      // add to src
      for(int index = previousLineNumber; index < lineNumber -1; index++) {
        value.append(LINE_SEPARATOR);
      }

      if(lineContent != null) {
        value.append(lineContent);
      }
      value.append(LINE_SEPARATOR);
      previousLineNumber = lineNumber;

      if(lineContent == null) {
        continue;
      }

      KeywordAndValueBean keywordAndValue = src.getKeywordAndValue();
      if(keywordAndValue == null) {
        continue;
      }

      int indent = keywordAndValue.getIndent();
      if(indent % 2 != 0) {
        String message = String.format("Incorrect indent: %s, at line: %s!", indent, lineNumber);
        addError(lineNumber, message, command);
        return result.setEnd(lineNumber).setValue(value.toString());
      }

      int indentToFind = indent - 2;
      while(parent != null) {
        if(parent.getIndent() <= indentToFind) {
          break;
        }
        parent = parent.getParent();
      }

      if(parent == null) {
        String message = String.format("Incorrect indent at line: %s, expecting: %s but was: %s!", lineNumber, indentToFind, indent);
        addError(lineNumber, message, command);
        return result.setEnd(lineNumber).setValue(value.toString());
      }

      try {
        parent = parent.addChild(src, indent, keywordAndValue.getKeyword(), keywordAndValue.getValue());
      } catch(FlowModelException e) {
        addError(lineNumber, e.getMessage(), command);
      }
    }

    if(!errors.isEmpty()) {
      String model = Optional.ofNullable(result.getId()).map(FlowModel::getValue).orElse("undefined");
      throw FlowModelException.builder().model(model).msg(this.errors).build();
    }
    return result.setEnd(lineNumber).setRev(added.size()).setValue(value.toString());
  }


  private void addError(int line, String message, DataTypeCommand command) {
    this.errors.add(new FlowModelException.FlowModelLineErrorDescription(line, message, command));
  }
}
