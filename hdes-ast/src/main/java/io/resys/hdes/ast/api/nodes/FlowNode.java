package io.resys.hdes.ast.api.nodes;

import java.util.List;
import java.util.Optional;

import org.immutables.value.Value;

public interface FlowNode extends AstNode {

  enum RefTaskType { MANUAL_TASK, DECISION_TABLE, SERVICE_TASK }
  
  interface TaskBody extends FlowNode {}
  
  @Value.Immutable
  interface FlowBody extends FlowNode {
    String getId();
    String getDescription();
    List<Input> getInputs();
    Optional<Task> getTask();
  }
  
  @Value.Immutable
  interface Input extends FlowNode {
    Boolean getRequired();
    NodeDataType getDataType();
    String getName();
    String getDebugValue();
  }
  
  @Value.Immutable
  interface Task extends FlowNode {
    String getId();
    TaskBody getBody();
  }
  
  @Value.Immutable
  interface SwitchBody extends TaskBody {
    List<WhenThen> getValue();
  }
  
  @Value.Immutable
  interface WhenThen extends TaskBody {
    ExpressionNode getWhen();
    Then getThen();
  }
  
  @Value.Immutable
  interface RefTaskBody extends TaskBody {
    RefTaskType getType();
    String getRef();
    List<Mapping> getMapping();
    Then getThen();
  }
  
  @Value.Immutable
  interface Then extends FlowNode {
    Task getTask();
  }
  
  @Value.Immutable
  interface Mapping extends FlowNode {
    String getLeft();
    String getRight();
  }
}
