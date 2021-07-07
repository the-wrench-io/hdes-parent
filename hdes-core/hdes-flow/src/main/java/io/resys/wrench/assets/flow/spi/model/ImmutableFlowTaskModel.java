package io.resys.wrench.assets.flow.spi.model;

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

import io.resys.wrench.assets.flow.api.model.FlowModel.FlowTaskModel;
import io.resys.wrench.assets.flow.api.model.FlowModel.FlowTaskType;
import io.resys.wrench.assets.flow.api.model.FlowModel.FlowTaskValue;

@JsonIgnoreProperties("previous")
public class ImmutableFlowTaskModel implements FlowTaskModel {

  private static final long serialVersionUID = -882704966799718075L;

  private final String id;
  private final List<FlowTaskModel> next = new ArrayList<>();

  private FlowTaskValue value;
  private FlowTaskType type;
  private FlowTaskModel previous;

  public ImmutableFlowTaskModel(String id, FlowTaskValue value, FlowTaskType type) {
    super();
    this.id = id;
    this.value = value;
    this.type = type;
  }

  public void addNext(ImmutableFlowTaskModel node) {
    node.previous = this;
    next.add(node);
  }

  @Override
  public FlowTaskValue getBody() {
    return value;
  }

  @Override
  public FlowTaskType getType() {
    return type;
  }

  @Override
  public FlowTaskModel getPrevious() {
    return previous;
  }

  @Override
  public List<FlowTaskModel> getNext() {
    return next;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public FlowTaskModel get(String id) {
    if(id == this.id) {
      return this;
    }

    for(FlowTaskModel node : next) {
      FlowTaskModel result = node.get(id);
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

    for(FlowTaskModel flowMetamodelNode : next) {
      result.append(((ImmutableFlowTaskModel) flowMetamodelNode).toString(visited));
    }

    return result.toString();
  }
  private String getIndent(List<String> visited) {
    FlowTaskModel flowMetamodelNode = previous;
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
