package io.resys.wrench.assets.script.spi;

/*-
 * #%L
 * wrench-component-script
 * %%
 * Copyright (C) 2016 - 2018 Copyright 2016 ReSys OÜ
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

import java.util.List;
import java.util.stream.Collectors;

import io.resys.hdes.client.api.ast.AstDataType;
import io.resys.hdes.client.api.ast.ServiceAstType;
import io.resys.hdes.client.api.execution.Service;
import io.resys.wrench.assets.script.api.ServiceException;

public class ServiceTemplate implements Service {
  private final ServiceAstType model;
  private final Class<?> beanType;
  private final List<AstDataType> inputs;
  private boolean created;
  @SuppressWarnings("rawtypes")
  private ServiceExecutorType0 type0;
  @SuppressWarnings("rawtypes")
  private ServiceExecutorType1 type1;
  @SuppressWarnings("rawtypes")
  private ServiceExecutorType2 type2;
  
  public ServiceTemplate(
      ServiceAstType model, Class<?> beanType) {
    this.beanType = beanType;
    this.model = model;
    this.inputs = model.getHeaders().getInputs().stream()
        .sorted((p1, p2) -> Integer.compare(p1.getOrder(), p2.getOrder()))
        .collect(Collectors.toList());
  }
  
  private Object getInput(List<Object> context, int index) {
    Class<?> clazz = inputs.get(0).getBeanType();
    for (Object fact : context) {
      if (clazz.isAssignableFrom(fact.getClass())) {
        return fact;
      }
    }
    return null;
  }
  @Override
  public ServiceAstType getModel() {
    return model;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public Object execute(List<Object> context, ServiceInit init) {
  
    Object input1 = getInput(context, 0);
    Object input2 = getInput(context, 1);
    
    
    if(!created) {
      try {
        Object executable = init.get(this.beanType);
        if(executable instanceof ServiceExecutorType0) {
          type0 = (ServiceExecutorType0) executable; 
        } else if(executable instanceof ServiceExecutorType1) {
          type1 = (ServiceExecutorType1) executable;      
        } else if(executable instanceof ServiceExecutorType2) {
          type2 = (ServiceExecutorType2) executable;
        } else {
          throw new ServiceException(model, "Can't find/call execute method!");  
        }
      
      } catch(ServiceException e) {
        throw e;
      } catch(Exception e) {
        throw new ServiceException(model, "Can't find/call execute method!" + e.getMessage(), e);
      }
    }
    
    if(type0 != null) {
      return type0.execute(); 
    }
    
    if(input1 == null) {
      throw new ServiceException(model, "Can't find/call execute method with null input1!");
    }
  
    if(type1 != null) {
      return type1.execute(input1);
    }
    
    if(input2 == null) {
      throw new ServiceException(model, "Can't find/call execute method with null input2!");
    }
    return type2.execute(input1, input2);
  }

  @Override
  public void stop() {

  }
}
