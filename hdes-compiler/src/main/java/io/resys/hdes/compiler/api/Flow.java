package io.resys.hdes.compiler.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.immutables.value.Value;

import io.resys.hdes.compiler.api.Flow.FlowInput;
import io.resys.hdes.compiler.api.Flow.FlowState;

public interface Flow<S extends FlowState, I extends FlowInput> {
  
  enum ExecutionStatusType { COMPLETED, PENDING, ERROR }
  interface FlowInput {}
  interface FlowTaskInput {}
  interface FlowTaskOutput {}
  
  S apply(I input);
  S apply(S old, FlowTaskOutput output);
  
  interface FlowState {
    ExecutionStatusType getType();
    List<FlowError> getErrors();
    FlowExecutionLog getLog();
    String getHead();
  }
  
  interface FlowTaskState<I extends FlowTaskInput, R extends FlowTaskOutput> {
    String getId();
    Optional<R> getOutput();
    I getInput();
  }
  
  @Value.Immutable
  interface FlowExecutionLog {
    String getId();
    Optional<String> getParent();
    Optional<Long> getDuration();
    LocalDateTime getStart();
    Optional<LocalDateTime> getEnd();
  }

  @Value.Immutable
  interface FlowError {
    String getId();
    String getTrace();
    String getValue();
  }
  
  @FunctionalInterface
  interface FlowTask<S extends FlowState, I extends FlowTaskInput, R extends FlowTaskOutput> {
    R apply(S state, I input);
  }
}
