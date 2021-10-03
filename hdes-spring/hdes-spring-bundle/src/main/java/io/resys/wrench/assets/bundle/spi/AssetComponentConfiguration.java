package io.resys.wrench.assets.bundle.spi;

/*-
 * #%L
 * wrench-assets-bundle
 * %%
 * Copyright (C) 2016 - 2021 Copyright 2020 ReSys OÜ
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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.io.IOUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.resys.hdes.client.api.ast.FlowAstType.NodeFlowVisitor;
import io.resys.hdes.client.api.execution.Service.ServiceInit;
import io.resys.hdes.client.api.model.FlowModel.FlowTaskType;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceBuilder;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceIdGen;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServicePostProcessor;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServicePostProcessorSupplier;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceStore;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceType;
import io.resys.wrench.assets.bundle.spi.builders.GenericServiceQuery;
import io.resys.wrench.assets.bundle.spi.clock.ClockRepository;
import io.resys.wrench.assets.bundle.spi.clock.SystemClockRepository;
import io.resys.wrench.assets.bundle.spi.dt.DtServiceBuilder;
import io.resys.wrench.assets.bundle.spi.flow.FlowDataTypeSupplier;
import io.resys.wrench.assets.bundle.spi.flow.FlowServiceBuilder;
import io.resys.wrench.assets.bundle.spi.flow.FlowServiceDataModelValidator;
import io.resys.wrench.assets.bundle.spi.flow.executors.GenericFlowDtExecutor;
import io.resys.wrench.assets.bundle.spi.flow.executors.GenericFlowServiceExecutor;
import io.resys.wrench.assets.bundle.spi.flow.executors.VariableResolver;
import io.resys.wrench.assets.bundle.spi.flow.hints.PartialTaskInputsAutocomplete;
import io.resys.wrench.assets.bundle.spi.flow.hints.TaskInputMappingAutocomplete;
import io.resys.wrench.assets.bundle.spi.flow.hints.TaskInputsAutocomplete;
import io.resys.wrench.assets.bundle.spi.flow.hints.TaskRefAutocomplete;
import io.resys.wrench.assets.bundle.spi.flowtask.FlowTaskServiceBuilder;
import io.resys.wrench.assets.bundle.spi.postprocessors.FlowDependencyServicePostProcessor;
import io.resys.wrench.assets.bundle.spi.postprocessors.GenericServicePostProcessorSupplier;
import io.resys.wrench.assets.bundle.spi.postprocessors.ListServicePostProcessor;
import io.resys.wrench.assets.bundle.spi.repositories.GenericAssetServiceRepository;
import io.resys.wrench.assets.bundle.spi.store.AssetLocation;
import io.resys.wrench.assets.bundle.spi.store.GenericServiceIdGen;
import io.resys.wrench.assets.bundle.spi.store.ListAssetLoader;
import io.resys.wrench.assets.bundle.spi.store.PostProcessingServiceStore;
import io.resys.wrench.assets.bundle.spi.tag.TagServiceBuilder;
import io.resys.wrench.assets.datatype.api.DataTypeRepository;
import io.resys.wrench.assets.datatype.spi.GenericDataTypeRepository;
import io.resys.wrench.assets.dt.api.DecisionTableRepository;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.DecisionTableExpressionBuilder;
import io.resys.wrench.assets.dt.api.DecisionTableRepository.NodeExpressionExecutor;
import io.resys.wrench.assets.dt.spi.GenericDecisionTableRepository;
import io.resys.wrench.assets.dt.spi.expression.GenericDecisionTableExpressionBuilder;
import io.resys.wrench.assets.dt.spi.expression.GenericExpressionExecutor;
import io.resys.wrench.assets.dt.spi.expression.SpringDynamicValueExpressionExecutor;
import io.resys.wrench.assets.flow.api.FlowAstFactory;
import io.resys.wrench.assets.flow.api.FlowExecutorRepository;
import io.resys.wrench.assets.flow.api.FlowExecutorRepository.FlowTaskExecutor;
import io.resys.wrench.assets.flow.api.FlowRepository;
import io.resys.wrench.assets.flow.spi.GenericFlowExecutorFactory;
import io.resys.wrench.assets.flow.spi.GenericFlowRepository;
import io.resys.wrench.assets.flow.spi.GenericNodeRepository;
import io.resys.wrench.assets.flow.spi.executors.EmptyFlowTaskExecutor;
import io.resys.wrench.assets.flow.spi.executors.EndFlowTaskExecutor;
import io.resys.wrench.assets.flow.spi.executors.ExclusiveFlowTaskExecutor;
import io.resys.wrench.assets.flow.spi.executors.MergeFlowTaskExecutor;
import io.resys.wrench.assets.flow.spi.expressions.SpelExpressionFactory;
import io.resys.wrench.assets.flow.spi.hints.DescAutocomplete;
import io.resys.wrench.assets.flow.spi.hints.IdAutocomplete;
import io.resys.wrench.assets.flow.spi.hints.input.InputAutocomplete;
import io.resys.wrench.assets.flow.spi.hints.input.InputDataTypeAutocomplete;
import io.resys.wrench.assets.flow.spi.hints.input.InputDebugValueAutocomplete;
import io.resys.wrench.assets.flow.spi.hints.input.InputRequiredAutocomplete;
import io.resys.wrench.assets.flow.spi.hints.input.InputsAutocomplete;
import io.resys.wrench.assets.flow.spi.hints.task.SwitchAutocomplete;
import io.resys.wrench.assets.flow.spi.hints.task.SwitchBodyAutocomplete;
import io.resys.wrench.assets.flow.spi.hints.task.TaskAutocomplete;
import io.resys.wrench.assets.flow.spi.hints.task.TaskCollectionAutocomplete;
import io.resys.wrench.assets.flow.spi.hints.task.TaskThenAutocomplete;
import io.resys.wrench.assets.flow.spi.hints.task.TasksAutocomplete;
import io.resys.wrench.assets.flow.spi.validators.DescriptionValidator;
import io.resys.wrench.assets.flow.spi.validators.IdValidator;
import io.resys.wrench.assets.script.api.ScriptRepository;
import io.resys.wrench.assets.script.spi.GenericScriptRepository;
import io.resys.wrench.assets.script.spi.builders.GroovyScriptParser;


@Configuration
public class AssetComponentConfiguration {
  private final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
  
  @Bean
  public AssetServiceRepository assetServiceRepository(
      ApplicationContext context, ObjectMapper objectMapper, 
      ServiceStore origServiceStore) {
    
    final ServiceInit init = new ServiceInit() {
      @Override
      public <T> T get(Class<T> type) {
        return context.getAutowireCapableBeanFactory().createBean(type);
      }
    };
    
    final ClockRepository clockRepository = new SystemClockRepository();
    final DataTypeRepository dataTypeRepository = new GenericDataTypeRepository(objectMapper);
    final DecisionTableRepository decisionTableRepository = decisionTableRepository(dataTypeRepository, objectMapper, origServiceStore);
    final FlowRepository flowRepository = flowRepository(dataTypeRepository, clockRepository, origServiceStore, objectMapper);
    final ScriptRepository scriptRepository = scriptRepository(objectMapper, dataTypeRepository, context);
    
    final ServiceIdGen idGen = new GenericServiceIdGen();
    final Map<ServiceType, Function<ServiceStore, ServiceBuilder>> builders = new HashMap<>();
    builders.put(ServiceType.DT, (store) -> new DtServiceBuilder(idGen, decisionTableRepository, clockRepository, getDefaultContent(ServiceType.DT)));
    builders.put(ServiceType.FLOW, (store) -> new FlowServiceBuilder(idGen, store, flowRepository, flowRepository, clockRepository, getDefaultContent(ServiceType.FLOW)));
    builders.put(ServiceType.FLOW_TASK, (store) -> new FlowTaskServiceBuilder(idGen, store, init, scriptRepository, objectMapper, getDefaultContent(ServiceType.FLOW_TASK)));
    builders.put(ServiceType.TAG, (store) -> new TagServiceBuilder("", idGen, getDefaultContent(ServiceType.TAG)));
    
    final Map<ServiceType, ServicePostProcessor> postProcessors = new HashMap<>();
    postProcessors.put(ServiceType.FLOW_TASK, new FlowDependencyServicePostProcessor(builders));
    postProcessors.put(ServiceType.DT, new FlowDependencyServicePostProcessor(builders));
    postProcessors.put(ServiceType.DATA_TYPE, new ListServicePostProcessor(new FlowDependencyServicePostProcessor(builders)));
    
    final ServicePostProcessorSupplier servicePostProcessorSupplier = new GenericServicePostProcessorSupplier(postProcessors);
    final ServiceStore serviceStore = new PostProcessingServiceStore(origServiceStore, servicePostProcessorSupplier); 
    
    return new GenericAssetServiceRepository(objectMapper,
        decisionTableRepository, flowRepository, scriptRepository, 
        builders, serviceStore);
  }

  @Bean
  public Loader loader(AssetLocation location, AssetServiceRepository assetRepository) {
    return new Loader(location, assetRepository);
  }

  public static class Loader {
    private final AssetLocation location;
    private final AssetServiceRepository assetRepository;

    public Loader(AssetLocation location, AssetServiceRepository assetRepository) {
      super();
      this.location = location;
      this.assetRepository = assetRepository;
    }
    
    @EventListener({ContextRefreshedEvent.class})
    public void load() {
      ListAssetLoader result = new ListAssetLoader(assetRepository, location);
      result.load();
    }
  }
  
  
  private DecisionTableRepository decisionTableRepository(DataTypeRepository dataTypeRepository, ObjectMapper objectMapper, ServiceStore serviceStore) {
    Supplier<DecisionTableExpressionBuilder> springExpressionBuilder = () -> new GenericDecisionTableExpressionBuilder(objectMapper);
    NodeExpressionExecutor expressionExecutor = new GenericExpressionExecutor(springExpressionBuilder);
    return new GenericDecisionTableRepository(
        objectMapper,
        dataTypeRepository,
        expressionExecutor,
        () -> new SpringDynamicValueExpressionExecutor(),
        springExpressionBuilder);
  }


  private FlowRepository flowRepository(
      DataTypeRepository dataTypeRepository,
      ClockRepository clockRepository,
      ServiceStore serviceStore, 
      ObjectMapper objectMapper) {

    SpelExpressionFactory parser = new SpelExpressionFactory();
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    List<NodeFlowVisitor> visitors = Arrays.asList(
        new IdAutocomplete(),
        new DescAutocomplete(),
        new InputsAutocomplete(),
        new TasksAutocomplete(),
        new TaskThenAutocomplete(),
        new TaskRefAutocomplete(serviceStore),
        new InputRequiredAutocomplete(),
        new InputDataTypeAutocomplete(),
        new InputAutocomplete(),
        new TaskAutocomplete(),
        new TaskCollectionAutocomplete(),
        new TaskInputsAutocomplete(serviceStore),
        new PartialTaskInputsAutocomplete(serviceStore),
        new SwitchAutocomplete(),
        new InputDebugValueAutocomplete(),
        new SwitchBodyAutocomplete(),
        new TaskInputMappingAutocomplete(serviceStore),

        new IdValidator(),
        new DescriptionValidator(),
        new FlowServiceDataModelValidator(serviceStore, dataTypeRepository)
        );
    
    VariableResolver variableResolver = new VariableResolver(objectMapper);
    
    Map<FlowTaskType, FlowTaskExecutor> executors = new HashMap<>();
    executors.put(FlowTaskType.END, new EndFlowTaskExecutor());
    executors.put(FlowTaskType.EXCLUSIVE, new ExclusiveFlowTaskExecutor());
    executors.put(FlowTaskType.MERGE, new MergeFlowTaskExecutor());
    executors.put(FlowTaskType.SERVICE, new GenericFlowServiceExecutor(serviceStore, variableResolver));
    executors.put(FlowTaskType.DT, new GenericFlowDtExecutor(() -> new GenericServiceQuery(serviceStore), variableResolver));
    executors.put(FlowTaskType.EMPTY, new EmptyFlowTaskExecutor());

    FlowExecutorRepository executorRepository = new GenericFlowExecutorFactory(executors);  
    FlowAstFactory nodeRepository = new GenericNodeRepository(mapper, new FlowDataTypeSupplier(serviceStore));
    return new GenericFlowRepository(dataTypeRepository, executorRepository, parser, nodeRepository, objectMapper, visitors, clockRepository.get());
  }


  private ScriptRepository scriptRepository(ObjectMapper objectMapper, DataTypeRepository dataTypeRepository, ApplicationContext context) {
    return new GenericScriptRepository(new GroovyScriptParser(objectMapper), dataTypeRepository);
  }

  protected String getDefaultContent(ServiceType type) {
    try {
      String location = getDefaultContentPattern(type);
      Resource resource = resolver.getResource(location);
      return IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
    } catch(IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  protected String getDefaultContentPattern(ServiceType type) {
    switch(type) {
    case DT: return "classpath:defaults/default-dt.json";
    case FLOW: return "classpath:defaults/default-flow.json";
    case FLOW_TASK: return "classpath:defaults/default-flowtask.json";
    case TAG: return "classpath:defaults/default-tag.json";
    case DATA_TYPE: return "classpath:defaults/default-datatype.yaml";
    default: throw new IllegalArgumentException("No default content for service type: " + type + "!");
    }
  }
}
