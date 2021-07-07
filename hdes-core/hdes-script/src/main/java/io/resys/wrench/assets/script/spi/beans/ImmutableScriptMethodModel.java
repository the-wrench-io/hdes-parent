package io.resys.wrench.assets.script.spi.beans;

/*-
 * #%L
 * wrench-component-script
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

import java.util.List;

import io.resys.wrench.assets.datatype.api.DataTypeRepository.Direction;
import io.resys.wrench.assets.script.api.ScriptRepository.ScriptMethodModel;
import io.resys.wrench.assets.script.api.ScriptRepository.ScriptParameterModel;

public class ImmutableScriptMethodModel implements ScriptMethodModel {
  private static final long serialVersionUID = 8451288360934228389L;

  private final String id;
  private final String name;
  private final boolean returnType;
  private final List<ScriptParameterModel> params;

  public ImmutableScriptMethodModel(String id, String name, List<ScriptParameterModel> params) {
    super();
    this.id = id;
    this.name = name;
    this.params = params;
    this.returnType = params.stream().filter(p -> p.getType().getDirection() == Direction.OUT).findFirst().isPresent();
  }
  @Override
  public String getId() {
    return id;
  }
  @Override
  public String getName() {
    return name;
  }
  @Override
  public List<ScriptParameterModel> getParameters() {
    return params;
  }
  @Override
  public boolean isReturnType() {
    return returnType;
  }
}
