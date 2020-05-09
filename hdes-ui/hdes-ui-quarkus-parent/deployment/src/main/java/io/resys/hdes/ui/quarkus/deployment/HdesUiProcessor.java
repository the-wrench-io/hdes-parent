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
import io.quarkus.deployment.index.ResolvedArtifact;
import io.quarkus.deployment.util.FileUtil;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.quarkus.vertx.http.deployment.HttpRootPathBuildItem;
import io.quarkus.vertx.http.deployment.RouteBuildItem;
import io.quarkus.vertx.http.deployment.devmode.NotFoundPageDisplayableEndpointBuildItem;
import io.quarkus.vertx.http.runtime.HandlerType;
import io.resys.hdes.ui.quarkus.runtime.HdesDefsHandler;
import io.resys.hdes.ui.quarkus.runtime.HdesUIBackendProducer;
import io.resys.hdes.ui.quarkus.runtime.HdesUiRecorder;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class HdesUiProcessor {
  private static final Logger LOGGER = Logger.getLogger(HdesUiProcessor.class.getName());
  private static final String WEBJAR_GROUP_ID = "io.resys.hdes";
  private static final String WEBJAR_ARTIFACT_ID = "hdes-ui-frontend";
  private static final String WEBJAR_PREFIX = "META-INF/resources/webjars/" + WEBJAR_ARTIFACT_ID;
  
  private static final String FINAL_DESTINATION = "META-INF/hdes-ui-files";
  private static final String FEATURE_BUILD_ITEM = "hdes-ui";
  private static final String TEMP_DIR = "hdes-ui-" + System.nanoTime();
  @Inject
  private LaunchModeBuildItem launch;
  HdesUiConfig hdesUiConfig;

  @ConfigRoot
  static final class HdesUiConfig {
    /**
     * Hdes UI path, anything except '/'
     */
    @ConfigItem(defaultValue = "/hdes-ui")
    String path;
    /**
     * Enable/disable Hdes UI path, default is true
     */
    @ConfigItem(defaultValue = "true")
    boolean enable;
  }

  private static final class HdesUICache implements Runnable {
    private String path;
    private String dir;
    public HdesUICache setPath(String path) {
      this.path = path;
      return this;
    }
    public HdesUICache setDir(String dir) {
      this.dir = dir;
      return this;
    }
    @Override
    public void run() {
      try {
        FileUtil.deleteDirectory(Paths.get(dir));
      } catch (Exception e) {
        LOGGER.error("Error when cleaning Hdes UI temp dir " + e.getMessage(), e);
      }
    }
  }

  @BuildStep
  void feature(BuildProducer<FeatureBuildItem> feature) {
    if (hdesUiConfig.enable) {
      feature.produce(new FeatureBuildItem(FEATURE_BUILD_ITEM));
    }
  }
  
  @BuildStep
  @Record(ExecutionTime.STATIC_INIT)
  void registerHdesUIBackendExtension(
      HdesUiRecorder recorder,
      BuildProducer<RouteBuildItem> routes,
      BuildProducer<NotFoundPageDisplayableEndpointBuildItem> displayableEndpoints,
      BuildProducer<AdditionalBeanBuildItem> buildItems,
      BuildProducer<BeanContainerListenerBuildItem> beans) {
    
    if (!hdesUiConfig.enable) {
      return;
    }
    if ("/".equals(hdesUiConfig.path)) {
      throw new ConfigurationError("quarkus.hdes-ui.path was set to \"/\", this is not allowed as it blocks the application from serving anything else.");
    }

    buildItems.produce(AdditionalBeanBuildItem.builder().setUnremovable().addBeanClass(HdesUIBackendProducer.class).build());

    beans.produce(new BeanContainerListenerBuildItem(recorder.listener()));
    
    String path = hdesUiConfig.path + "/services";
    String defsPath = path + "/defs";
    routes.produce(new RouteBuildItem(defsPath, new HdesDefsHandler(), HandlerType.BLOCKING));
    displayableEndpoints.produce(new NotFoundPageDisplayableEndpointBuildItem(defsPath));
  }

  @BuildStep
  @Record(ExecutionTime.STATIC_INIT)
  public void registerHdesUiServletExtension(
      HdesUiRecorder recorder,
      BuildProducer<RouteBuildItem> routes,
      BeanContainerBuildItem container,
      
      BuildProducer<GeneratedResourceBuildItem> generatedResources,
      BuildProducer<NativeImageResourceBuildItem> nativeImageResourceBuildItemBuildProducer,
      
      LiveReloadBuildItem liveReloadBuildItem,
      HttpRootPathBuildItem httpRootPathBuildItem,
      BuildProducer<NotFoundPageDisplayableEndpointBuildItem> displayableEndpoints) throws Exception {
    
    if (!hdesUiConfig.enable) {
      return;
    }
    if ("/".equals(hdesUiConfig.path)) {
      throw new ConfigurationError("quarkus.hdes-ui.path was set to \"/\", this is not allowed as it blocks the application from serving anything else.");
    }
    
    final String path = httpRootPathBuildItem.adjustPath(hdesUiConfig.path);
    final Handler<RoutingContext> handler;
    if (launch.getLaunchMode().isDevOrTest()) {
      HdesUICache cached = liveReloadBuildItem.getContextObject(HdesUICache.class);
      boolean extract = cached == null;
      if (cached != null && !cached.path.equals(path)) {
        cached.run();
        extract = true;
        cached.setPath(path);
      }
      if (cached == null) {
        cached = new HdesUICache().setPath(path);
        liveReloadBuildItem.setContextObject(HdesUICache.class, cached);
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
            LOGGER.error("Error when creating Hdes UI file " + e.getMessage(), e);
          }
        });
      }
      handler = recorder.handler(cached.dir, httpRootPathBuildItem.adjustPath(hdesUiConfig.path));
      displayableEndpoints.produce(new NotFoundPageDisplayableEndpointBuildItem(hdesUiConfig.path + "/"));
      
    } else {
      processArtifact(path, (GeneratedResourceBuildItem item) -> {
        String fileName = FINAL_DESTINATION + "/" + item.getName();
        generatedResources.produce(new GeneratedResourceBuildItem(fileName, item.getClassData()));
        nativeImageResourceBuildItemBuildProducer.produce(new NativeImageResourceBuildItem(fileName));
      });
      handler = recorder.handler(FINAL_DESTINATION, httpRootPathBuildItem.adjustPath(hdesUiConfig.path));
    }
    
    routes.produce(new RouteBuildItem(hdesUiConfig.path, handler));
    routes.produce(new RouteBuildItem(hdesUiConfig.path + "/*", handler));
  }

  private void processArtifact(String path, Consumer<GeneratedResourceBuildItem> consumer) throws IOException {
    HdesArtifactResolver resolver = new HdesArtifactResolver();
    ResolvedArtifact artifact = resolver.getArtifact(WEBJAR_GROUP_ID, WEBJAR_ARTIFACT_ID, null);
    
    try (JarFile jarFile = new JarFile(artifact.getArtifactPath().toFile())) {
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
