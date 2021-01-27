package io.resys.hdes.ui.quarkus.deployment;

/*-
 * #%L
 * hdes-ui-quarkus-deployment
 * %%
 * Copyright (C) 2020 Copyright 2020 ReSys OÜ
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.inject.Inject;

import org.jboss.logging.Logger;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanContainerBuildItem;
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
import io.quarkus.deployment.util.FileUtil;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.quarkus.vertx.http.deployment.HttpRootPathBuildItem;
import io.quarkus.vertx.http.deployment.RouteBuildItem;
import io.quarkus.vertx.http.deployment.devmode.NotFoundPageDisplayableEndpointBuildItem;
import io.quarkus.vertx.http.runtime.HandlerType;
import io.resys.hdes.pm.quarkus.runtime.HdesProjectsContextProducer;
import io.resys.hdes.pm.quarkus.runtime.HdesProjectsRecorder;
import io.resys.hdes.pm.quarkus.runtime.handlers.HdesGroupsResourceHandler;
import io.resys.hdes.pm.quarkus.runtime.handlers.HdesProjectsResourceHandler;
import io.resys.hdes.pm.quarkus.runtime.handlers.HdesUsersResourceHandler;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class HdesProjectsProcessor {
  private static final Logger LOGGER = Logger.getLogger(HdesProjectsProcessor.class.getName());
  
  private static final String WEBJAR_GROUP_ID = "io.resys.hdes";
  private static final String WEBJAR_ARTIFACT_ID = "hdes-pm-frontend";
  private static final String WEBJAR_PREFIX = "META-INF/resources/webjars/" + WEBJAR_ARTIFACT_ID;
  
  private static final String FINAL_DESTINATION = "META-INF/hdes-pm-files";
  private static final String FEATURE_BUILD_ITEM = "hdes-projects";
  private static final String TEMP_DIR = "hdes-pm-" + System.nanoTime();
  
  @Inject
  private LaunchModeBuildItem launch;
  
  HdesConfig hdesConfig;

  @ConfigRoot
  public static final class HdesConfig {
    
    /**
     * Enable/disable Hdes UI path, default is true
     */
    @ConfigItem(defaultValue = "true")
    boolean enable;
    
    /**
     * projects UI path, anything except '/'
     */
    @ConfigItem(defaultValue = "/hdes-projects")
    String path;
    
    /**
     * Mongo DB connection URL
     */
    @ConfigItem
    String connectionUrl;
  }

  private static final class HdesProjectsUICache implements Runnable {
    private String path;
    private String dir;
    public HdesProjectsUICache setPath(String path) {
      this.path = path;
      return this;
    }
    public HdesProjectsUICache setDir(String dir) {
      this.dir = dir;
      return this;
    }
    @Override
    public void run() {
      try {
        FileUtil.deleteDirectory(Paths.get(dir));
      } catch (Exception e) {
        LOGGER.error("Error when cleaning Hdes Projects UI temp dir " + e.getMessage(), e);
      }
    }
  }

  @BuildStep
  void feature(BuildProducer<FeatureBuildItem> feature) {
    if (hdesConfig.enable) {
      feature.produce(new FeatureBuildItem(FEATURE_BUILD_ITEM));
    }
  }
  
  @BuildStep
  @Record(ExecutionTime.STATIC_INIT)
  void registerHdesUIBackendExtension(
      HdesProjectsRecorder recorder,
      BuildProducer<RouteBuildItem> routes,
      BuildProducer<NotFoundPageDisplayableEndpointBuildItem> displayableEndpoints,
      BuildProducer<AdditionalBeanBuildItem> buildItems,
      BuildProducer<BeanContainerListenerBuildItem> beans) {
    
    if (!hdesConfig.enable) {
      return;
    }
    if ("/".equals(hdesConfig.path)) {
      throw new ConfigurationError("quarkus.hdes-ui.path was set to \"/\", this is not allowed as it blocks the application from serving anything else.");
    }

    buildItems.produce(AdditionalBeanBuildItem.builder().setUnremovable().addBeanClass(HdesProjectsContextProducer.class).build());
    beans.produce(new BeanContainerListenerBuildItem(recorder.listener(hdesConfig.connectionUrl)));
    
    String path = hdesConfig.path + "/hdes/services";
    
    // Projects
    String projectsPath = path + "/projects";
    routes.produce(new RouteBuildItem(projectsPath, BodyHandler.create()));
    routes.produce(new RouteBuildItem(projectsPath, new HdesProjectsResourceHandler(), HandlerType.BLOCKING));
    displayableEndpoints.produce(new NotFoundPageDisplayableEndpointBuildItem(projectsPath));

    // Groups
    String groupsPath = path + "/groups";
    routes.produce(new RouteBuildItem(groupsPath, BodyHandler.create()));
    routes.produce(new RouteBuildItem(groupsPath, new HdesGroupsResourceHandler(), HandlerType.BLOCKING));
    displayableEndpoints.produce(new NotFoundPageDisplayableEndpointBuildItem(groupsPath));
    
    // Users
    String usersPath = path + "/users";
    routes.produce(new RouteBuildItem(usersPath, BodyHandler.create()));
    routes.produce(new RouteBuildItem(usersPath, new HdesUsersResourceHandler(), HandlerType.BLOCKING));
    displayableEndpoints.produce(new NotFoundPageDisplayableEndpointBuildItem(usersPath));
  }
  
  @BuildStep
  @Record(ExecutionTime.STATIC_INIT)
  public void registerHdesUiServletExtension(
      HdesProjectsRecorder recorder,
      BuildProducer<RouteBuildItem> routes,
      BeanContainerBuildItem container,
      
      BuildProducer<GeneratedResourceBuildItem> generatedResources,
      BuildProducer<NativeImageResourceBuildItem> nativeImageResourceBuildItemBuildProducer,
      
      LiveReloadBuildItem liveReloadBuildItem,
      HttpRootPathBuildItem httpRootPathBuildItem,
      BuildProducer<NotFoundPageDisplayableEndpointBuildItem> displayableEndpoints) throws Exception {
    
    if (!hdesConfig.enable) {
      return;
    }
    if ("/".equals(hdesConfig.path)) {
      throw new ConfigurationError("quarkus.hdes-projects.path was set to \"/\", this is not allowed as it blocks the application from serving anything else.");
    }

    
    final String path = httpRootPathBuildItem.adjustPath(hdesConfig.path);
    final Handler<RoutingContext> handler;
    if (launch.getLaunchMode().isDevOrTest()) {
      HdesProjectsUICache cached = liveReloadBuildItem.getContextObject(HdesProjectsUICache.class);
      boolean extract = cached == null;
      if (cached != null && !cached.path.equals(path)) {
        cached.run();
        extract = true;
        cached.setPath(path);
      }
      if (cached == null) {
        cached = new HdesProjectsUICache().setPath(path);
        liveReloadBuildItem.setContextObject(HdesProjectsUICache.class, cached);
        Runtime.getRuntime().addShutdownHook(new Thread(cached, "Hdes UI Shutdown Hook"));
      }
      if (extract) {
        Path tempDir = Files.createTempDirectory(TEMP_DIR).toRealPath();
        cached.setDir(tempDir.toAbsolutePath().toString());

        processArtifact(path, (GeneratedResourceBuildItem item) -> {
          try {
            Path newFile = tempDir.resolve(item.getName());
            newFile.toFile().getParentFile().mkdirs();
            Files.copy(new ByteArrayInputStream(item.getClassData()), newFile);
          } catch (Exception e) {
            LOGGER.error("Error when creating Hdes Projects UI file " + e.getMessage(), e);
          }
        });
      }
      handler = recorder.handler(cached.dir, httpRootPathBuildItem.adjustPath(hdesConfig.path));
      displayableEndpoints.produce(new NotFoundPageDisplayableEndpointBuildItem(hdesConfig.path + "/"));
      
    } else {
      processArtifact(path, (GeneratedResourceBuildItem item) -> {
        String fileName = FINAL_DESTINATION + "/" + item.getName();
        generatedResources.produce(new GeneratedResourceBuildItem(fileName, item.getClassData()));
        nativeImageResourceBuildItemBuildProducer.produce(new NativeImageResourceBuildItem(fileName));
      });
      handler = recorder.handler(FINAL_DESTINATION, httpRootPathBuildItem.adjustPath(hdesConfig.path));
    }
    
    routes.produce(new RouteBuildItem(hdesConfig.path, handler));
    routes.produce(new RouteBuildItem(hdesConfig.path + "/*", handler));
  }

  private void processArtifact(String path, Consumer<GeneratedResourceBuildItem> consumer) throws IOException {
    HdesArtifactResolver resolver = new HdesArtifactResolver();
    AppArtifact artifact = resolver.getArtifact(WEBJAR_GROUP_ID, WEBJAR_ARTIFACT_ID, null);
    
    try (JarFile jarFile = new JarFile(artifact.getPaths().getSinglePath().toFile())) {
      Enumeration<JarEntry> entries = jarFile.entries();
      String jarPrefix = String.format("%s/%s/", WEBJAR_PREFIX, artifact.getVersion());
      
      while (entries.hasMoreElements()) {
        JarEntry entry = entries.nextElement();
        if (entry.getName().startsWith(jarPrefix) && !entry.isDirectory()) {
          try (InputStream inputStream = jarFile.getInputStream(entry)) {
            String jarName = entry.getName().replace(jarPrefix, "");
            byte[] content = FileUtil.readFileContents(inputStream);
            if (entry.getName().endsWith("index.html")) {
              content = addConfig(new String(content, StandardCharsets.UTF_8), path).getBytes(StandardCharsets.UTF_8);
            }
            
            consumer.accept(new GeneratedResourceBuildItem(jarName, content));
          }
        }
      }
    }
  }
  public String addConfig(String original, String config) {
    return original.replaceFirst("\\_HDES\\_UI\\_CONFIG=\\{\\}", "_HDES_UI_CONFIG={/*empty config*/};");
  }
}
