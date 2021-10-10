package io.resys.hdes.client.api.programs;

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

import io.resys.hdes.client.api.ast.TypeDef;
import io.resys.hdes.client.api.ast.TypeDef.ValueType;
import io.resys.hdes.client.api.programs.DecisionProgram.Row;

public interface DecisionResult extends Serializable {
  List<DecisionExpression> getRejections();
  List<DecisionExpression> getMatches();
  List<DecisionTableOutput> getOutputs();

  interface DecisionExpression extends Serializable {
    List<DecisionContext> getContext();
    Row getNode();
    boolean isMatch();
    Map<String, String> getExpressions();
  }
  
  interface DecisionContext {
    TypeDef getKey();
    Object getValue();
  }

  interface DecisionTableOutput extends Serializable {
    int getId();
    int getOrder();
    Map<String, String> getExpressions();
    Map<String, Serializable> getValues();
  }
  
  interface NodeExpressionExecutor {
    ExpressionProgram getExpression(String src, ValueType type);
    boolean execute(String expression, ValueType type, Object entity);
  }
  interface HitPolicyExecutor {
    boolean execute(DecisionExpression decision);
  }
}
