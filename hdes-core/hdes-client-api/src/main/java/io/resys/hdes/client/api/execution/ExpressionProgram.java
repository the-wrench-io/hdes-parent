package io.resys.hdes.client.api.execution;

import java.util.List;

import io.resys.hdes.client.api.ast.TypeDef.ValueType;

public interface ExpressionProgram {
  ValueType getType();
  List<String> getConstants();
  ExpressionResult run(Object context);
}
