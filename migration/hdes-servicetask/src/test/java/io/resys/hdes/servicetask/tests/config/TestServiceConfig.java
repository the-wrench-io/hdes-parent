package io.resys.hdes.servicetask.tests.config;

/*-
 * #%L
 * hdes-servicetask
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
import io.resys.hdes.servicetask.api.ServiceTaskCommandType;
import io.resys.hdes.servicetask.api.ServiceTaskModel;
import io.resys.hdes.servicetask.api.ServiceTaskService;
import io.resys.hdes.servicetask.spi.GenericServiceTaskService;

public class TestServiceConfig {
  
  public static final DataTypeService dataTypeService = GenericDataTypeService.config().build();
  public static final ServiceTaskService serviceTaskService = GenericServiceTaskService.config().dataType(dataTypeService).build(); 

  
  public static Builder builder() {
    return new Builder();
  }
  
  public static class Builder {
    private final List<DataTypeCommand> commands = new ArrayList<>();
    private int inputIndex = 1;
    private int outputIndex = 1;
    private int bodyIndex = 1;
    
    public Builder name(String name) {
      commands.add(ImmutableDataTypeCommand.builder()
          .type(ServiceTaskCommandType.SET_NAME.toString()).value(name)
          .build());
      return this;
    }
    
    public Builder input(String value) {
      commands.add(ImmutableDataTypeCommand.builder()
          .id(inputIndex++)
          .type(ServiceTaskCommandType.ADD.toString())
          .subType(ServiceTaskCommandType.SubType.INPUT.toString()).value(value)
          .build());
      return this;
    }
    
    public Builder output(String value) {
      commands.add(ImmutableDataTypeCommand.builder()
          .id(outputIndex++)
          .type(ServiceTaskCommandType.ADD.toString())
          .subType(ServiceTaskCommandType.SubType.OUTPUT.toString()).value(value)
          .build());
      return this;
    }

    public Builder body(String value) {
      commands.add(ImmutableDataTypeCommand.builder()
          .id(bodyIndex++)
          .type(ServiceTaskCommandType.ADD.toString())
          .subType(ServiceTaskCommandType.SubType.BODY.toString()).value(value)
          .build());
      return this;
    }
    
    public ServiceTaskModel build(Class<?> ctx) {
      return serviceTaskService.model().context(ctx).src(commands).build();
    }
  }
}
