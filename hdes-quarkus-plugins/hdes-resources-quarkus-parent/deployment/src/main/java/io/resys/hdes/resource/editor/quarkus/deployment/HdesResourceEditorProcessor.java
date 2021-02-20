package io.resys.hdes.resource.editor.quarkus.deployment;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import io.resys.hdes.pm.quarkus.runtime.HdesResourceEditorContextProducer;
import io.resys.hdes.pm.quarkus.runtime.HdesResourceEditorRecorder;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class HdesResourceEditorProcessor {
  private static final Logger LOGGER = LoggerFactory.getLogger(HdesResourceEditorProcessor.class);
  private static final String WEBJAR_GROUP_ID = "io.resys.hdes";
  private static final String WEBJAR_ARTIFACT_ID = "hdes-re-frontend";
  private static final String WEBJAR_PREFIX = "META-INF/resources/webjars/" + WEBJAR_ARTIFACT_ID + "/";
  private static final String FINAL_DESTINATION = "META-INF/hdes-pm-files";
  public static final String FEATURE_BUILD_ITEM = "hdes-re";
  
  @Inject
  private LaunchModeBuildItem launch;
  
  HdesResourceEditorConfig hdesResourceEditorConfig;
  
  @BuildStep
  FeatureBuildItem feature() {
    return new FeatureBuildItem(FEATURE_BUILD_ITEM);
  }

  @BuildStep
  @Record(ExecutionTime.STATIC_INIT)
  void backendBeans(
      HdesResourceEditorRecorder recorder,
      BuildProducer<AdditionalBeanBuildItem> buildItems,
      BuildProducer<BeanContainerListenerBuildItem> beans) {
    
    if ("/".equals(hdesResourceEditorConfig.connectionUrl)) {
      throw new ConfigurationError("quarkus.hdes-projects.connectionUrl was set to \"/\", this is not allowed as it blocks the application from serving anything else.");
    }

    buildItems.produce(AdditionalBeanBuildItem.builder().setUnremovable().addBeanClass(HdesResourceEditorContextProducer.class).build());
    beans.produce(new BeanContainerListenerBuildItem(recorder.listener(
        hdesResourceEditorConfig.connectionUrl, 
        hdesResourceEditorConfig.dbName)));
  }
  
  @BuildStep
  @Record(ExecutionTime.RUNTIME_INIT)
  void backendHandlers(
      HdesResourceEditorRecorder recorder,
      NonApplicationRootPathBuildItem nonApplicationRootPathBuildItem,
      HttpRootPathBuildItem httpRootPathBuildItem,
      BodyHandlerBuildItem body,
      BuildProducer<RouteBuildItem> routes) {
    
    if ("/".equals(hdesResourceEditorConfig.backendPath)) {
      throw new ConfigurationError("quarkus.hdes-projects.backendPath was set to \"/\", this is not allowed as it blocks the application from serving anything else.");
    }

    
    routes.produce(new RouteBuildItem.Builder()
        .routeFunction(recorder.routeFunction(httpRootPathBuildItem.adjustPath(nonApplicationRootPathBuildItem.adjustPath(hdesResourceEditorConfig.getProjects())), body.getHandler()))
        .handler(recorder.projectHandler())
        .blockingRoute()
        .build());
  }
  
  @BuildStep
  @Record(ExecutionTime.RUNTIME_INIT)
  public void frontendHandler(
    HdesResourceEditorRecorder recorder,
    BuildProducer<RouteBuildItem> routes,
    HdesResourceEditorUIBuildItem buildItem,
    HdesResourceEditorConfig uiConfig,
    BodyHandlerBuildItem body) throws Exception {
    
    Handler<RoutingContext> handler = recorder.uiHandler(buildItem.getUiFinalDestination(), buildItem.getUiPath());

    routes.produce(new RouteBuildItem.Builder()
        .route(uiConfig.frontendPath)
        .handler(handler)
        .nonApplicationRoute()
        .build());
    routes.produce(new RouteBuildItem.Builder()
        .route(uiConfig.frontendPath + "/*")
        .handler(handler)
        .nonApplicationRoute()
        .build());
  }
  
  @BuildStep
  @Record(ExecutionTime.STATIC_INIT)
  public void frontendBeans(
      HdesResourceEditorRecorder recorder,
      BuildProducer<HdesResourceEditorUIBuildItem> buildProducer,
      
      BuildProducer<GeneratedResourceBuildItem> generatedResources,
      BuildProducer<NativeImageResourceBuildItem> nativeImage,
      
      NonApplicationRootPathBuildItem nonApplicationRootPathBuildItem,
      CurateOutcomeBuildItem curateOutcomeBuildItem,
      
      LiveReloadBuildItem liveReloadBuildItem,
      HttpRootPathBuildItem httpRootPathBuildItem,
      BuildProducer<NotFoundPageDisplayableEndpointBuildItem> displayableEndpoints) throws Exception {
    
    if ("/".equals(hdesResourceEditorConfig.frontendPath)) {
      throw new ConfigurationError("quarkus.hdes-projects.frontendPath was set to \"/\", this is not allowed as it blocks the application from serving anything else.");
    }
    
    final AppArtifact artifact = WebJarUtil.getAppArtifact(curateOutcomeBuildItem, WEBJAR_GROUP_ID, WEBJAR_ARTIFACT_ID);    
    if (launch.getLaunchMode().isDevOrTest()) {
      Path tempPath = WebJarUtil.copyResourcesForDevOrTest(curateOutcomeBuildItem, launch, artifact, WEBJAR_PREFIX + artifact.getVersion());
      
      // Update index.html
      Path index = tempPath.resolve("index.html");
      final String frontendPath = httpRootPathBuildItem.adjustPath(nonApplicationRootPathBuildItem.adjustPath(hdesResourceEditorConfig.frontendPath));
      
      WebJarUtil.updateFile(index, IndexFactory.builder()
        .frontend(frontendPath)
        .backend(httpRootPathBuildItem.adjustPath(nonApplicationRootPathBuildItem.adjustPath(hdesResourceEditorConfig.backendPath)))
        .backendProjects(httpRootPathBuildItem.adjustPath(nonApplicationRootPathBuildItem.adjustPath(hdesResourceEditorConfig.getProjects())))
        .index(index)
        .build());
      
      
      buildProducer.produce(new HdesResourceEditorUIBuildItem(tempPath.toAbsolutePath().toString(),
              nonApplicationRootPathBuildItem.adjustPath(hdesResourceEditorConfig.frontendPath)));
      displayableEndpoints.produce(new NotFoundPageDisplayableEndpointBuildItem(
              nonApplicationRootPathBuildItem.adjustPath(hdesResourceEditorConfig.frontendPath + "/"), "HDES Projects UI"));

      // Handle live reload of branding files
      if (liveReloadBuildItem.isLiveReload() && !liveReloadBuildItem.getChangedResources().isEmpty()) {
          WebJarUtil.hotReloadBrandingChanges(
              curateOutcomeBuildItem, launch, artifact,
                  liveReloadBuildItem.getChangedResources());
      }
    
    } else {
      final String frontendPath = httpRootPathBuildItem.adjustPath(nonApplicationRootPathBuildItem.adjustPath(hdesResourceEditorConfig.frontendPath));
      Map<String, byte[]> files = WebJarUtil.copyResourcesForProduction(curateOutcomeBuildItem, artifact, WEBJAR_PREFIX + artifact.getVersion());

      boolean indexReplaced = false; 
      for (Map.Entry<String, byte[]> file : files.entrySet()) {
        String fileName = file.getKey();
        byte[] content;
        if (fileName.endsWith("index.html")) {
          content = IndexFactory.builder()
              .frontend(frontendPath)
              .backend(httpRootPathBuildItem.adjustPath(nonApplicationRootPathBuildItem.adjustPath(hdesResourceEditorConfig.backendPath)))
              .backendProjects(httpRootPathBuildItem.adjustPath(nonApplicationRootPathBuildItem.adjustPath(hdesResourceEditorConfig.getProjects())))
              .index(file.getValue())
              .build();
          indexReplaced = true;
        } else {
          content = file.getValue();
        }
        
        fileName = FINAL_DESTINATION + "/" + fileName;
        generatedResources.produce(new GeneratedResourceBuildItem(fileName, content));
        nativeImage.produce(new NativeImageResourceBuildItem(fileName));
      }
      
      if(!indexReplaced) {
        throw new ConfigurationError(new StringBuilder("Failed to create frontend index.html, ")
            .append("artifact = ").append(artifact).append(System.lineSeparator()).append(",")
            .append("path = ").append(frontendPath).append("!")
            .append("final destination = ").append(FINAL_DESTINATION).append("!")
            .toString());
      }
      
      buildProducer.produce(new HdesResourceEditorUIBuildItem(FINAL_DESTINATION, frontendPath));
    }
  }
}
