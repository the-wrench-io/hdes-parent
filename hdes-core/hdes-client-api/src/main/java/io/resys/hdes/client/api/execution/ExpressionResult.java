package io.resys.hdes.client.api.execution;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import io.resys.hdes.client.api.ast.TypeDef.ValueType;

@Value.Immutable
public interface ExpressionResult {
  ValueType getType();
  List<String> getConstants();
  
  @Nullable
  Object getValue();
}
