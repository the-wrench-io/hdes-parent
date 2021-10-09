package io.resys.wrench.assets.dt.spi.export;

import io.resys.hdes.client.api.ast.AstDecision;
import io.resys.hdes.client.api.execution.DecisionProgram;

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

import io.resys.wrench.assets.dt.api.DecisionTableRepository.DecisionTableExporter;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.DecisionTableFormat;

public abstract class TemplateDecisionTableExporter implements DecisionTableExporter {
  protected AstDecision dt;
  protected DecisionTableFormat format;

  @Override
  public DecisionTableExporter src(AstDecision dt) {
    this.dt = dt;
    return this;
  }

  @Override
  public DecisionTableExporter format(DecisionTableFormat format) {
    this.format = format;
    return this;
  }
}
