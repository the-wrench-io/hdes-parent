package io.resys.wrench.assets.script.spi.beans;

/*-
 * #%L
 * wrench-assets-script
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

import io.resys.wrench.assets.datatype.api.DataTypeRepository.DataType;
import io.resys.wrench.assets.script.api.ScriptRepository.ScriptParameterContextType;
import io.resys.wrench.assets.script.api.ScriptRepository.ScriptParameterModel;

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
