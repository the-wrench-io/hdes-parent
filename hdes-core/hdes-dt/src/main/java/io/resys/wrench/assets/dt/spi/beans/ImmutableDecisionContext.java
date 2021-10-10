package io.resys.wrench.assets.dt.spi.beans;

import io.resys.hdes.client.api.ast.TypeDef;
import io.resys.hdes.client.api.programs.DecisionResult.DecisionContext;

public class ImmutableDecisionContext implements DecisionContext {

  private final TypeDef key;
  private final Object value;
  public ImmutableDecisionContext(TypeDef key, Object value) {
    super();
    this.key = key;
    this.value = value;
  }
  @Override
  public TypeDef getKey() {
    return key;
  }
  @Override
  public Object getValue() {
    return value;
  }
}
