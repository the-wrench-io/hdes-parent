package io.resys.wrench.assets.script.spi.beans;

/*-
 * #%L
 * hdes-script
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

import io.resys.hdes.client.api.ast.ServiceAstType.ScriptParameterContextType;
import io.resys.hdes.client.api.ast.ServiceAstType.ScriptParameterModel;
import io.resys.hdes.client.api.model.DataType;

public class ImmutableScriptParameterModel implements ScriptParameterModel {

  private static final long serialVersionUID = -3494498278287692703L;

  private final ScriptParameterContextType contextType;
  private final DataType type;

  public ImmutableScriptParameterModel(DataType type, ScriptParameterContextType contextType) {
    super();
    this.type = type;
    this.contextType = contextType;
  }

  @Override
  public DataType getType() {
    return type;
  }
  @Override
  public ScriptParameterContextType getContextType() {
    return contextType;
  }
}
