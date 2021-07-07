package io.resys.wrench.assets.script.spi;

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
import io.resys.wrench.assets.script.api.ScriptRepository;
import io.resys.wrench.assets.script.api.ScriptRepository.ScriptMethodModel;
import io.resys.wrench.assets.script.api.ScriptRepository.ScriptModel;
import io.resys.wrench.assets.script.api.ScriptRepository.ScriptParameterModel;

public abstract class ScriptTemplate implements ScriptRepository.Script {
  private final ScriptModel model;

  public ScriptTemplate(ScriptModel model) {
    this.model = model;
  }

  public ScriptMethodModel getMethod(List<Object> facts) {
    if(isAssignableFrom(model.getMethod(), facts)) {
      return model.getMethod();
    }
    return null;
  }

  protected Object getArgument(Class<?> arg, List<Object> facts) {
    for(Object fact : facts) {
      if(arg.isAssignableFrom(fact.getClass())) {
        return fact;
      }
    }
    return null;
  }

  protected boolean isAssignableFrom(ScriptMethodModel method, List<Object> facts) {
    for(ScriptParameterModel arg : method.getParameters()) {
      if(arg.getType().getDirection() == Direction.OUT) {
        continue;
      }
      if(!isAssignableFrom(arg.getType().getBeanType(), facts)) {
        return false;
      }
    }
    return true;
  }

  protected boolean isAssignableFrom(Class<?> clazz, List<Object> facts) {
    for(Object fact : facts) {
      if(clazz.isAssignableFrom(fact.getClass())) {
        return true;
      }
    }
    return false;
  }
  @Override
  public ScriptModel getModel() {
    return model;
  }
}
