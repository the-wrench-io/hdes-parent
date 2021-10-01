package io.resys.wrench.assets.dt.spi.beans;

import java.util.List;

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

import java.util.Map;

import io.resys.hdes.client.api.execution.DecisionTableResult.DecisionContext;
import io.resys.hdes.client.api.execution.DecisionTableResult.DecisionTableDecision;
import io.resys.hdes.client.api.execution.DecisionTableResult.DecisionTableExpression;
import io.resys.hdes.client.api.model.DecisionTable.DecisionTableNode;

public class ImmutableDecisionTableDecision implements DecisionTableDecision {

  private static final long serialVersionUID = 3977249182024873157L;

  private final List<DecisionContext> context;
  private final DecisionTableNode node;
  private final Map<String, DecisionTableExpression> expressions;
  private final boolean match;

  public ImmutableDecisionTableDecision(List<DecisionContext> data, DecisionTableNode node, boolean match, Map<String, DecisionTableExpression> expressions) {
    super();
    this.context = data;
    this.node = node;
    this.match = match;
    this.expressions = expressions;
  }

  @Override
  public boolean isMatch() {
    return match;
  }

  @Override
  public List<DecisionContext> getContext() {
    return context;
  }
  @Override
  public Map<String, DecisionTableExpression> getExpressions() {
    return expressions;
  }
  @Override
  public DecisionTableNode getNode() {
    return node;
  }
}
