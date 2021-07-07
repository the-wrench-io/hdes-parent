package io.resys.wrench.assets.script.api;

/*-
 * #%L
 * wrench-component-script
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
import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import io.resys.wrench.assets.datatype.api.AstType;
import io.resys.wrench.assets.datatype.api.DataTypeRepository.DataType;

public interface ScriptRepository {

  ScriptBuilder createBuilder();

  interface ScriptBuilder {
    ScriptBuilder src(InputStream src);
    ScriptBuilder src(JsonNode src);
    ScriptBuilder src(String src);
    ScriptBuilder rev(Integer rev);
    Script build();
  }

  interface ScriptModelBuilder {
    ScriptModelBuilder src(String src);
    ScriptModelBuilder commands(List<ScriptCommand> commands);
    ScriptModelBuilder rev(int rev);
    ScriptModelBuilder type(Class<?> type);
    ScriptModelBuilder method(ScriptMethodModel method);
    ScriptModel build();
  }

  interface ScriptCommand extends Serializable {
    int getId();
    String getValue();
    ScriptCommandType getType();
  }

  interface ScriptModel extends AstType, Serializable {
    String getSrc();
    List<ScriptCommand> getCommands();
    ScriptMethodModel getMethod();
    Class<?> getType();
  }


  interface ScriptMethodModel extends Serializable {
    String getId();
    String getName();
    boolean isReturnType();
    List<ScriptParameterModel> getParameters();
  }

  interface ScriptParameterModel extends Serializable {
    DataType getType();
    ScriptParameterContextType getContextType();
  }

  interface Script {
    ScriptModel getModel();
    Object execute(List<Object> context);
  }

  interface ScriptConstructor {
    <T> T get(Class<T> type);
  }
  
  interface ScriptContext {
    <T> T get(Class<T> type);
  }


  enum ScriptCommandType {
    SET, ADD, DELETE
  }

  enum ScriptParameterContextType {
    INTERNAL, EXTERNAL
  }
}
