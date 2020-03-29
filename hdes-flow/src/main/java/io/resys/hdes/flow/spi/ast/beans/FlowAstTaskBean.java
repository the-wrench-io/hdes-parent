package io.resys.hdes.flow.spi.ast.beans;

/*-
 * #%L
 * wrench-component-assets-flow
 * %%
 * Copyright (C) 2016 - 2017 Copyright 2016 ReSys OÜ
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
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.resys.hdes.flow.api.FlowAst;
import io.resys.hdes.flow.api.FlowAst.FlowTaskType;

@JsonIgnoreProperties("previous")
public class FlowAstTaskBean implements FlowAst.Task {

  private static final long serialVersionUID = -882704966799718075L;

  private final String id;
  private final List<FlowAst.Task> next = new ArrayList<>();

  private FlowAst.TaskValue value;
  private FlowTaskType type;
  private FlowAst.Task previous;

  public FlowAstTaskBean(String id, FlowAst.TaskValue value, FlowTaskType type) {
    super();
    this.id = id;
    this.value = value;
    this.type = type;
  }

  public void addNext(FlowAstTaskBean node) {
    node.previous = this;
    next.add(node);
  }

  @Override
  public FlowAst.TaskValue getValue() {
    return value;
  }

  @Override
  public FlowTaskType getType() {
    return type;
  }

  @Override
  public FlowAst.Task getPrevious() {
    return previous;
  }

  @Override
  public List<FlowAst.Task> getNext() {
    return next;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public FlowAst.Task get(String id) {
    if(id.equals(this.id)) {
      return this;
    }

    for(FlowAst.Task node : next) {
      FlowAst.Task result = node.get(id);
      if(result != null) {
        return result;
      }
    }

    return null;
  }

  @Override
  public String toString() {
    List<String> visited = new ArrayList<>();
    return toString(visited);
  }

  public String toString(List<String> visited) {
    if(visited.contains(id)) {
      return " -> (recursion) " + id;
    }
    visited.add(id);

    StringBuilder result = new StringBuilder();

    final String prefix;
    if(this.type == FlowTaskType.DECISION) {
      prefix = System.lineSeparator() + getIndent(new ArrayList<>());
    } else {
      prefix = " -> ";
    }
    result.append(prefix);


    if(type != FlowTaskType.EXCLUSIVE) {
      result.append(id);
    }

    for(FlowAst.Task flowMetamodelNode : next) {
      result.append(((FlowAstTaskBean) flowMetamodelNode).toString(visited));
    }

    return result.toString();
  }
  private String getIndent(List<String> visited) {
    FlowAst.Task flowMetamodelNode = previous;
    int total = 4;
    while(flowMetamodelNode != null) {
      if(visited.contains(flowMetamodelNode.getId())) {
        break;
      }
      visited.add(flowMetamodelNode.getId());

      if(flowMetamodelNode.getType() != FlowTaskType.EXCLUSIVE) {
        total += flowMetamodelNode.getId().length() + 4;
      }
      flowMetamodelNode = flowMetamodelNode.getPrevious();
    }

    StringBuilder result = new StringBuilder();
    for(int index = 0; index <= total; index++) {
      result.append(" ");
    }
    return result.toString();
  }
}
