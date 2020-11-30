package io.resys.hdes.backend.spi.storage;

/*-
 * #%L
 * hdes-ui-backend
 * %%
 * Copyright (C) 2020 Copyright 2020 ReSys OÜ
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

import io.resys.hdes.backend.api.HdesBackend.DefType;
import io.resys.hdes.backend.spi.util.Assert;
import io.resys.hdes.backend.spi.util.Assert.HdesUIBackendApiExeption;

public class HdesResourceBuilder {
  
  private String name;
  private DefType type;
  
  public HdesResourceBuilder type(DefType type) {
    this.type = type;
    return this;
  }
  
  public HdesResourceBuilder name(String name) {
    this.name = name;
    return this;
  }
  
  public String build() {
    Assert.notEmpty(name, () -> "name can't be null!");
    Assert.notNull(type, () -> "type can't be null!");
    
    // TODO::
    switch (type) {
    case DT: return new StringBuilder()
      .append("define decision-table: ").append(name).append("\n")
      .append("headers: {\n")
      .append("  name STRING required IN,\n")
      .append("  lastName STRING required IN,\n") 
      .append("  type INTEGER required IN,\n")
      .append("  value INTEGER required OUT \n") 
      .append("} ALL: {\n")
      .append("  { ?, ?, between 1 and 30, 20 },\n")
      .append("  { not 'bob' or 'sam', 'woman', ?, 4570 }\n") 
      .append("}")
      .toString();
    
    case FL: return new StringBuilder()
      .append("define flow: ").append(name).append(" description: 'descriptive'\n")
      .append("headers: {\n")
      .append("  id INTEGER optional IN,\n") 
      .append("  externalId INTEGER required IN,\n") 
      .append("  elements ARRAY of OBJECT required IN: {\n") 
      .append("    value STRING required IN\n")
      .append("  },\n") 
      .append("  output ARRAY of OBJECT required OUT: {\n") 
      .append("    value STRING required OUT\n")
      .append("  }\n")
      .append("}\n")
      .append("tasks: {\n") 
      .append("  FirstTask: {\n") 
      .append("    then: HandleElement\n") 
      .append("    decision-table: bestDtTask uses: {\n") 
      .append("      name: elements.value\n")
      .append("    }\n")
      .append("  } from elements then: EndTask,\n") 
      .append("  \n")
      .append("  HandleElement: {\n") 
      .append("    then: end as: { output: { value: FirstTask.dtOutput }}\n") 
      .append("  },\n")
      .append("  \n")
      .append("  EndTask: {\n") 
      .append("    then: end as: { input1: arg1.x1, input2: arg2.x1 }\n") 
      .append("  }\n")
      .append("}").toString();
    default: throw new HdesUIBackendApiExeption("Resource builder for type: " + type + " is not implemented!");
    }
  }
  
  public static HdesResourceBuilder builder() {
    return new HdesResourceBuilder();
  }
}
