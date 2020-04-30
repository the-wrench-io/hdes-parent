package io.resys.hdes.ast.spi.visitors.ast.util;

/*-
 * #%L
 * hdes-ast
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.immutables.value.Value;

import io.resys.hdes.ast.api.AstNodeException;
import io.resys.hdes.ast.api.nodes.FlowNode.EndPointer;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowLoop;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowTaskNode;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowTaskPointer;
import io.resys.hdes.ast.api.nodes.FlowNode.ThenPointer;
import io.resys.hdes.ast.api.nodes.FlowNode.WhenThen;
import io.resys.hdes.ast.api.nodes.FlowNode.WhenThenPointer;
import io.resys.hdes.ast.api.nodes.ImmutableErrorNode;
import io.resys.hdes.ast.api.nodes.ImmutableFlowLoop;
import io.resys.hdes.ast.api.nodes.ImmutableFlowTaskNode;
import io.resys.hdes.ast.api.nodes.ImmutableThenPointer;
import io.resys.hdes.ast.api.nodes.ImmutableWhenThen;
import io.resys.hdes.ast.api.nodes.ImmutableWhenThenPointer;
import io.resys.hdes.ast.spi.visitors.ast.FwParserAstNodeVisitor.FwRedundentTasks;

public class FlowTreePointerParser {
  @Value.Immutable
  public interface FwRedundentOrderedTasks {
    Optional<FlowTaskNode> getFirst();

    List<FlowTaskNode> getUnclaimed();
  }

  private final Map<String, FlowTaskNode> createdTasks = new HashMap<>();
  private Map<String, FlowTaskNode> sourceTasks;
  
  public FwRedundentOrderedTasks visit(FwRedundentTasks redundentTasks) {
    List<FlowTaskNode> tasks = redundentTasks.getValues();
    if (tasks.isEmpty()) {
      return ImmutableFwRedundentOrderedTasks.builder().build();
    }
    sourceTasks = redundentTasks.getValues().stream()
        .collect(Collectors.toMap(e -> e.getId(), e -> e));
    FlowTaskNode first = visit(tasks.get(0));
    
    List<FlowTaskNode> unclaimed = sourceTasks.values().stream()
    .filter(src -> !createdTasks.containsKey(src.getId()))
    .collect(Collectors.toList());
    
    return ImmutableFwRedundentOrderedTasks.builder().first(first).unclaimed(unclaimed).build();
  }

  private FlowTaskNode visit(FlowTaskNode task) {
    if (createdTasks.containsKey(task.getId())) {
      return createdTasks.get(task.getId());
    }

    FlowTaskPointer next = visit(task.getNext());
    Optional<FlowLoop> loop = task.getLoop()
        .map(l -> ImmutableFlowLoop.builder().from(l).next(visit(l.getNext())).build()); 
    
    FlowTaskNode clone = ImmutableFlowTaskNode.builder()
        .from(task)
        .next(next)
        .loop(loop)
        .build();
    
    createdTasks.put(clone.getId(), clone);
    return clone;
  }

  private FlowTaskPointer visit(FlowTaskPointer pointer) {
    if(pointer instanceof WhenThenPointer) {
      return visit((WhenThenPointer) pointer);
    } else if(pointer instanceof ThenPointer) {
      return visit((ThenPointer) pointer);
    } else if(pointer instanceof EndPointer) {
      return pointer;
    }
    // TODO : error handling
    throw new AstNodeException("Unknown pointer: " + pointer + "!");
  }
  
  private FlowTaskPointer visit(WhenThenPointer pointer) {
    List<WhenThen> values = new ArrayList<>();    
    for(WhenThen src : pointer.getValues()) {
      WhenThen result = visit(src);
      values.add(result);
    }
    
    return ImmutableWhenThenPointer.builder()
        .from(pointer)
        .values(values)
        .build();
  }

  private WhenThen visit(WhenThen pointer) {
    FlowTaskPointer then = visit(pointer.getThen());
    return ImmutableWhenThen.builder().from(pointer).then(then).build();
  }
  
  private FlowTaskPointer visit(ThenPointer pointer) {
    String taskName = pointer.getName();
    
    if(createdTasks.containsKey(taskName)) {
      return ImmutableThenPointer.builder().from(pointer).task(createdTasks.get(taskName)).build();
    } else if(sourceTasks.containsKey(taskName)) {
      FlowTaskNode src = sourceTasks.get(taskName);
      FlowTaskNode result = visit(src);
      return ImmutableThenPointer.builder().from(pointer).task(result).build();
    } else if(taskName.equalsIgnoreCase("end")) {
      return pointer;
    }
    
    StringBuilder message = new StringBuilder()
        .append("Unrecognized task reference: ")
        .append("'").append(taskName).append("'")
        .append(" used in then!");
    
    if(!sourceTasks.values().isEmpty()) {
      message.append(System.lineSeparator()).append("Expecting one of: ");
    }
    for(FlowTaskNode task : sourceTasks.values()) {
      message.append(System.lineSeparator()).append("  - ").append(task.getId()).append(" (line: ").append(task.getToken().getLine()).append(")");
    }
    throw new AstNodeException(Arrays.asList(ImmutableErrorNode.builder()
        .message(message.toString())
        .target(pointer)
        .build()));
  }
}
