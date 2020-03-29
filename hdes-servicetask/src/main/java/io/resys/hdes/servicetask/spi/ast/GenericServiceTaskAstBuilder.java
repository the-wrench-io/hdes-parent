package io.resys.hdes.servicetask.spi.ast;

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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import io.resys.hdes.datatype.api.DataType;
import io.resys.hdes.datatype.api.DataTypeService;
import io.resys.hdes.datatype.spi.Assert;
import io.resys.hdes.servicetask.api.ImmutableServiceTaskAst;
import io.resys.hdes.servicetask.api.ServiceTask;
import io.resys.hdes.servicetask.api.ServiceTaskAst;
import io.resys.hdes.servicetask.api.ServiceTaskModel;
import io.resys.hdes.servicetask.api.ServiceTaskService;
import io.resys.hdes.servicetask.api.ServiceTaskService.AstBuilder;

public class GenericServiceTaskAstBuilder implements ServiceTaskService.AstBuilder {

  private final DataTypeService dataTypeService;  
  private ServiceTaskModel model;
  
  public GenericServiceTaskAstBuilder(DataTypeService dataTypeService) {
    super();
    this.dataTypeService = dataTypeService;
  }
  
  @Override
  public AstBuilder from(ServiceTaskModel model) {
    this.model = model;
    return this;
  }

  @Override
  public ServiceTaskAst build() {
    Assert.notNull(model, () -> "model can't be null");
    
    ServiceTask<?, ?, ?> serviceTask;
    try {
      serviceTask = model.getType().newInstance();
    } catch(Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
    
    List<DataType> inputs = getDataTypes(serviceTask.getInputType(), DataType.Direction.IN);
    List<DataType> outputs = getDataTypes(serviceTask.getInputType(), DataType.Direction.OUT);
    return ImmutableServiceTaskAst.builder()
        .model(model)
        .type(serviceTask)
        .inputs(inputs)
        .outputs(outputs)
        .build();
  }

  private List<DataType> getDataTypes(Class<?> type, DataType.Direction direction) {
    List<DataType> result = new ArrayList<>();
    for(Field field : type.getDeclaredFields()) {
      int modifier = field.getModifiers();
      if( Modifier.isFinal(modifier) ||
          Modifier.isTransient(modifier) ||
          Modifier.isStatic(modifier) ||
          field.getName().startsWith("$") ||
          field.getName().startsWith("_")) {
        continue;
      }
      result.add(dataTypeService.model()
          .name(field.getName())
          .direction(direction)
          .beanType(field.getType())
          .build());
    }
    
    return result;
  }
}
