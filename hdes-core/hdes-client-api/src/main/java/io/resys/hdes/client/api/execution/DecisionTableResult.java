package io.resys.hdes.client.api.execution;

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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import io.resys.hdes.client.api.ast.AstDataType;
import io.resys.hdes.client.api.ast.AstType.ValueType;
import io.resys.hdes.client.api.model.DecisionTableModel.DecisionTableNode;

public interface DecisionTableResult extends Serializable {
  List<DecisionTableDecision> getRejections();
  List<DecisionTableDecision> getMatches();
  List<DecisionTableOutput> getOutputs();

  interface DecisionTableDecision extends Serializable {
    List<DecisionContext> getContext();
    DecisionTableNode getNode();
    boolean isMatch();
    Map<String, Expression> getExpressions();
  }
  
  interface DecisionContext {
    AstDataType getKey();
    Object getValue();
  }

  interface DecisionTableOutput extends Serializable {
    int getId();
    int getOrder();
    Map<String, Expression> getExpressions();
    Map<String, Serializable> getValues();
  }

  interface Expression {
    String getSrc();
    ValueType getType();
    List<String> getConstants();
    Object getValue(Object entity);
  }
  
  
  interface NodeExpressionExecutor {
    Expression getExpression(String src, ValueType type);
    boolean execute(String expression, ValueType type, Object entity);
  }
  interface DynamicValueExpressionExecutor {
    Object parseVariable(String expression, ValueType type);
    String execute(String expression, Map<String, Object> context);
  }
  interface HitPolicyExecutor {
    boolean execute(DecisionTableDecision decision);
  }
}
