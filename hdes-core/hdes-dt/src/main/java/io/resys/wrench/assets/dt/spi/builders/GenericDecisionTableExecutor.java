package io.resys.wrench.assets.dt.spi.builders;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import io.resys.wrench.assets.datatype.api.DataTypeRepository.DataType;
import io.resys.wrench.assets.datatype.spi.util.Assert;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.DecisionTableExecutor;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.DecisionTableExpression;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.DecisionTableFixedValue;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.HitPolicyExecutor;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.NodeExpressionExecutor;
import io.resys.wrench.assets.dt.api.model.DecisionTable;
import io.resys.wrench.assets.dt.api.model.DecisionTable.DecisionTableNode;
import io.resys.wrench.assets.dt.api.model.DecisionTable.DecisionTableNodeInput;
import io.resys.wrench.assets.dt.api.model.DecisionTableResult;
import io.resys.wrench.assets.dt.api.model.DecisionTableResult.DecisionContext;
import io.resys.wrench.assets.dt.api.model.DecisionTableResult.DecisionTableDecision;
import io.resys.wrench.assets.dt.spi.beans.ImmutableDecisionContext;
import io.resys.wrench.assets.dt.spi.beans.ImmutableDecisionTableDecision;
import io.resys.wrench.assets.dt.spi.beans.ImmutableDecisionTableResult;
import io.resys.wrench.assets.dt.spi.hitpolicy.DelegateHitPolicyExecutor;

public class GenericDecisionTableExecutor implements DecisionTableExecutor {

  private final NodeExpressionExecutor expressionExecutor;
  private DecisionTable decisionTable;
  private Function<DataType, Object> context;

  public GenericDecisionTableExecutor(NodeExpressionExecutor expressionExecutor) {
    super();
    this.expressionExecutor = expressionExecutor;
  }

  @Override
  public DecisionTableExecutor decisionTable(DecisionTable decisionTable) {
    this.decisionTable = decisionTable;
    return this;
  }

  @Override
  public DecisionTableExecutor context(Function<DataType, Object> context) {
    this.context = context;
    return this;
  }

  @Override
  public DecisionTableResult execute() {
    Assert.notNull(decisionTable, () -> "decisionTable can't be null!");
    Assert.notNull(context, () -> "context can't be null!");

    List<DecisionTableDecision> decisions = new ArrayList<>();
    DecisionTableNode node = decisionTable.getNode();
    HitPolicyExecutor hitPolicy = new DelegateHitPolicyExecutor(decisionTable);
    while(node != null) {
      DecisionTableDecision decision = execute(node);
      decisions.add(decision);
      if(!hitPolicy.execute(decision)) {
        break;
      }
      node = node.getNext();
    }

    return new ImmutableDecisionTableResult(Collections.unmodifiableList(decisions));
  }

  protected DecisionTableDecision execute(DecisionTableNode node) {
    Boolean match = null;
    
    List<DecisionContext> data = new ArrayList<>();
    
    Map<String, DecisionTableExpression> expressions = new HashMap<>();
    for(DecisionTableNodeInput input : node.getInputs()) {
      Object contextEntity = this.context.apply(input.getKey());
      if(DecisionTableFixedValue.ALWAYS_TRUE == contextEntity) {
        DecisionTableExpression expression = expressionExecutor.getExpression(input.getValue(), input.getKey().getValueType());
        expressions.put(input.getKey().getName(), expression);
        
        if(!Boolean.FALSE.equals(match)) {
          match = true;
        }
        continue;
      }
      
      Object entity = input.getKey().toValue(contextEntity);
      data.add(new ImmutableDecisionContext(input.getKey(), entity));

      if(Boolean.FALSE.equals(match)) {
        continue;
      }
      match = expressionExecutor.execute(input.getValue(), input.getKey().getValueType(), entity);
      DecisionTableExpression expression = expressionExecutor.getExpression(input.getValue(), input.getKey().getValueType());
      expressions.put(input.getKey().getName(), expression);
      
    }
    return new ImmutableDecisionTableDecision(data, node, node.getInputs().isEmpty() || Boolean.TRUE.equals(match), Collections.unmodifiableMap(expressions));
  }
}