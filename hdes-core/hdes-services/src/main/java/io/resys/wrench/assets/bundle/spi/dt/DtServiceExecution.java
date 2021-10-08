package io.resys.wrench.assets.bundle.spi.dt;

import java.io.Serializable;

/*-
 * #%L
 * wrench-component-assets-dmn
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
import java.util.List;
import java.util.function.Consumer;

import org.springframework.util.Assert;

import io.resys.hdes.client.api.execution.DecisionProgram;
import io.resys.hdes.client.api.execution.DecisionResult;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceExecution;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceResponse;
import io.resys.wrench.assets.dt.api.DecisionTableRepository;

public class DtServiceExecution implements ServiceExecution {

  private final DecisionTableRepository decisionTableRepository;
  private final DecisionProgram decisionTable;
  private final List<Object> inputs = new ArrayList<>();
  private DtInputResolver dtInputResolver;

  public DtServiceExecution(DecisionTableRepository decisionTableRepository, DecisionProgram decisionTable) {
    super();
    this.decisionTableRepository = decisionTableRepository;
    this.decisionTable = decisionTable;
  }

  @Override
  public ServiceExecution insert(Serializable bean) {
    if(bean == null) {
      return this;
    }
    if(bean instanceof DtInputResolver) {
      Assert.isNull(dtInputResolver, "dtInputResolver can be inserted only once!");
      dtInputResolver = (DtInputResolver) bean;
    } else {
      inputs.add(bean);
    }
    return this;
  }

  @Override
  public ServiceResponse run() {
    Assert.notNull(dtInputResolver, "dtInputResolver must be inserted!");

    // Custom resolver
    DecisionResult result = decisionTableRepository.createExecutor().decisionTable(decisionTable).context(dtInputResolver).execute();
    return new DtServiceResponse(result);

  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> void run(Consumer<T> serviceType) {
    serviceType.accept((T) decisionTable);
  }
}
