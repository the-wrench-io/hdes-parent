package io.resys.hdes.projects.quarkus.deployment;

/*-
 * #%L
 * hdes-projects-quarkus-deployment
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

import java.nio.file.Path;
import java.util.Map;

import javax.inject.Inject;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanContainerListenerBuildItem;
import io.quarkus.bootstrap.model.AppArtifact;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.GeneratedResourceBuildItem;
import io.quarkus.deployment.builditem.LaunchModeBuildItem;
import io.quarkus.deployment.builditem.LiveReloadBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.configuration.ConfigurationError;
import io.quarkus.deployment.pkg.builditem.CurateOutcomeBuildItem;
import io.quarkus.deployment.util.WebJarUtil;
import io.quarkus.vertx.http.deployment.BodyHandlerBuildItem;
import io.quarkus.vertx.http.deployment.HttpRootPathBuildItem;
import io.quarkus.vertx.http.deployment.NonApplicationRootPathBuildItem;
import io.quarkus.vertx.http.deployment.RouteBuildItem;
import io.quarkus.vertx.http.deployment.devmode.NotFoundPageDisplayableEndpointBuildItem;
import io.resys.hdes.pm.quarkus.runtime.HdesProjectsContextProducer;
import io.resys.hdes.pm.quarkus.runtime.HdesProjectsRecorder;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class HdesProjectsProcessor {
  private static final String WEBJAR_GROUP_ID = "io.resys.hdes";
  private static final String WEBJAR_ARTIFACT_ID = "hdes-pm-frontend";
  private static final String WEBJAR_PREFIX = "META-INF/resources/webjars/" + WEBJAR_ARTIFACT_ID;
  private static final String FINAL_DESTINATION = "META-INF/hdes-pm-files";
  public static final String FEATURE_BUILD_ITEM = "hdes-projects";
  
  @Inject
  private LaunchModeBuildItem launch;
  
  HdesProjectsConfig hdesProjectsConfig;
  
  @BuildStep
  FeatureBuildItem feature() {
    return new FeatureBuildItem(FEATURE_BUILD_ITEM);
  }

  @BuildStep
  @Record(ExecutionTime.STATIC_INIT)
  void backendBeans(
      HdesProjectsRecorder recorder,
      BuildProducer<AdditionalBeanBuildItem> buildItems,
      BuildProducer<BeanContainerListenerBuildItem> beans) {
    
    if ("/".equals(hdesProjectsConfig.connectionUrl)) {
      throw new ConfigurationError("quarkus.hdes-projects.connectionUrl was set to \"/\", this is not allowed as it blocks the application from serving anything else.");
    }

    buildItems.produce(AdditionalBeanBuildItem.builder().setUnremovable().addBeanClass(HdesProjectsContextProducer.class).build());
    beans.produce(new BeanContainerListenerBuildItem(recorder.listener(hdesProjectsConfig.connectionUrl)));
  }
  
  @BuildStep
  @Record(ExecutionTime.RUNTIME_INIT)
  void backendHandlers(
      HdesProjectsRecorder recorder,
      BodyHandlerBuildItem body,
      BuildProducer<RouteBuildItem> routes) {
    
    if ("/".equals(hdesProjectsConfig.backendPath)) {
      throw new ConfigurationError("quarkus.hdes-projects.backendPath was set to \"/\", this is not allowed as it blocks the application from serving anything else.");
    }

    routes.produce(new RouteBuildItem.Builder()
        .routeFunction(recorder.routeFunction(hdesProjectsConfig.getUsers(), body.getHandler()))
        .handler(recorder.userHandler())
        .blockingRoute()
        .build());
    
    routes.produce(new RouteBuildItem.Builder()
        .routeFunction(recorder.routeFunction(hdesProjectsConfig.getGroups(), body.getHandler()))
        .handler(recorder.groupHandler())
        .blockingRoute()
        .build());
    
    routes.produce(new RouteBuildItem.Builder()
        .routeFunction(recorder.routeFunction(hdesProjectsConfig.getProjects(), body.getHandler()))
        .handler(recorder.projectHandler())
        .blockingRoute()
        .build());
  }
  
  @BuildStep
  @Record(ExecutionTime.RUNTIME_INIT)
  public void frontendHandler(
    HdesProjectsRecorder recorder,
    BuildProducer<RouteBuildItem> routes,
    HdesUIBuildItem buildItem,
    HdesProjectsConfig uiConfig) throws Exception {

    Handler<RoutingContext> handler = recorder.uiHandler(
        buildItem.getUiFinalDestination(),
        buildItem.getUiPath());

    routes.produce(
      new RouteBuildItem.Builder()
        .route(uiConfig.frontendPath)
        .handler(handler)
        .nonApplicationRoute()
        .build());
    routes.produce(
      new RouteBuildItem.Builder()
        .route(uiConfig.frontendPath + "/*")
        .handler(handler)
        .nonApplicationRoute()
        .build());
  }
  
  @BuildStep
  @Record(ExecutionTime.STATIC_INIT)
  public void frontendBeans(
      BuildProducer<HdesUIBuildItem> buildProducer,
      
      BuildProducer<GeneratedResourceBuildItem> generatedResources,
      BuildProducer<NativeImageResourceBuildItem> nativeImage,
      
      NonApplicationRootPathBuildItem nonApplicationRootPathBuildItem,
      CurateOutcomeBuildItem curateOutcomeBuildItem,
      
      LiveReloadBuildItem liveReloadBuildItem,
      HttpRootPathBuildItem httpRootPathBuildItem,
      BuildProducer<NotFoundPageDisplayableEndpointBuildItem> displayableEndpoints) throws Exception {
    
    if ("/".equals(hdesProjectsConfig.frontendPath)) {
      throw new ConfigurationError("quarkus.hdes-projects.frontendPath was set to \"/\", this is not allowed as it blocks the application from serving anything else.");
    }
    
    AppArtifact artifact = WebJarUtil.getAppArtifact(curateOutcomeBuildItem, WEBJAR_GROUP_ID, WEBJAR_ARTIFACT_ID);
    final String frontendPath = httpRootPathBuildItem.adjustPath(hdesProjectsConfig.frontendPath);
    if (launch.getLaunchMode().isDevOrTest()) {
      Path tempPath = WebJarUtil.copyResourcesForDevOrTest(curateOutcomeBuildItem, launch, artifact, WEBJAR_PREFIX + "/" + artifact.getVersion());
      
      // Update index.html
      Path index = tempPath.resolve("index.html");
      
      WebJarUtil.updateFile(index, IndexFactory.builder()
        .frontend(frontendPath)
        .backend(httpRootPathBuildItem.adjustPath(hdesProjectsConfig.backendPath))
        .backendProjects(httpRootPathBuildItem.adjustPath(hdesProjectsConfig.getProjects()))
        .backendGroups(httpRootPathBuildItem.adjustPath(hdesProjectsConfig.getGroups()))
        .backendUsers(httpRootPathBuildItem.adjustPath(hdesProjectsConfig.getUsers()))
        .index(index)
        .build());
      
      
      buildProducer.produce(new HdesUIBuildItem(tempPath.toAbsolutePath().toString(),
              nonApplicationRootPathBuildItem.adjustPath(hdesProjectsConfig.frontendPath)));
      displayableEndpoints.produce(new NotFoundPageDisplayableEndpointBuildItem(
              nonApplicationRootPathBuildItem.adjustPath(hdesProjectsConfig.frontendPath + "/"), "HDES Projects UI"));

      // Handle live reload of branding files
      if (liveReloadBuildItem.isLiveReload() && !liveReloadBuildItem.getChangedResources().isEmpty()) {
          WebJarUtil.hotReloadBrandingChanges(
              curateOutcomeBuildItem, launch, artifact,
                  liveReloadBuildItem.getChangedResources());
      }
    
    } else {
      Map<String, byte[]> files = WebJarUtil.copyResourcesForProduction(curateOutcomeBuildItem, artifact, WEBJAR_PREFIX);
      for (Map.Entry<String, byte[]> file : files.entrySet()) {
        String fileName = file.getKey();
        byte[] content;
        if (fileName.endsWith("index.html")) {
          content = IndexFactory.builder()
              .frontend(frontendPath)
              .backend(httpRootPathBuildItem.adjustPath(hdesProjectsConfig.backendPath))
              .backendProjects(httpRootPathBuildItem.adjustPath(hdesProjectsConfig.getProjects()))
              .backendGroups(httpRootPathBuildItem.adjustPath(hdesProjectsConfig.getGroups()))
              .backendUsers(httpRootPathBuildItem.adjustPath(hdesProjectsConfig.getUsers()))
              .index(file.getValue())
              .build();
            
        } else {
          content = file.getValue();
        }
        fileName = FINAL_DESTINATION + "/" + fileName;
        generatedResources.produce(new GeneratedResourceBuildItem(fileName, content));
        nativeImage.produce(new NativeImageResourceBuildItem(fileName));
      }
      buildProducer.produce(new HdesUIBuildItem(
        FINAL_DESTINATION,
        nonApplicationRootPathBuildItem.adjustPath(hdesProjectsConfig.frontendPath)));
    }
  }
}
