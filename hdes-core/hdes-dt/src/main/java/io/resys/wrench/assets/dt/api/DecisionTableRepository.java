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
import java.util.Optional;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;

import io.resys.hdes.client.api.ast.TypeDef;
import io.resys.hdes.client.api.execution.DecisionProgram;
import io.resys.hdes.client.api.execution.DecisionResult;

public interface DecisionTableRepository {

  DecisionTableBuilder createBuilder();
  DecisionTableExecutor createExecutor();
  DecisionTableExporter createExporter();


  interface DecisionTableExporter {
    DecisionTableExporter src(DecisionProgram dt);
    DecisionTableExporter format(DecisionTableFormat format);
    String build();
  }

  interface DecisionTableExecutor {
    DecisionTableExecutor decisionTable(DecisionProgram decisionTable);
    DecisionTableExecutor context(Function<TypeDef, Object> context);
    DecisionResult execute();
  }

  interface DecisionTableBuilder {
    DecisionTableBuilder format(DecisionTableFormat format);

    DecisionTableBuilder rename(Optional<String> name);
    DecisionTableBuilder src(String input);
    DecisionTableBuilder src(InputStream inputStream);
    DecisionTableBuilder src(JsonNode src);
    DecisionProgram build();
  }

  enum DecisionTableFormat {
    JSON,
    CSV
  }
  
  enum DecisionTableFixedValue {
    ALWAYS_TRUE
  }
}
