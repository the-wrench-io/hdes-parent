package io.resys.hdes.client.spi;

/*-
 * #%L
 * hdes-client-api
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

import org.codehaus.groovy.control.CompilerConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import groovy.lang.GroovyClassLoader;
import io.resys.hdes.client.api.HdesAstTypes;
import io.resys.hdes.client.spi.decision.DecisionAstBuilderImpl;
import io.resys.hdes.client.spi.flow.FlowAstBuilderImpl;
import io.resys.hdes.client.spi.groovy.ServiceAstBuilderImpl;
import io.resys.hdes.client.spi.groovy.ServiceExecutorCompilationCustomizer;

public class HdesAstTypesImpl implements HdesAstTypes {

  private final ObjectMapper objectMapper;
  private final ObjectMapper yaml = new ObjectMapper(new YAMLFactory());
  private final GroovyClassLoader gcl;
  private final HdesDataTypeFactory dataType;
  
  public HdesAstTypesImpl(ObjectMapper objectMapper) {
    super();
    this.objectMapper = objectMapper;
    
    CompilerConfiguration config = new CompilerConfiguration();
    config.setTargetBytecode(CompilerConfiguration.JDK8);
    config.addCompilationCustomizers(new ServiceExecutorCompilationCustomizer());
    
    this.gcl = new GroovyClassLoader(Thread.currentThread().getContextClassLoader(), config);
    this.dataType = new HdesDataTypeFactory(objectMapper);
  }

  @Override
  public DecisionAstBuilder decision() {
    return new DecisionAstBuilderImpl(objectMapper);
  }
  @Override
  public FlowAstBuilder flow() {
    return new FlowAstBuilderImpl(yaml);
  }
  @Override
  public ServiceAstBuilder service() {
    return new ServiceAstBuilderImpl(dataType, gcl);
  }
  @Override
  public DataTypeAstBuilder dataType() {
    return dataType.create();
  }
}
