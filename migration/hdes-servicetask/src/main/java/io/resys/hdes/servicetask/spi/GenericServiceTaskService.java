package io.resys.hdes.servicetask.spi;

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

import java.util.Optional;

import io.resys.hdes.datatype.api.DataTypeService;
import io.resys.hdes.datatype.spi.GenericDataTypeService;
import io.resys.hdes.servicetask.api.ServiceTaskService;
import io.resys.hdes.servicetask.spi.ast.GenericServiceTaskAstBuilder;
import io.resys.hdes.servicetask.spi.execution.GenericServiceTaskExecutionBuilder;
import io.resys.hdes.servicetask.spi.model.GenericServiceTaskModelBuilder;
import io.resys.hdes.servicetask.spi.model.ServiceTaskFactory;
import io.resys.hdes.servicetask.spi.model.groovy.GenericServiceTaskFactory;

public class GenericServiceTaskService implements ServiceTaskService {

  private final DataTypeService dataTypeService;
  private final ServiceTaskFactory serviceTaskFactory;
  
  public GenericServiceTaskService(ServiceTaskFactory serviceTaskFactory, DataTypeService dataTypeService) {
    super();
    this.serviceTaskFactory = serviceTaskFactory;
    this.dataTypeService = dataTypeService;
  }

  @Override
  public ModelBuilder model() {
    return new GenericServiceTaskModelBuilder(serviceTaskFactory);
  }

  @Override
  public AstBuilder ast() {
    return new GenericServiceTaskAstBuilder(dataTypeService);
  }

  @Override
  public ExecutionBuilder execution() {
    return new GenericServiceTaskExecutionBuilder(dataTypeService);
  }
  
  public static Config config() {
    return new Config();
  }
  
  public static class Config {
    
    private Optional<ServiceTaskFactory> serviceTaskFactory = Optional.empty();
    private Optional<DataTypeService> dataTypeService = Optional.empty();
    
    public Config serviceTask(ServiceTaskFactory serviceTaskFactory) {
      this.serviceTaskFactory = Optional.of(serviceTaskFactory);
      return this;
    }
    public Config dataType(DataTypeService dataType) {
      this.dataTypeService = Optional.of(dataType);
      return this;
    }
    public GenericServiceTaskService build() {
      ServiceTaskFactory serviceTaskFactory = this.serviceTaskFactory.orElseGet(() -> new GenericServiceTaskFactory());
      DataTypeService dataTypeService = this.dataTypeService.orElseGet(() -> GenericDataTypeService.config().build());
      return new GenericServiceTaskService(serviceTaskFactory, dataTypeService);
    }
  }
}
