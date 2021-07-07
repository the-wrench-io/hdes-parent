package io.resys.wrench.assets.bundle.spi.flowtask;

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
import java.util.stream.Collectors;

import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceAssociation;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceDataModel;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceError;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceStatus;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceStore;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceType;
import io.resys.wrench.assets.bundle.spi.beans.ImmutableServiceDataModel;
import io.resys.wrench.assets.datatype.api.DataTypeRepository.DataType;
import io.resys.wrench.assets.script.api.ScriptRepository.Script;
import io.resys.wrench.assets.script.api.ScriptRepository.ScriptModel;
import io.resys.wrench.assets.script.api.ScriptRepository.ScriptParameterContextType;
import io.resys.wrench.assets.script.api.ScriptRepository.ScriptParameterModel;

public class FlowTaskServiceDataModelBuilder {

  public ServiceDataModel build(String id, String name, Script script, ServiceStore serviceStore) {
    List<ServiceAssociation> associations = new ArrayList<>();
    List<ServiceError> errors = new ArrayList<>();
    ScriptModel model = script.getModel();

    List<DataType> params = getScriptParameterModels(model);

    return new ImmutableServiceDataModel(
        id, name, null,
        ServiceType.FLOW_TASK,
        script.getModel().getType(),
        errors.isEmpty() ? ServiceStatus.OK : ServiceStatus.ERROR,
            Collections.unmodifiableList(errors),
            Collections.unmodifiableList(params),
            Collections.unmodifiableList(associations));
  }

  protected List<DataType> getScriptParameterModels(ScriptModel model) {
    List<ScriptParameterModel> externals = model.getMethod().getParameters().stream()
        .filter(p -> p.getContextType() == ScriptParameterContextType.EXTERNAL)
        .collect(Collectors.toList());

    // drop dummy layer
    List<DataType> result = new ArrayList<>();
    externals.forEach(e -> result.addAll(e.getType().getProperties()));
    return result;
  }
}
