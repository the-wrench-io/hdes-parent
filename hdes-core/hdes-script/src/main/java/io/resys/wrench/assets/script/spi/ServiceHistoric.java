package io.resys.wrench.assets.script.spi;

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

import java.util.List;

import io.resys.hdes.client.api.ast.ServiceAstType;
import io.resys.hdes.client.api.execution.Service;


public class ServiceHistoric implements Service {
  private final ServiceAstType model;
  
  public ServiceHistoric(ServiceAstType model) {
    this.model = model;
  }
  @Override
  public ServiceAstType getModel() {
    return model;
  }

  @Override
  public Object execute(List<Object> context, ServiceInit init) {    
    return null;
  }

  @Override
  public void stop() {
  }
}
