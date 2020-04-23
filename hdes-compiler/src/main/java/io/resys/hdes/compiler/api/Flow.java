package io.resys.hdes.compiler.api;

import java.util.List;
import java.util.Optional;

import org.immutables.value.Value;

import io.resys.hdes.compiler.api.Flow.FlowInput;
import io.resys.hdes.compiler.api.Flow.FlowOutput;
import io.resys.hdes.compiler.api.Flow.FlowState;

public interface Flow<
  I extends FlowInput,
  O extends FlowOutput,
  S extends FlowState<I, O>> {
  
  enum ExecutionStatusType { COMPLETED, RUNNING, PENDING, ERROR }
  interface FlowInput {}
  interface FlowOutput {}
  
  S apply(I input);
  //S apply(S old, ? output);
  
  interface FlowState<I, O> {
    String getId();
    I getInput();
    Optional<O> getOutput();
    ExecutionStatusType getType();
    List<FlowError> getErrors();
    FlowExecutionLog getLog();
  }
  
  interface FlowTaskState<I, R> {
    String getId();
    Optional<R> getOutput();
    I getInput();
  }

  @Value.Immutable
  interface FlowExecutionLog {
    String getId();
    Long getStart();
    Optional<FlowExecutionLog> getParent();
    Optional<Long> getDuration();
    Optional<Long> getEnd();
  }

  @Value.Immutable
  interface FlowError {
    String getId();
    String getTrace();
    String getValue();
  }
  
  @FunctionalInterface
  interface FlowTask<S extends FlowState<?, ?>, I, R> {
    R apply(S state, I input);
  }
}
