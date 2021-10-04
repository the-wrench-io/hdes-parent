package io.resys.wrench.assets.dt.api;

/*-
 * #%L
 * wrench-component-assets-Dt
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

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;

import io.resys.hdes.client.api.ast.AstType.AstCommandType;
import io.resys.hdes.client.api.ast.AstType.ValueType;
import io.resys.hdes.client.api.ast.DecisionAstType;
import io.resys.hdes.client.api.execution.DecisionTableResult;
import io.resys.hdes.client.api.execution.DecisionTableResult.DecisionTableDecision;
import io.resys.hdes.client.api.execution.DecisionTableResult.DecisionTableExpression;
import io.resys.hdes.client.api.model.DataType;
import io.resys.hdes.client.api.model.DecisionTableModel;

public interface DecisionTableRepository {

  DecisionTableBuilder createBuilder();
  DecisionTableExecutor createExecutor();
  DecisionTableExporter createExporter();
  DecisionTableExpressionBuilder createExpression();
  DecisionTableCommandModelBuilder createCommandModelBuilder();

  interface DecisionTableExpressionBuilder {
    DecisionTableExpressionBuilder src(String src);
    DecisionTableExpressionBuilder valueType(ValueType valueType);
    DecisionTableExpression build();
  }

  interface DecisionTableCommandModelBuilder {
    DecisionTableCommandModelBuilder src(List<AstCommandType> src);
    DecisionTableCommandModelBuilder src(JsonNode src);
    DecisionTableCommandModelBuilder rev(Integer version);
    DecisionAstType build();
  }

  interface DecisionTableExporter {
    DecisionTableExporter src(DecisionTableModel dt);
    DecisionTableExporter format(DecisionTableFormat format);
    String build();
  }

  interface DecisionTableExecutor {
    DecisionTableExecutor decisionTable(DecisionTableModel decisionTable);
    DecisionTableExecutor context(Function<DataType, Object> context);
    DecisionTableResult execute();
  }

  interface DecisionTableBuilder {
    DecisionTableBuilder format(DecisionTableFormat format);

    DecisionTableBuilder rename(Optional<String> name);
    DecisionTableBuilder src(String input);
    DecisionTableBuilder src(InputStream inputStream);
    DecisionTableBuilder src(JsonNode src);
    DecisionTableModel build();
  }

  interface NodeExpressionExecutor {
    DecisionTableExpression getExpression(String src, ValueType type);
    boolean execute(String expression, ValueType type, Object entity);
  }



  interface DynamicValueExpressionExecutor {
    Object parseVariable(String expression, ValueType type);
    String execute(String expression, Map<String, Object> context);
  }

  interface HitPolicyExecutor {
    boolean execute(DecisionTableDecision decision);
  }

  enum DecisionTableFormat {
    JSON,
    CSV
  }
  
  enum DecisionTableFixedValue {
    ALWAYS_TRUE
  }
}
