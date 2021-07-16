package io.resys.wrench.assets.dt.spi.beans;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;

/*-
 * #%L
 * wrench-component-dt
 * %%
 * Copyright (C) 2016 - 2017 Copyright 2016 ReSys OÜ
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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.resys.wrench.assets.dt.api.model.DecisionTable.DecisionTableNodeOutput;
import io.resys.wrench.assets.dt.api.model.DecisionTableResult;

public class ImmutableDecisionTableResult implements DecisionTableResult {

  private static final long serialVersionUID = -2615335425678830306L;

  private final List<DecisionTableDecision> decisions;

  public ImmutableDecisionTableResult(List<DecisionTableDecision> decisions) {
    super();
    this.decisions = decisions;
  }

  @Override
  public List<DecisionTableDecision> getRejections() {
    return decisions.stream().filter(d -> !d.isMatch()).collect(Collectors.toList());
  }

  @Override
  public List<DecisionTableDecision> getMatches() {
    return decisions.stream().filter(d -> d.isMatch()).collect(Collectors.toList());
  }

  @Override
  public List<DecisionTableOutput> getOutputs() {
    return getMatches().stream()
        .map(m -> new ImmutableDecisionTableOutput(m.getNode().getId(), m.getNode().getOrder(), Collections.unmodifiableMap(toValues(m)), m.getExpressions()))
        .collect(Collectors.toList());
  }

  protected Map<String, Serializable> toValues(DecisionTableDecision decision) {
    Map<String, Serializable> result = new HashMap<>();
    for(DecisionTableNodeOutput entry : decision.getNode().getOutputs()) {
      result.put(entry.getKey().getName(), entry.getValue());
    }
    return result;
  }
}
