package io.resys.wrench.assets.dt.spi.hitpolicy;

import io.resys.hdes.client.api.ast.DecisionAstType.HitPolicy;
import io.resys.hdes.client.api.execution.DecisionTableResult.DecisionTableDecision;
import io.resys.hdes.client.api.execution.DecisionTableResult.HitPolicyExecutor;
import io.resys.hdes.client.api.model.DecisionTableModel;
import io.resys.wrench.assets.dt.spi.exceptions.DecisionTableException;

public class DelegateHitPolicyExecutor implements HitPolicyExecutor {

  private HitPolicyExecutor delgate;

  public DelegateHitPolicyExecutor(DecisionTableModel decisionTable) {
    HitPolicy hitPolicy = decisionTable.getHitPolicy() == null ? HitPolicy.ALL : decisionTable.getHitPolicy();

    switch(hitPolicy) {
    case FIRST:
      // match only the first
      delgate = (decision) -> !decision.isMatch();
      break;
    case ALL:
      // match only the first
      delgate = (decision) -> true;
      break;
    default: throw new DecisionTableException("Unknown hit policy: " + hitPolicy + "!");
    }
  }

  @Override
  public boolean execute(DecisionTableDecision decision) {
    return delgate.execute(decision);
  }
}
