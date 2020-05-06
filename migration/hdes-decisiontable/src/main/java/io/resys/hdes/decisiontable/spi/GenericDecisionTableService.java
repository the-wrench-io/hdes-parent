package io.resys.hdes.decisiontable.spi;

/*-
 * #%L
 * hdes-decisiontable
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
import io.resys.hdes.decisiontable.api.DecisionTableService;
import io.resys.hdes.decisiontable.spi.ast.DelegateDecisionTableAstBuilder;
import io.resys.hdes.decisiontable.spi.executor.GenericDecisionTableExecutionBuilder;
import io.resys.hdes.decisiontable.spi.exporters.DelegateDecisionTableExporter;
import io.resys.hdes.decisiontable.spi.headers.GenericHeaderFactory;
import io.resys.hdes.decisiontable.spi.headers.HeaderFactory;
import io.resys.hdes.decisiontable.spi.model.GenericDecisionTableModelBuilder;

public class GenericDecisionTableService implements DecisionTableService {

  private final DataTypeService dataTypeService;
  private final HeaderFactory headerFactory;

  public GenericDecisionTableService(DataTypeService dataTypeService,
                                     HeaderFactory headerFactory) {
    this.dataTypeService = dataTypeService;
    this.headerFactory = headerFactory;
  }

  @Override
  public ExecutionBuilder execution() {
    return new GenericDecisionTableExecutionBuilder(dataTypeService);
  }

  @Override
  public ModelBuilder model() {
    return new GenericDecisionTableModelBuilder(dataTypeService, headerFactory);
  }

  @Override
  public AstBuilder ast() {
    return new DelegateDecisionTableAstBuilder(dataTypeService);
  }

  @Override
  public ExportBuilder export() {
    return new DelegateDecisionTableExporter(dataTypeService);
  }

  public static Config config() {
    return new Config();
  }
  
  public static class Config {
    
    private Optional<DataTypeService> dataTypeService = Optional.empty();
    private Optional<HeaderFactory> headerFactory = Optional.empty();
    
    public Config dataType(DataTypeService dataTypeService) {
      this.dataTypeService = Optional.of(dataTypeService);
      return this;
    }
    public Config headerFactory(HeaderFactory headerFactory) {
      this.headerFactory = Optional.of(headerFactory);
      return this;
    }
    public GenericDecisionTableService build() {
      DataTypeService dataTypeService = this.dataTypeService.orElseGet(() -> GenericDataTypeService.config().build());
      HeaderFactory headerFactory = this.headerFactory.orElseGet(() -> GenericHeaderFactory.config().build());
      return new GenericDecisionTableService(dataTypeService, headerFactory);
    }
  }
}
