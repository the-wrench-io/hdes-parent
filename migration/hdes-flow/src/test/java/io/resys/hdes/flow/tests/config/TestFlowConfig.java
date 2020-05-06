package io.resys.hdes.flow.tests.config;

/*-
 * #%L
 * hdes-flow
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

import java.util.ArrayList;
import java.util.List;

import io.resys.hdes.datatype.api.DataTypeCommand;
import io.resys.hdes.datatype.api.DataTypeService;
import io.resys.hdes.datatype.api.ImmutableDataTypeCommand;
import io.resys.hdes.datatype.spi.GenericDataTypeService;
import io.resys.hdes.flow.api.FlowAst;
import io.resys.hdes.flow.api.FlowCommandType;
import io.resys.hdes.flow.api.FlowModel;
import io.resys.hdes.flow.api.FlowService;
import io.resys.hdes.flow.spi.GenericFlowService;

public class TestFlowConfig {
  public static final DataTypeService DATA_TYPE_SERVICE = GenericDataTypeService.config().build();
  public static final FlowService FLOW_SERVICE = GenericFlowService.config().dataType(DATA_TYPE_SERVICE).build();

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private List<DataTypeCommand> commands = new ArrayList<>();

    public Builder add(DataTypeCommand command) {
      this.commands.add(command);
      return this;
    }

    public Builder add(List<DataTypeCommand> commands) {
      this.commands.addAll(commands);
      return this;
    }

    public Builder add(int id, String line) {
      this.commands.add(ImmutableDataTypeCommand.builder()
        .id(id)
        .type(FlowCommandType.ADD.toString())
        .value(line).build());
      return this;
    }
    public Builder add(String line) {
      this.commands.add(ImmutableDataTypeCommand.builder()
          .id(commands.size() + 1)
          .type(FlowCommandType.ADD.toString())
          .value(line).build());
      return this;
    }
    
    public Builder delete(int line) {
      this.commands.add(ImmutableDataTypeCommand.builder()
          .id(line)
          .type(FlowCommandType.DELETE.toString())
          .build());
      return this;
    }
    
    public FlowModel.Root model() {
      return FLOW_SERVICE.model().src(commands).build();
    }
    public FlowAst ast() {
      FlowAst flowAst = FLOW_SERVICE.ast().from(model()).build();
      return flowAst;
    }
  }
}
