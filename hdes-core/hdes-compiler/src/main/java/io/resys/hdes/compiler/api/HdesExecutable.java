package io.resys.hdes.compiler.api;

import java.io.Serializable;

import org.immutables.value.Value;

public interface HdesExecutable<I extends HdesExecutable.Input, M extends HdesExecutable.Meta, V extends HdesExecutable.OutputValue> {
  
  // Core
  enum SourceType { FL, MT, DT, ST }
  interface Meta extends Serializable {}
  interface OutputValue extends Serializable {}
  interface Input extends Serializable {}

  @Value.Immutable
  interface MetaToken {
    String getValue();
    MetaStamp getStart();
    MetaStamp getEnd();
  }
  
  @Value.Immutable
  interface MetaStamp {
    //long getTime();
    int getLine();
    int getColumn();
  }
  
  // Generic output to encapsulate function output value with metadata associated with it
  @Value.Immutable
  interface Output<M extends Meta, V extends OutputValue> extends Serializable {
    M getMeta();
    V getValue();
  }
  
  // Single command style method
  Output<M, V> apply(I input);
  
  // Source from what executable was created
  SourceType getSourceType();
  
  // Markers for sub types
  interface DecisionTable<I extends Input, V extends OutputValue> extends HdesExecutable<I, DecisionTableMeta, V> {}
  interface Flow<I extends Input, M extends Meta, V extends OutputValue> extends HdesExecutable<I, M, V> {}
  interface ManualTask<I extends Input, M extends Meta, V extends OutputValue> extends HdesExecutable<I, M, V> {}
  interface ServiceTask<I extends Input, M extends Meta, V extends OutputValue> extends HdesExecutable<I, M, V> {}

}
