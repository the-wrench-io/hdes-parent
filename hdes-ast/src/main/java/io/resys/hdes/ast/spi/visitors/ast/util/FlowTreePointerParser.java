package io.resys.hdes.ast.spi.visitors.ast.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.immutables.value.Value;

import io.resys.hdes.ast.api.AstNodeException;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowTask;
import io.resys.hdes.ast.api.nodes.FlowNode.FlowTaskPointer;
import io.resys.hdes.ast.api.nodes.FlowNode.ThenPointer;
import io.resys.hdes.ast.api.nodes.FlowNode.WhenThen;
import io.resys.hdes.ast.api.nodes.FlowNode.WhenThenPointer;
import io.resys.hdes.ast.api.nodes.ImmutableFlowTask;
import io.resys.hdes.ast.api.nodes.ImmutableThenPointer;
import io.resys.hdes.ast.api.nodes.ImmutableWhenThen;
import io.resys.hdes.ast.api.nodes.ImmutableWhenThenPointer;
import io.resys.hdes.ast.spi.visitors.ast.FwParserAstNodeVisitor.FwRedundentTasks;

public class FlowTreePointerParser {
  @Value.Immutable
  public interface FwRedundentOrderedTasks {
    Optional<FlowTask> getFirst();

    List<FlowTask> getUnclaimed();
  }

  private final Map<String, FlowTask> createdTasks = new HashMap<>();
  private Map<String, FlowTask> sourceTasks;

  public FwRedundentOrderedTasks visit(FwRedundentTasks redundentTasks) {
    List<FlowTask> tasks = redundentTasks.getValues();
    if (tasks.isEmpty()) {
      return ImmutableFwRedundentOrderedTasks.builder().build();
    }
    sourceTasks = redundentTasks.getValues().stream()
        .collect(Collectors.toMap(e -> e.getId(), e -> e));
    FlowTask first = visit(tasks.get(0));
    
    List<FlowTask> unclaimed = sourceTasks.values().stream()
    .filter(src -> createdTasks.containsKey(src.getId()))
    .collect(Collectors.toList());
    
    return ImmutableFwRedundentOrderedTasks.builder().first(first).unclaimed(unclaimed).build();
  }

  private FlowTask visit(FlowTask task) {
    if (createdTasks.containsKey(task.getId())) {
      return createdTasks.get(task.getId());
    }
    Optional<FlowTaskPointer> next = task.getNext();
    if (!next.isPresent()) {
      createdTasks.put(task.getId(), task);
      return task;
    }
    next = visit(next.get());
    FlowTask clone = ImmutableFlowTask.builder().from(task).next(next).build();
    createdTasks.put(clone.getId(), clone);
    return clone;
  }

  private Optional<FlowTaskPointer> visit(FlowTaskPointer pointer) {
    if(pointer instanceof WhenThenPointer) {
      return visit((WhenThenPointer) pointer);
    } else if(pointer instanceof ThenPointer) {
      return visit((ThenPointer) pointer);
    }
    throw new AstNodeException("Unknown pointer: " + pointer + "!");
  }

  private Optional<FlowTaskPointer> visit(WhenThenPointer pointer) {
    List<WhenThen> values = new ArrayList<>();    
    for(WhenThen src : pointer.getValues()) {
      Optional<WhenThen> result = visit(src);
      if(result.isPresent()) {
        values.add(result.get());
      }
    }
    if(values.isEmpty()) {
      return Optional.empty();
    }
    
    return Optional.of(ImmutableWhenThenPointer.builder()
        .from(pointer)
        .values(values)
        .build());
  }

  private Optional<WhenThen> visit(WhenThen pointer) {
    Optional<FlowTaskPointer> then = visit(pointer.getThen());
    if(!then.isPresent()) {
      return Optional.empty();
    }
    return Optional.of(ImmutableWhenThen.builder().from(pointer).then((ThenPointer) then.get()).build());
  }
  private Optional<FlowTaskPointer> visit(ThenPointer pointer) {
    String taskName = pointer.getName();
    if(createdTasks.containsKey(taskName)) {
      return Optional.of(ImmutableThenPointer.builder().from(pointer).task(createdTasks.get(taskName)).build());
    } else if(sourceTasks.containsKey(taskName)) {
      FlowTask src = sourceTasks.get(taskName);
      FlowTask result = visit(src);
      return Optional.of(ImmutableThenPointer.builder().from(pointer).task(result).build());
    }
    return Optional.empty();
  }  
//  Map<String, FlowTask> tasksById = new HashMap<>();
//  
//  redundentTasks.getValues().stream()
//  .collect(Collectors.toMap(e -> e.getId(), e -> e));
//  
//  FlowTask first = null;
//  for(FlowTask task : redundentTasks.getValues()) {
//    if(!task.getNext().isPresent()) {
//      continue;
//    }
//    FlowTaskPointer pointer = task.getNext().get();
//    
//    if(pointer instanceof ) {
//      
//    }
//    
//  }
//  
}
