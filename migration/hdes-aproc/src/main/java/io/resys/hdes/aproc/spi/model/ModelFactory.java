package io.resys.hdes.aproc.spi.model;

/*-
 * #%L
 * hdes-aproc
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.resys.hdes.datatype.api.DataType;
import io.resys.hdes.datatype.api.DataTypeService;
import io.resys.hdes.datatype.spi.GenericDataTypeService;
import io.resys.hdes.decisiontable.api.DecisionTableService;
import io.resys.hdes.decisiontable.spi.headers.GenericHeaderFactory;
import io.resys.hdes.decisiontable.spi.headers.HeaderFactory;
import io.resys.hdes.decisiontable.spi.model.GenericDecisionTableModelBuilder;
import io.resys.hdes.flow.api.FlowModel;
import io.resys.hdes.flow.api.FlowService;
import io.resys.hdes.flow.api.ImmutableInputType;
import io.resys.hdes.flow.spi.ast.GenericFlowAstBuilder;
import io.resys.hdes.flow.spi.model.GenericFlowModelBuilder;
import io.resys.hdes.flow.spi.model.beans.FlowModelRootBean;
import io.resys.hdes.servicetask.api.ServiceTaskService;
import io.resys.hdes.servicetask.spi.model.GenericServiceTaskModelBuilder;
import io.resys.hdes.servicetask.spi.model.ServiceTaskFactory;
import io.resys.hdes.servicetask.spi.model.groovy.GenericServiceTaskFactory;

public class ModelFactory {
  
  private final Supplier<DecisionTableService.ModelBuilder> dt;
  private final Supplier<ServiceTaskService.ModelBuilder> servicetask;
  private final Supplier<FlowService.ModelBuilder> flow;
  private final Supplier<FlowService.AstBuilder> flowAst;

  public ModelFactory(
      Supplier<DecisionTableService.ModelBuilder> dt,
      Supplier<ServiceTaskService.ModelBuilder> servicetask,
      Supplier<FlowService.ModelBuilder> flow,
      Supplier<FlowService.AstBuilder> flowAst) {
    super();
    this.dt = dt;
    this.servicetask = servicetask;
    this.flow = flow;
    this.flowAst = flowAst;
  }

  public DecisionTableService.ModelBuilder dt() {
    return dt.get();
  }
  
  public FlowService.ModelBuilder flow() {
    return flow.get();
  }

  public FlowService.AstBuilder flowAst() {
    return flowAst.get();
  }
  
  public ServiceTaskService.ModelBuilder st() {
    return servicetask.get();
  }

  public static Config config() {
    return new Config();
  }

  public static class Config {
    private Optional<DataTypeService> dataTypeService = Optional.empty();
    private Optional<HeaderFactory> headerFactory = Optional.empty();
    private Optional<ServiceTaskFactory> serviceTaskFactory = Optional.empty();
    private Optional<ObjectMapper> yamlMapper = Optional.empty();

    public Config serviceTask(ServiceTaskFactory serviceTaskFactory) {
      this.serviceTaskFactory = Optional.of(serviceTaskFactory);
      return this;
    }
    public Config dataType(DataTypeService dataTypeService) {
      this.dataTypeService = Optional.of(dataTypeService);
      return this;
    }
    public Config headerFactory(HeaderFactory headerFactory) {
      this.headerFactory = Optional.of(headerFactory);
      return this;
    }
    public Config yamlMapper(ObjectMapper yamlMapper) {
      this.yamlMapper = Optional.of(yamlMapper);
      return this;
    }
    public ModelFactory build() {
      DataTypeService dataTypeService = this.dataTypeService.orElseGet(() -> GenericDataTypeService.config().build());
      HeaderFactory headerFactory = this.headerFactory.orElseGet(() -> GenericHeaderFactory.config().build());
      ServiceTaskFactory serviceTaskFactory = this.serviceTaskFactory.orElseGet(() -> new GenericServiceTaskFactory());
      ObjectMapper yamlMapper = this.yamlMapper.orElseGet(() -> new ObjectMapper(new YAMLFactory()));
      Collection<FlowModel.InputType> inputTypes = Collections.unmodifiableList(Arrays.asList(
          DataType.ValueType.ARRAY,
          DataType.ValueType.STRING,
          DataType.ValueType.BOOLEAN,
          DataType.ValueType.INTEGER,
          DataType.ValueType.LONG,
          DataType.ValueType.DECIMAL,
          DataType.ValueType.DATE,
          DataType.ValueType.DATE_TIME).stream()
          .map(v -> ImmutableInputType.builder().name(v.name()).value(v.name()).build())
          .collect(Collectors.toList()));
      
      return new ModelFactory(
          () -> new GenericDecisionTableModelBuilder(dataTypeService, headerFactory),
          () -> new GenericServiceTaskModelBuilder(serviceTaskFactory),
          () -> new GenericFlowModelBuilder(yamlMapper, new FlowModelRootBean(inputTypes)),
          () -> new GenericFlowAstBuilder(dataTypeService));
    }
  }
}
