package io.resys.wrench.assets.dt.spi.hitpolicy;

/*-
 * #%L
 * wrench-assets-dt
 * %%
 * Copyright (C) 2016 - 2018 Copyright 2016 ReSys OÜ
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import io.resys.wrench.assets.dt.api.DecisionTableRepository.HitPolicyExecutor;
import io.resys.wrench.assets.dt.api.model.DecisionTable;
import io.resys.wrench.assets.dt.api.model.DecisionTable.HitPolicy;
import io.resys.wrench.assets.dt.api.model.DecisionTableResult.DecisionTableDecision;
import io.resys.wrench.assets.dt.spi.exceptions.DecisionTableException;

public class DelegateHitPolicyExecutor implements HitPolicyExecutor {

  private HitPolicyExecutor delgate;

  public DelegateHitPolicyExecutor(DecisionTable decisionTable) {
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
