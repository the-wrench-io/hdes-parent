package io.resys.hdes.ast.api;

/*-
 * #%L
 * hdes-ast
 * %%
 * Copyright (C) 2020 Copyright 2020 ReSys OÜ
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

import io.resys.hdes.ast.api.nodes.AstNode;
import io.resys.hdes.ast.api.nodes.DecisionTableNode;
import io.resys.hdes.ast.api.nodes.ExpressionNode;
import io.resys.hdes.ast.api.nodes.FlowNode;
import io.resys.hdes.ast.api.nodes.ManualTaskNode;

public interface AstFactory {
  AstBuilder<FlowNode> flow();
  AstBuilder<ExpressionNode> expression();
  AstBuilder<ManualTaskNode> manualTask();
  AstBuilder<DecisionTableNode> decisionTable();

  interface AstBuilder<T extends AstNode> {
    AstBuilder<T> src(String src);
    T build();
  }
}
