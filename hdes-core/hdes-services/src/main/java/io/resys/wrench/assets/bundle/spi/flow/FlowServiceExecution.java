package io.resys.wrench.assets.bundle.spi.flow;

import java.io.Serializable;

/*-
 * #%L
 * wrench-component-assets-flow
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
import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import io.resys.hdes.client.api.HdesClient;
import io.resys.hdes.client.api.programs.FlowProgram;
import io.resys.hdes.client.api.programs.FlowProgram.FlowResult;
import io.resys.hdes.client.api.programs.Program;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceExecution;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceResponse;
import io.resys.wrench.assets.bundle.spi.exceptions.AssetErrorCodes;

public class FlowServiceExecution implements ServiceExecution {

  private static final Logger LOGGER = LoggerFactory.getLogger(FlowServiceExecution.class);

  private final FlowProgram flowModel;
  private final HdesClient flowRepository;
  private final List<Serializable> facts = new ArrayList<>();

  public FlowServiceExecution(
      FlowProgram flowModel,
      HdesClient flowRepository) {
    super();
    this.flowModel = flowModel;
    this.flowRepository = flowRepository;
  }

  @Override
  public ServiceExecution insert(Serializable bean) {
    Assert.notNull(bean, "can't insert null!");
    facts.add(bean);
    return this;
  }

  @Override
  public ServiceResponse run() {
    try {
      final var flowBuilder = flowRepository.executor();

      for(Serializable fact : facts) {
        if(fact instanceof Map) {
          flowBuilder.inputMap((Map<String, Serializable>) fact);
        }
      }
      FlowResult flow = flowBuilder.flow(flowModel).andGetBody();
      return new FlowServiceResponse(flow);
    } catch(RuntimeException e) {
      throw e;
    } catch(Exception e) {
      LOGGER.error(e.getMessage(), e);
      throw AssetErrorCodes.FLOW_START_ERROR.newException(flowModel.getId(), e.getMessage());
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> void run(Consumer<T> serviceType) {
    serviceType.accept((T) flowModel);
  }

  @Override
  public <T extends Program<?>> T unwrap() {
    return (T) flowModel;
  }

}
