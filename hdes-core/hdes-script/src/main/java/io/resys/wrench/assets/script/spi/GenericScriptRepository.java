package io.resys.wrench.assets.script.spi;

import com.fasterxml.jackson.databind.ObjectMapper;

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

import io.resys.hdes.client.api.HdesAstTypes;
import io.resys.wrench.assets.script.api.ScriptRepository;
import io.resys.wrench.assets.script.spi.builders.GenericScriptBuilder;

public class GenericScriptRepository implements ScriptRepository {
  private final HdesAstTypes dataTypeRepository;
  private final ObjectMapper objectMapper;

  public GenericScriptRepository( 
      HdesAstTypes dataTypeRepository, ObjectMapper objectMapper) {
    super();
    this.dataTypeRepository = dataTypeRepository;
    this.objectMapper = objectMapper;
  }

  @Override
  public ScriptBuilder createBuilder() {
    return new GenericScriptBuilder(dataTypeRepository, objectMapper);
  }
}
