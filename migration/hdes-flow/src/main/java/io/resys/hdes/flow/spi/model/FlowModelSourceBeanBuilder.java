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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Range;

import io.resys.hdes.datatype.api.DataTypeCommand;
import io.resys.hdes.datatype.api.ImmutableDataTypeCommand;
import io.resys.hdes.datatype.spi.Assert;
import io.resys.hdes.flow.api.FlowCommandType;
import io.resys.hdes.flow.spi.model.beans.FlowModelRootBean;
import io.resys.hdes.flow.spi.model.beans.FlowModelSourceBean;
import io.resys.hdes.flow.spi.model.beans.KeywordAndValueBean;

public class FlowModelSourceBeanBuilder {
  private final ObjectMapper yamlMapper;
  private final String lineSeperator;
  private Integer rev;
  
  public FlowModelSourceBeanBuilder(String lineSeperator, ObjectMapper yamlMapper) {
    super();
    this.lineSeperator = lineSeperator;
    this.yamlMapper = yamlMapper;
  }
  
  public FlowModelSourceBeanBuilder rev(Integer version) {
    this.rev = version;
    return this;
  }

  public List<FlowModelSourceBean> build(List<DataTypeCommand> src) {
    List<FlowModelSourceBean> added = new ArrayList<>();
    int index = 0;
    for (DataTypeCommand command : src) {
      index++;
      
      if(rev != null &&  index > rev) {
        break;
      }

      switch (FlowCommandType.valueOf(command.getType())) {
      
        case SET_NAME:
          Optional<FlowModelSourceBean> idModel = added.stream()
            .filter(s -> s.getKeywordAndValue() != null)
            .filter(s -> s.getKeywordAndValue().getIndent() == 0)
            .filter(s -> s.getKeywordAndValue().getKeyword().equals(FlowModelRootBean.KEY_ID))
            .findFirst();
          
          if(idModel.isPresent()) {
            added.add(new FlowModelSourceBean(idModel.get().getLine())
                .add(command, idModel.get().getKeywordAndValue().withValue(command.getValue())));
          } else {
            added.add(new FlowModelSourceBean(1)
                .add(command, new KeywordAndValueBean(FlowModelRootBean.KEY_ID, command.getValue(), 0)));            
          }
          
          break;
        case SET_CONTENT:
          //Clear all previous commands, set content clears all and sets everything from 0
          added.clear();

          String content = command.getValue();
          for (String lineContent : content.split(lineSeperator)) {
            int id = added.size() + 1;
            DataTypeCommand subCommand = ImmutableDataTypeCommand.builder()
                .id(id)
                .type(FlowCommandType.ADD.toString())
                .value(lineContent)
                .build();
            added.add(new FlowModelSourceBean(id).add(subCommand, getKeywordAndValue(command)));
          }

          break;

        case ADD: {
          int line = command.getId();
          added.stream().filter(s -> s.getLine() >= line).forEach(s -> s.setLine(s.getLine() + 1));
          added.add(new FlowModelSourceBean(line).add(command, getKeywordAndValue(command)));
          break;
        }
        case DELETE:

          // Delete set of lines
          int from = command.getId();
          int to = StringUtils.isEmpty(command.getValue()) ? command.getId(): Integer.parseInt(command.getValue());
          Range<Integer> range = Range.closed(from, to);
          int linesToDelete = (to - from) + 1;

          Iterator<FlowModelSourceBean> sources = added.iterator();
          while(sources.hasNext()) {
            FlowModelSourceBean source = sources.next();

            if(range.contains(source.getLine())) {
              sources.remove();
            } else if(source.getLine() > to) {
              source.setLine(source.getLine() - linesToDelete);
            }
          }
          
          break;
        case SET: {
          int line = command.getId();
          Optional<FlowModelSourceBean> source = added.stream().filter(s -> s.getLine() == line).findFirst();
          Assert.isTrue(source.isPresent(), () -> String.format("Can't change value of non existing line: %s!", line));
          source.get().add(command, getKeywordAndValue(command));
          break;
        }
        default:
          break;
      }
    }
    Collections.sort(added);
    return added;
  }
  
  private KeywordAndValueBean getKeywordAndValue(DataTypeCommand command) {
    String lineContent = command.getValue();
    String value = null;
    String keyword = null;
    try {
      JsonNode node = yamlMapper.readTree(lineContent);
      if(node == null || node.isNull()) {
        return null;
      }
      node = node.isArray() ? node.iterator().next() : node;
      Iterator<String> iterator = node.fieldNames();
      if(iterator.hasNext()) {
        keyword = iterator.next();
        JsonNode nodeValue = node.get(keyword);
        if (node.isNull()) {
          value = null;
        } else if (nodeValue.isArray()) {
          value = nodeValue.toString();
        } else {
          value = nodeValue.asText();
        }
      }
    } catch(Exception e) {
      return null;
    }
    
    if(keyword == null && value == null) {
      return null;
    }
    
    int indent = getIndent(lineContent);
    return new KeywordAndValueBean(keyword, value, indent);
  }
  
  private static int getIndent(String value){
    char[] characters = value.toCharArray();
    for(int index = 0; index < value.length(); index++){
      if(!Character.isWhitespace(characters[index])){
        return index;
      }
    }
    return 0;
  }
}
