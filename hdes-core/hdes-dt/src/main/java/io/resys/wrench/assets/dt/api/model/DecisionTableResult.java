package io.resys.wrench.assets.dt.api.model;

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

import io.resys.wrench.assets.datatype.api.DataTypeRepository.DataType;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.DecisionTableExpression;
import io.resys.wrench.assets.dt.api.model.DecisionTable.DecisionTableNode;

public interface DecisionTableResult extends Serializable {
  List<DecisionTableDecision> getRejections();
  List<DecisionTableDecision> getMatches();
  List<DecisionTableOutput> getOutputs();

  interface DecisionTableDecision extends Serializable {
    List<DecisionContext> getContext();
    DecisionTableNode getNode();
    boolean isMatch();
    Map<String, DecisionTableExpression> getExpressions();
  }
  
  interface DecisionContext {
    DataType getKey();
    Object getValue();
  }

  interface DecisionTableOutput extends Serializable {
    int getId();
    int getOrder();
    Map<String, DecisionTableExpression> getExpressions();
    Map<String, Serializable> getValues();
  }
}
