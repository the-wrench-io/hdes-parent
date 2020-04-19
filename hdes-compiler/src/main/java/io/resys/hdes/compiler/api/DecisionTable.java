package io.resys.hdes.compiler.api;

import java.util.function.Function;

import io.resys.hdes.compiler.api.DecisionTable.DecisionTableInput;
import io.resys.hdes.compiler.api.DecisionTable.DecisionTableOutput;

public interface DecisionTable<I extends DecisionTableInput, O extends DecisionTableOutput> extends Function<I, O> {
  interface DecisionTableInput {}
  interface DecisionTableOutput {}
}
