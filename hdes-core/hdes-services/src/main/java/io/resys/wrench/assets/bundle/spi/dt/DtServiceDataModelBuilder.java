package io.resys.wrench.assets.bundle.spi.dt;

import java.util.ArrayList;

/*-
 * #%L
 * wrench-component-assets
 * %%
 * Copyright (C) 2016 - 2017 Copyright 2016 ReSys OÜ
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

import java.util.Collections;
import java.util.List;

import io.resys.hdes.client.api.ast.TypeDef;
import io.resys.hdes.client.api.programs.DecisionProgram;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceDataModel;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceError;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceStatus;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceType;
import io.resys.wrench.assets.bundle.spi.beans.ImmutableServiceDataModel;

public class DtServiceDataModelBuilder {

  public ServiceDataModel build(String id, DecisionProgram dt) {
    List<TypeDef> params = new ArrayList<>();
    params.addAll(dt.getAst().getHeaders().getAcceptDefs());
    params.addAll(dt.getAst().getHeaders().getReturnDefs());
    
    List<ServiceError> errors = new ArrayList<>();

    return new ImmutableServiceDataModel(
        id, dt.getId(), dt.getAst().getDescription(),
        ServiceType.DT,
        dt.getClass(),
        errors.isEmpty() ? ServiceStatus.OK : ServiceStatus.ERROR,
        Collections.emptyList(),
        Collections.unmodifiableList(params),
        Collections.emptyList());
  }
}
