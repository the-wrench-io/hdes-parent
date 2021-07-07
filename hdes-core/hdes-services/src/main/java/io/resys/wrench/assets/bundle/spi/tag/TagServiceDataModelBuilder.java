package io.resys.wrench.assets.bundle.spi.tag;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceAssociation;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceDataModel;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceError;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceStatus;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceType;
import io.resys.wrench.assets.bundle.spi.beans.ImmutableServiceDataModel;
import io.resys.wrench.assets.datatype.api.DataTypeRepository.DataType;

public class TagServiceDataModelBuilder {

  public ServiceDataModel build(String id, String name) {
    List<DataType> params = Collections.emptyList();
    List<ServiceError> errors = new ArrayList<>();
    List<ServiceAssociation> assocs = new ArrayList<>();

    return new ImmutableServiceDataModel(
        id, name, "",
        ServiceType.TAG,
        null,
        errors.isEmpty() ? ServiceStatus.OK : ServiceStatus.ERROR,
            Collections.unmodifiableList(errors),
            Collections.unmodifiableList(params),
            Collections.unmodifiableList(assocs));
  }
}
