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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import io.resys.hdes.client.api.ast.TypeDef;
import io.resys.hdes.client.api.execution.DecisionProgram;
import io.resys.hdes.client.api.execution.DecisionProgram.Row;
import io.resys.hdes.client.api.execution.DecisionProgram.RowAccepts;
import io.resys.hdes.client.api.execution.DecisionResult;
import io.resys.hdes.client.api.execution.DecisionResult.DecisionContext;
import io.resys.hdes.client.api.execution.DecisionResult.DecisionExpression;
import io.resys.hdes.client.api.execution.DecisionResult.HitPolicyExecutor;
import io.resys.hdes.client.api.execution.DecisionResult.NodeExpressionExecutor;
import io.resys.hdes.client.api.execution.ExpressionProgram;
import io.resys.hdes.client.spi.util.HdesAssert;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.DecisionTableExecutor;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.DecisionTableFixedValue;
import io.resys.wrench.assets.dt.spi.beans.ImmutableDecisionContext;
import io.resys.wrench.assets.dt.spi.beans.ImmutableDecisionTableDecision;
import io.resys.wrench.assets.dt.spi.beans.ImmutableDecisionTableResult;
import io.resys.wrench.assets.dt.spi.hitpolicy.DelegateHitPolicyExecutor;

public class GenericDecisionTableExecutor implements DecisionTableExecutor {

  private final NodeExpressionExecutor expressionExecutor;
  private DecisionProgram decisionTable;
  private Function<TypeDef, Object> context;

  public GenericDecisionTableExecutor(NodeExpressionExecutor expressionExecutor) {
    super();
    this.expressionExecutor = expressionExecutor;
  }

  @Override
  public DecisionTableExecutor decisionTable(DecisionProgram decisionTable) {
    this.decisionTable = decisionTable;
    return this;
  }

  @Override
  public DecisionTableExecutor context(Function<TypeDef, Object> context) {
    this.context = context;
    return this;
  }

  @Override
  public DecisionResult execute() {
    HdesAssert.notNull(decisionTable, () -> "decisionTable can't be null!");
    HdesAssert.notNull(context, () -> "context can't be null!");

    List<DecisionExpression> decisions = new ArrayList<>();
    Iterator<Row> it = decisionTable.getRows().iterator();
    HitPolicyExecutor hitPolicy = new DelegateHitPolicyExecutor(decisionTable);
    while(it.hasNext()) {
      final var node = it.next();
      DecisionExpression decision = execute(node);
      decisions.add(decision);
      if(!hitPolicy.execute(decision)) {
        break;
      }
    }

    return new ImmutableDecisionTableResult(Collections.unmodifiableList(decisions));
  }

  protected DecisionExpression execute(Row node) {
    Boolean match = null;
    
    List<DecisionContext> data = new ArrayList<>();
    
    Map<String, String> expressions = new HashMap<>();
    for(RowAccepts input : node.getAccepts()) {
      Object contextEntity = this.context.apply(input.getKey());
      if(DecisionTableFixedValue.ALWAYS_TRUE == contextEntity) {
        ExpressionProgram expression = input.getExpression();
        expressions.put(input.getKey().getName(), expression.getSrc());
        
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
      match = Boolean.TRUE.equals(input.getExpression().run(entity).getValue());
      expressions.put(input.getKey().getName(), input.getExpression().getSrc());
      
    }
    return new ImmutableDecisionTableDecision(data, node, node.getAccepts().isEmpty() || Boolean.TRUE.equals(match), Collections.unmodifiableMap(expressions));
  }
}
