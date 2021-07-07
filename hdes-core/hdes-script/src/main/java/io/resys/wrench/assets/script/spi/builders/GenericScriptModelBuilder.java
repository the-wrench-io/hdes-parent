package io.resys.wrench.assets.script.spi.builders;

/*-
 * #%L
 * wrench-assets-script
 * %%
 * Copyright (C) 2016 - 2019 Copyright 2016 ReSys OÜ
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

import io.resys.wrench.assets.datatype.spi.util.Assert;
import io.resys.wrench.assets.script.api.ScriptRepository.ScriptCommand;
import io.resys.wrench.assets.script.api.ScriptRepository.ScriptMethodModel;
import io.resys.wrench.assets.script.api.ScriptRepository.ScriptModel;
import io.resys.wrench.assets.script.api.ScriptRepository.ScriptModelBuilder;
import io.resys.wrench.assets.script.spi.beans.ImmutableScriptModel;

public class GenericScriptModelBuilder implements ScriptModelBuilder {

  private String src;
  private int rev;
  private List<ScriptCommand> commands;
  private Class<?> type;
  private ScriptMethodModel method;

  @Override
  public ScriptModelBuilder src(String src) {
    this.src = src;
    return this;
  }
  @Override
  public ScriptModelBuilder commands(List<ScriptCommand> commands) {
    this.commands = commands;
    return this;
  }
  @Override
  public ScriptModelBuilder type(Class<?> type) {
    this.type = type;
    return this;
  }
  @Override
  public ScriptModelBuilder method(ScriptMethodModel method) {
    this.method = method;
    return this;
  }
  @Override
  public ScriptModelBuilder rev(int rev) {
    this.rev = rev;
    return this;
  }
  @Override
  public ScriptModel build() {
    Assert.isTrue(src != null, () -> "src can't be null!");
    Assert.isTrue(type != null, () -> "type can't be null!");
    Assert.isTrue(method != null, () -> "method can't be null!");

    return new ImmutableScriptModel(type.getSimpleName(), rev, src, commands, type, method);
  }
}
