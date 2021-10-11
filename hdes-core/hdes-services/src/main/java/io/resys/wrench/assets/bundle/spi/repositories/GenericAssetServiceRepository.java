package io.resys.wrench.assets.bundle.spi.repositories;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

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

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.hdes.client.api.HdesClient;
import io.resys.hdes.client.api.ast.TypeDef;
import io.resys.hdes.client.api.programs.DecisionProgram.DecisionLog;
import io.resys.hdes.client.api.programs.FlowResult;
import io.resys.hdes.client.api.programs.FlowResult.FlowTask;
import io.resys.hdes.client.spi.decision.DecisionProgramExecutor;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository;
import io.resys.wrench.assets.bundle.spi.builders.GenericServiceQuery;
import io.resys.wrench.assets.bundle.spi.dt.resolvers.MatchingDtInputResolver;
import io.resys.wrench.assets.bundle.spi.exceptions.DataException;
import io.resys.wrench.assets.bundle.spi.exceptions.Message;
import io.resys.wrench.assets.bundle.spi.hash.HashBuilder;
import io.resys.wrench.assets.bundle.spi.migration.GenericServiceExporter;
import io.resys.wrench.assets.flow.api.FlowRepository;

public class GenericAssetServiceRepository implements AssetServiceRepository {

  private final Map<ServiceType, Function<ServiceStore, ServiceBuilder>> builders;
  private final FlowRepository flowRepository;
  private final ServiceStore serviceStore;
  private final ObjectMapper objectMapper;
  private final HdesClient types;

  public GenericAssetServiceRepository(
      HdesClient types,
      ObjectMapper objectMapper,
      FlowRepository flowRepository,
      
      Map<ServiceType, Function<ServiceStore, ServiceBuilder>> builders,
      ServiceStore serviceStore) {
    
    super();
    this.types = types;
    this.objectMapper = objectMapper;
    this.flowRepository = flowRepository;
    
    this.builders = builders;
    this.serviceStore = serviceStore;
  }

  @Override
  public ServiceBuilder createBuilder(ServiceType type) {
    Assert.isTrue(builders.containsKey(type), "No asset service builder for type: " + type + "!");
    return builders.get(type).apply(serviceStore);
  }

  @Override
  public ServiceQuery createQuery() {
    return new GenericServiceQuery(serviceStore);
  }

  @Override
  public ServiceStore createStore() {
    return serviceStore;
  }
  @Override
  public String getHash() {
    HashBuilder hashBuilder = new HashBuilder();
    createQuery().list().stream().sorted((s1, s2) -> s1.getId().compareTo(s2.getId())).forEachOrdered(hashBuilder::add);
    return hashBuilder.build();
  }
  @Override
  public FlowRepository getFlRepo() {
    return flowRepository;
  }

  @Override
  public ServiceExecutor executor() {
    return new ServiceExecutor() {
      @Override
      public FlowServiceExecutor flow(String name) {
        Assert.notNull(name, "Define flow name!");
        Optional<AssetService> service = createQuery().type(ServiceType.FLOW).name(name).get();
        if(service.isEmpty()) {
          throw new DataException(422, new Message("E002", "No flow with id: " + name + "!"));
        }
        
        return new FlowServiceExecutor() {
          private final Map<String, Object> inputs = new HashMap<>();
          @Override
          public FlowServiceExecutor withMap(Map<String, Object> input) {
            this.inputs.putAll(input);
            return this;
          }
          @SuppressWarnings("unchecked")
          @Override
          public FlowServiceExecutor withEntity(Object inputObject) {
            this.inputs.putAll(objectMapper.convertValue(inputObject, Map.class));
            return this;
          }
          
          @Override
          public Object andGetTask(String taskName) {
            validateFlowInput(service.get(), inputs);
            FlowResult flow = service.get().newExecution().insert((Serializable) inputs).run().get();
            
            Collection<FlowTask> tasks = flow.getContext().getTasks(taskName);
            if(tasks.isEmpty()) {
              throw new DataException(422, new Message("E002", "Flow with id: " + flow.getModel().getId() + " does not have task with id: " + taskName + "!"));
            }

            FlowTask task = tasks.iterator().next();
            Serializable delegate = task.getVariables().get(taskName);
            return delegate;
          }
          private void validateFlowInput(AssetService service, Map<String, Object> input) {
            for(TypeDef dataType : service.getDataModel().getParams()) {
              if(dataType.isRequired() && input.get(dataType.getName()) == null) {
                throw new DataException(422, new Message("E003", "Flow with id: " + service.getName() + " can't have null input: " + dataType.getName() + "!"));
              }
            }
          }
        };
      }

      @Override
      public DtServiceExecutor dt(String name) {
        Assert.notNull(name, "Define dt name!");
        Optional<AssetService> service = createQuery().type(ServiceType.DT).name(name).get();
        if(service.isEmpty()) {
          throw new DataException(422, new Message("E002", "No dt with id: " + name + "!"));
        }
        
        return new DtServiceExecutor() {
          private final Map<String, Object> inputs = new HashMap<>();
          @Override
          public DtServiceExecutor withMap(Map<String, Object> input) {
            this.inputs.putAll(input);
            return this;
          }
          @SuppressWarnings("unchecked")
          @Override
          public DtServiceExecutor withEntity(Object inputObject) {
            this.inputs.putAll(objectMapper.convertValue(inputObject, Map.class));
            return this;
          }
          private void validateDtInput(AssetService service, Map<String, Object> input) {
            for(TypeDef dataType : service.getDataModel().getParams()) {
              if(dataType.isRequired() && input.get(dataType.getName()) == null) {
                throw new DataException(422, new Message("E003", "DT with id: " + service.getName() + " can't have null input: " + dataType.getName() + "!"));
              }
            }
          }
          @Override
          public Map<String, Serializable> andGet() {
            validateDtInput(service.get(), inputs);
            final ServiceResponse dt = service.get().newExecution().insert(new MatchingDtInputResolver(inputs)).run();
            final DecisionLog output = dt.get();
            return DecisionProgramExecutor.toValues(output);
          }
          
          @Override
          public List<Map<String, Serializable>> andFind() {
            validateDtInput(service.get(), inputs);
            final ServiceResponse dt = service.get().newExecution().insert(new MatchingDtInputResolver(inputs)).run();
            final List<DecisionLog> output = (List<DecisionLog>) dt.list();
            return output.stream().map(e -> DecisionProgramExecutor.toValues(e)).collect(Collectors.toList());
          }
        };
      }
    };
  }

  @Override
  public MigrationBuilder createMigration() {
    return new GenericServiceExporter(this, objectMapper);
  }

  @Override
  public Migration readMigration(String json) {
    try {
      return objectMapper.readValue(json, Migration.class);
    } catch(Exception e) {
      throw new RuntimeException("Failed to parse migration json, msg: " + e.getMessage(), e);
    }
  }

  @Override
  public String toSrc(MigrationValue migration) {
    try {
      return objectMapper.writeValueAsString(migration.getCommands());
    } catch(Exception e) {
      throw new RuntimeException("Failed to parse migration json for: '" + migration.getName() + "', msg: " + e.getMessage(), e);
    }
  }

  @Override
  public HdesClient getTypes() {
    return types;
  }
}
