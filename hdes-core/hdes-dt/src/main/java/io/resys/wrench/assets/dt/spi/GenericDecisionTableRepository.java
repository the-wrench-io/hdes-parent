package io.resys.wrench.assets.dt.spi;

/*-
 * #%L
 * hdes-dt
 * %%
 * Copyright (C) 2020 - 2021 Copyright 2020 ReSys OÜ
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

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.client.api.HdesAstTypes;
import io.resys.hdes.client.api.execution.DecisionTableResult.NodeExpressionExecutor;
import io.resys.hdes.client.spi.HdesAstTypesImpl;
import io.resys.wrench.assets.dt.api.DecisionTableRepository;
import io.resys.wrench.assets.dt.spi.builders.CommandDecisionTableBuilder;
import io.resys.wrench.assets.dt.spi.builders.GenericDecisionTableExecutor;
import io.resys.wrench.assets.dt.spi.export.DelegateDecisionTableExporter;

public class GenericDecisionTableRepository implements DecisionTableRepository {

  private final ObjectMapper objectMapper;
  private final NodeExpressionExecutor expressionExecutor;
  private final HdesAstTypes ast;
  public GenericDecisionTableRepository(
      ObjectMapper objectMapper,
      HdesAstTypes dataTypeRepository,
      NodeExpressionExecutor expressionExecutor) {
    super();
    this.objectMapper = objectMapper;
    this.expressionExecutor = expressionExecutor;
    this.ast = new HdesAstTypesImpl(objectMapper);
  }

  @Override
  public DecisionTableBuilder createBuilder() {
    return new CommandDecisionTableBuilder(objectMapper, () -> ast);
  }
  @Override
  public DecisionTableExecutor createExecutor() {
    return new GenericDecisionTableExecutor(expressionExecutor);
  }
  @Override
  public DecisionTableExporter createExporter() {
    return new DelegateDecisionTableExporter(objectMapper);
  }
}
