package io.resys.wrench.assets.script.api;

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

import java.io.InputStream;

import com.fasterxml.jackson.databind.JsonNode;

import io.resys.hdes.client.api.execution.ServiceProgram;

public interface ScriptRepository {

  ScriptBuilder createBuilder();

  interface ScriptBuilder {
    ScriptBuilder src(InputStream src);
    ScriptBuilder src(JsonNode src);
    ScriptBuilder src(String src);
    ScriptBuilder rev(Integer rev);
    ServiceProgram build();
  }

  interface ScriptContext {
    <T> T get(Class<T> type);
  }
}
