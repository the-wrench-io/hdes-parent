package io.resys.hdes.quarkus.composer.pg.deployment;

/*-
 * #%L
 * quarkus-composer-pg-deployment
 * %%
 * Copyright (C) 2020 - 2022 Copyright 2020 ReSys OÜ
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

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanContainerBuildItem;
import io.quarkus.arc.deployment.BeanContainerListenerBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.ShutdownContextBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.vertx.http.deployment.BodyHandlerBuildItem;
import io.quarkus.vertx.http.deployment.HttpRootPathBuildItem;
import io.quarkus.vertx.http.deployment.RouteBuildItem;
import io.quarkus.vertx.http.deployment.devmode.NotFoundPageDisplayableEndpointBuildItem;
import io.resys.hdes.client.spi.Serializers;
import io.resys.hdes.client.spi.web.HdesWebConfig;
import io.resys.hdes.quarkus.composer.pg.IDEServicesProducer;
import io.resys.hdes.quarkus.composer.pg.IDEServicesRecorder;
import io.resys.hdes.quarkus.composer.pg.RuntimeConfig;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;



public class IDEServicesProcessor {
  IDEServicesConfig config;
  
  @BuildStep
  FeatureBuildItem feature() {
    return new FeatureBuildItem(IDEServicesRecorder.FEATURE_BUILD_ITEM);
  }
  
  @BuildStep
  @Record(ExecutionTime.STATIC_INIT)
  void buildtimeInit(
      IDEServicesBuildItem buildItem,
      IDEServicesRecorder recorder,
      BuildProducer<AdditionalBeanBuildItem> buildItems,
      BuildProducer<BeanContainerListenerBuildItem> beans) {
    
    buildItems.produce(AdditionalBeanBuildItem.builder().setUnremovable().addBeanClass(IDEServicesProducer.class).build());
    beans.produce(new BeanContainerListenerBuildItem(recorder.configureBuildtimeConfig(
        buildItem.getConfig().getServicePath(), 
        buildItem.getConfig().getModelsPath(),
        buildItem.getConfig().getExportsPath(), 
        buildItem.getConfig().getCommandsPath(), 
        buildItem.getConfig().getDebugsPath(), 
        buildItem.getConfig().getImportsPath(), 
        buildItem.getConfig().getCopyasPath(), 
        buildItem.getConfig().getResourcesPath(), 
        buildItem.getConfig().getHistoryPath())));
  }
  
  @BuildStep
  @Record(ExecutionTime.RUNTIME_INIT)
  void runtimeInit(
      RuntimeConfig config,
      IDEServicesBuildItem buildItem,
      IDEServicesRecorder recorder,
      
      BeanContainerBuildItem beanContainer, 
      ShutdownContextBuildItem shutdown) {
    
    recorder.configureRuntimeConfig(config);
  }
  
  @BuildStep
  @Record(ExecutionTime.RUNTIME_INIT)
  public void staticContentHandler(
    IDEServicesBuildItem buildItem,
    IDEServicesRecorder recorder,
    HttpRootPathBuildItem httpRoot,
    BuildProducer<RouteBuildItem> routes,
    BodyHandlerBuildItem body,
    IDEServicesConfig config) throws Exception {
    
    final var bodyHandler = body.getHandler();
    final Handler<RoutingContext> handler = recorder.ideServicesHandler();
    
    
    final Consumer<String> addRoute = (path) -> {
      routes.produce(httpRoot.routeBuilder()
          .routeFunction(path, recorder.routeFunction(bodyHandler))
          .handler(handler)
          .displayOnNotFoundPage()
          .build());
      routes.produce(httpRoot.routeBuilder()
          .routeFunction(path + "/", recorder.routeFunction(bodyHandler))
          .handler(handler)
          .displayOnNotFoundPage()
          .build());
    };
    
    addRoute.accept(buildItem.getConfig().getServicePath());
    addRoute.accept(buildItem.getConfig().getCommandsPath());
    addRoute.accept(buildItem.getConfig().getCopyasPath());
    addRoute.accept(buildItem.getConfig().getDebugsPath());
    addRoute.accept(buildItem.getConfig().getExportsPath());
    addRoute.accept(buildItem.getConfig().getHistoryPath());
    addRoute.accept(buildItem.getConfig().getImportsPath());
    addRoute.accept(buildItem.getConfig().getModelsPath());
    addRoute.accept(buildItem.getConfig().getResourcesPath());

    routes.produce(httpRoot.routeBuilder()
        .routeFunction(buildItem.getConfig().getHistoryPath() + "/:id", recorder.idRouteFunctionGet(bodyHandler))
        .handler(handler)
        .displayOnNotFoundPage()
        .build());
    routes.produce(httpRoot.routeBuilder()
        .routeFunction(buildItem.getConfig().getResourcesPath() + "/:id", recorder.idRouteFunctionGet(bodyHandler))
        .handler(handler)
        .displayOnNotFoundPage()
        .build());

  }
  
  @BuildStep
  public ReflectiveClassBuildItem reflection() throws SecurityException, ClassNotFoundException {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    List<String> names = new ArrayList<>();
    
    for(Class<?> clazz : Serializers.VALUES) {
      Class<?>[] declaredClasses = classLoader.loadClass(clazz.getName()).getDeclaredClasses();
      
      names.add(clazz.getName());
      for (Class<?> decl : declaredClasses) {
        names.add(decl.getName());
      }
    }
    
    return new ReflectiveClassBuildItem(true, true, names.toArray(new String[] {}));
  }
  
  @BuildStep
  @Record(ExecutionTime.STATIC_INIT)
  public void frontendBeans(
      IDEServicesRecorder recorder,
      BuildProducer<IDEServicesBuildItem> buildProducer,
      HttpRootPathBuildItem httpRootPathBuildItem,
      BuildProducer<NotFoundPageDisplayableEndpointBuildItem> displayableEndpoints) throws Exception {
    
    final var servicePath = "/" + cleanPath(config.servicePath);
    final var webConfig = HdesWebConfig.builder()
        .servicePath(servicePath)
        .modelsPath(servicePath     + "/" + HdesWebConfig.MODELS)
        .exportsPath(servicePath    + "/" + HdesWebConfig.EXPORTS)
        .commandsPath(servicePath   + "/" + HdesWebConfig.COMMANDS)
        .debugsPath(servicePath     + "/" + HdesWebConfig.DEBUGS)
        .importsPath(servicePath    + "/" + HdesWebConfig.IMPORTS)
        .copyasPath(servicePath     + "/" + HdesWebConfig.COPYAS)
        .resourcesPath(servicePath  + "/" + HdesWebConfig.RESOURCES)
        .build();
    
    displayableEndpoints.produce(new NotFoundPageDisplayableEndpointBuildItem(httpRootPathBuildItem.resolvePath(servicePath), "Hdes Composer Actions"));
    buildProducer.produce(new IDEServicesBuildItem(webConfig));
  }
  
  private static String cleanPath(String value) {
    return IDEServicesProducer.cleanPath(value);
  }
}
