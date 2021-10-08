package io.resys.hdes.client.spi.flow.program;

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

import io.resys.hdes.client.api.execution.FlowProgram.FlowTaskType;
import io.resys.hdes.client.api.execution.FlowProgram.Step;
import io.resys.hdes.client.api.execution.FlowProgram.StepBody;

@JsonIgnoreProperties("previous")
public class ImmutableStep implements Step {
  public static final ImmutableStep EMPTY = new ImmutableStep("empty", null, FlowTaskType.END);
  

  private static final long serialVersionUID = -882704966799718075L;

  private final String id;
  private final List<Step> next = new ArrayList<>();

  private StepBody value;
  private FlowTaskType type;
  private Step previous;

  public ImmutableStep(String id, StepBody value, FlowTaskType type) {
    super();
    this.id = id;
    this.value = value;
    this.type = type;
  }

  public void addNext(ImmutableStep node) {
    node.previous = this;
    next.add(node);
  }

  @Override
  public StepBody getBody() {
    return value;
  }

  @Override
  public FlowTaskType getType() {
    return type;
  }

  @Override
  public Step getPrevious() {
    return previous;
  }

  @Override
  public List<Step> getNext() {
    return next;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public Step get(String id) {
    if(id == this.id) {
      return this;
    }

    for(Step node : next) {
      Step result = node.get(id);
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

    for(Step flowMetamodelNode : next) {
      result.append(((ImmutableStep) flowMetamodelNode).toString(visited));
    }

    return result.toString();
  }
  private String getIndent(List<String> visited) {
    Step flowMetamodelNode = previous;
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
