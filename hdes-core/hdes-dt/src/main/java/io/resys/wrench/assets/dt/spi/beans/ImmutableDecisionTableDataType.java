package io.resys.wrench.assets.dt.spi.beans;

import io.resys.hdes.client.api.ast.AstDataType;
import io.resys.hdes.client.api.model.DecisionTableModel.DecisionTableDataType;

public class ImmutableDecisionTableDataType implements DecisionTableDataType {

  private static final long serialVersionUID = 5025265615138518092L;

  private final int order;
  private final String script;
  private final AstDataType value;

  public ImmutableDecisionTableDataType(int order, String script, AstDataType value) {
    super();
    this.order = order;
    this.script = script;
    this.value = value;
  }

  @Override
  public int getOrder() {
    return order;
  }

  @Override
  public String getScript() {
    return script;
  }

  @Override
  public AstDataType getValue() {
    return value;
  }

  @Override
  public int compareTo(DecisionTableDataType o) {
    return Integer.compare(order, o.getOrder());
  }
}
