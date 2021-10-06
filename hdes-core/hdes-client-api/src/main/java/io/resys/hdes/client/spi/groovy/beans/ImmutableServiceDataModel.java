package io.resys.hdes.client.spi.groovy.beans;

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

import io.resys.hdes.client.api.ast.AstType.Direction;
import io.resys.hdes.client.api.ast.ServiceAstType.ServiceDataModel;
import io.resys.hdes.client.api.ast.ServiceAstType.ServiceDataParamModel;

public class ImmutableServiceDataModel implements ServiceDataModel {
  private static final long serialVersionUID = 8451288360934228389L;

  private final int id;
  private final String name;
  private final boolean returnType;
  private final List<ServiceDataParamModel> params;

  public ImmutableServiceDataModel(int id, String name, List<ServiceDataParamModel> params) {
    super();
    this.id = id;
    this.name = name;
    this.params = params;
    this.returnType = params.stream().filter(p -> p.getType().getDirection() == Direction.OUT).findFirst().isPresent();
  }
  @Override
  public int getOrder() {
    return id;
  }
  @Override
  public String getName() {
    return name;
  }
  @Override
  public List<ServiceDataParamModel> getParameters() {
    return params;
  }
  @Override
  public boolean isReturnType() {
    return returnType;
  }
}
