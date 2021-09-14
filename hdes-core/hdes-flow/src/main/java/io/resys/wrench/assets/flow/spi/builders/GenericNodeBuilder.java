package io.resys.wrench.assets.flow.spi.builders;

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

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.Range;

import io.resys.wrench.assets.datatype.api.ImmutableAstCommandType;
import io.resys.wrench.assets.datatype.spi.util.Assert;
import io.resys.wrench.assets.flow.api.FlowAstFactory.NodeBuilder;
import io.resys.wrench.assets.flow.api.FlowAstFactory.NodeInputType;
import io.resys.wrench.assets.flow.api.model.FlowAst.FlowCommandMessage;
import io.resys.wrench.assets.flow.api.model.FlowAst.FlowCommandMessageType;
import io.resys.wrench.assets.flow.api.model.FlowAst.FlowCommandType;
import io.resys.wrench.assets.flow.api.model.ImmutableFlowCommandMessage;
import io.resys.wrench.assets.flow.spi.exceptions.NodeFlowException;
import io.resys.wrench.assets.flow.spi.model.NodeBean;
import io.resys.wrench.assets.flow.spi.model.NodeFlowBean;
import io.resys.wrench.assets.flow.spi.model.NodeSourceBean;
import io.resys.wrench.assets.flow.spi.support.FlowNodesFactory;

public class GenericNodeBuilder implements NodeBuilder {
  private final static String LINE_SEPARATOR = System.lineSeparator();
  private final ObjectMapper yamlMapper;
  private final Consumer<FlowCommandMessage> messageConsumer;
  private final NodeFlowBean result;
  private final List<NodeSourceBean> sourcesAdded = new ArrayList<>();
  private final List<NodeSourceBean> sourcesDeleted = new ArrayList<>();


  public GenericNodeBuilder(ObjectMapper yamlMapper, Collection<NodeInputType> inputTypes, Consumer<FlowCommandMessage> messageConsumer) {
    super();
    this.yamlMapper = yamlMapper;
    this.messageConsumer = messageConsumer;
    this.result = new NodeFlowBean(inputTypes);

  }

  @Override
  public GenericNodeBuilder add(int line, String value) {
    sourcesAdded.stream().filter(s -> s.getLine() >= line).forEach(s -> s.setLine(s.getLine() + 1));
    sourcesAdded.add(new NodeSourceBean(line, ImmutableAstCommandType.builder().id(String.valueOf(line)).value(value).type(FlowCommandType.ADD.name()).build()));
    return this;
  }

  @Override
  public GenericNodeBuilder set(int line, String value) {
    Optional<NodeSourceBean> source = sourcesAdded.stream().filter(s -> s.getLine() == line).findFirst();
    Assert.isTrue(source.isPresent(), () -> String.format("Can't change value of non existing line: %s!", line));
    source.get().add(ImmutableAstCommandType.builder().id(String.valueOf(line)).value(value).type(FlowCommandType.SET.name()).build());
    return this;
  }

  @Override
  public GenericNodeBuilder delete(int line) {
    Iterator<NodeSourceBean> sources = sourcesAdded.iterator();
    while(sources.hasNext()) {
      NodeSourceBean source = sources.next();
      if(source.getLine() == line) {
        sources.remove();
        sourcesDeleted.add(source);
      } else if(source.getLine() > line) {
        source.setLine(source.getLine() - 1);
      }
    }
    return this;
  }
  @Override
  public GenericNodeBuilder delete(int from, int to) {
    Range<Integer> range = Range.closed(from, to);
    int linesToDelete = (to - from) + 1;

    Iterator<NodeSourceBean> sources = sourcesAdded.iterator();
    while(sources.hasNext()) {
      NodeSourceBean source = sources.next();

      if(range.contains(source.getLine())) {
        sources.remove();
        sourcesDeleted.add(source);
      } else if(source.getLine() > to) {
        source.setLine(source.getLine() - linesToDelete);
      }
    }

    return this;
  }

  @Override
  public NodeFlowBean build() {
    Collections.sort(sourcesAdded);
    Iterator<NodeSourceBean> iterator = sourcesAdded.iterator();
    NodeBean parent = result;
    StringBuilder value = new StringBuilder();

    int previousLineNumber = 0;
    int lineNumber = 0;
    while(iterator.hasNext()) {
      NodeSourceBean src = iterator.next();
      String lineContent = src.getCommands().get(src.getCommands().size() - 1).getValue();
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

      boolean containsOnlySpaces = lineContent.length() > 0 && "".equals(lineContent.trim());
      boolean endsWithSpace = lineContent.endsWith(" ");
      if(containsOnlySpaces || endsWithSpace) {
        int start = containsOnlySpaces ? 0 : getSpaceStart(lineContent);
        int end = lineContent.length();
        messageConsumer.accept(ImmutableFlowCommandMessage.builder()
            .line(lineNumber)
            .range(FlowNodesFactory.range().build(start, end))
            .value("space has no meaning")
            .type(FlowCommandMessageType.WARNING)
            .build());
      }

      if(containsOnlySpaces || lineContent.length() == 0) {
        continue;
      }

      Map.Entry<String, String> keywordAndValue = getKeywordAndValue(yamlMapper, lineContent, lineNumber);
      if(keywordAndValue == null) {
        continue;
      }

      int indent = getIndent(lineContent);
      if(indent % 2 != 0) {
        String message = String.format("Incorrect indent: %s, at line: %s!", indent, lineNumber);
        messageConsumer.accept(ImmutableFlowCommandMessage.builder()
            .line(lineNumber)
            .value(message)
            .type(FlowCommandMessageType.ERROR)
            .build());
        return result.setEnd(lineNumber).setValue(buildSource(value));
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
        messageConsumer.accept(ImmutableFlowCommandMessage.builder()
            .line(lineNumber)
            .value(message)
            .type(FlowCommandMessageType.ERROR)
            .build());
        return result.setEnd(lineNumber).setValue(buildSource(value));
      }

      try {
        parent = parent.addChild(src, indent, keywordAndValue.getKey(), keywordAndValue.getValue());
      } catch(NodeFlowException e) {
        messageConsumer.accept(
            ImmutableFlowCommandMessage.builder()
            .line(lineNumber)
            .value(e.getMessage())
            .type(FlowCommandMessageType.ERROR)
            .build());
        return result.setEnd(lineNumber).setValue(value.toString());
      }
    }

    return result.setEnd(lineNumber).setValue(buildSource(value));
  }
  
  private String buildSource(StringBuilder value) {
    String result = value.toString();
    if(result.endsWith(LINE_SEPARATOR)) {
      return result.substring(0, result.length() - LINE_SEPARATOR.length());
    }
    return result;
  }

  private static int getSpaceStart(String lineContent) {
    char[] charArray = lineContent.toCharArray();
    int index = charArray.length;
    do {
      index--;
      if(charArray[index] != ' ') {
        break;
      }
    } while(index > -1);
    return index + 1;
  }

  private Map.Entry<String, String> getKeywordAndValue(ObjectMapper yamlMapper, String lineContent, int lineNumber) {
    String value;
    String keyword;
    try {
      JsonNode node = yamlMapper.readTree(lineContent);
      if(node == null || node.isNull()) {
        return null;
      }
      node = node.isArray() ? ((ArrayNode) node).iterator().next() : node;
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
      } else {
        String message = String.format("Unknown content on line: %d", lineNumber);
        messageConsumer.accept(ImmutableFlowCommandMessage.builder()
            .line(lineNumber)
            .value(message)
            .type(FlowCommandMessageType.ERROR)
            .build());
        return null;
      }
    } catch(IOException e) {
      String message = String.format("Unknown content on line: %d", lineNumber);
      messageConsumer.accept(ImmutableFlowCommandMessage.builder()
          .line(lineNumber)
          .value(message)
          .type(FlowCommandMessageType.ERROR)
          .build());
      return null;
    }
    return new AbstractMap.SimpleImmutableEntry<String, String>(keyword, value);
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
