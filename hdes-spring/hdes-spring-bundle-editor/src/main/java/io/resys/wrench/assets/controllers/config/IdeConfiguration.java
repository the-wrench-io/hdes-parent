package io.resys.wrench.assets.controllers.config;

/*-
 * #%L
 * wrench-component-resource
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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;

import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository;
import io.resys.wrench.assets.bundle.spi.flow.executors.TransientFlowExecutor;
import io.resys.wrench.assets.bundle.spi.repositories.GenericAssetIdeServices;
import io.resys.wrench.assets.context.config.IdeConfigBean;
import io.resys.wrench.assets.controllers.IdeController;
import io.resys.wrench.assets.controllers.IdeServicesController;
import io.resys.wrench.assets.controllers.IndexController;
import io.resys.wrench.assets.datatype.spi.util.FileUtils;

@Configuration
@ConditionalOnProperty(name = "wrench.assets.ide.enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties(IdeConfigBean.class)
public class IdeConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(IdeConfiguration.class);

  @Value("${server.servlet.context-path:}")
  private String contextPath;
  private static final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

  
  
  @Bean
  @ConditionalOnProperty(name = "server.servlet.context-path", matchIfMissing = false)
  public IdeIndexConfigBean uiClasspathConfig() {
    final String contextPath;
    try {
      contextPath = FileUtils.cleanPath(this.contextPath).length() <= 1 ? "/" : "/" + FileUtils.cleanPath(this.contextPath) + "/";
    } catch (Exception e) {
      return new IdeIndexConfigBean();
    }
    
    try {
      final String path = "webjars/wrench-assets-ide/" + getVersion() + "/";
      
      final String js = chunkJs();
      final String hash = js.substring(0, js.length() - 3);
      final String configJs = resolveRuntimeScript("classpath*:**/wrench-assets-ide/**/env-config.js");
      final String manifest = resolveRuntimeScript("classpath*:**/wrench-assets-ide/**/manifest.json");
      final String mainJs = resolveRuntimeScript("classpath*:**/wrench-assets-ide/**/static/js/main*.chunk.js"); 
      
      final List<String> css = Arrays.asList(
          contextPath + path + "static/css/" + resolveRuntimeScript("classpath*:**/wrench-assets-ide/**/static/css/*.chunk.css"));
      
      final IdeIndexConfigBean config = new IdeIndexConfigBean(hash, css,
          contextPath + path + manifest,
          contextPath + path + "static/js/" + mainJs, 
          contextPath + path + "static/js/" + js, 
          contextPath + path + configJs
          );
      
      if(LOGGER.isDebugEnabled()) {
        LOGGER.debug("ASSETS IDE is enabled." + System.lineSeparator() + config);
      }
      
      return config;
    } catch (Exception e) {
      LOGGER.debug("ASSETS IDE is disabled.");
      return new IdeIndexConfigBean();
    }
  }

  private String getVersion() throws IOException {
    Resource[] resources = resolver.getResources("classpath*:**/wrench-assets-ide/**/index.html");
    if (resources.length > 0) {
      String uri = resources[0].getURI().toString();
      String[] paths = uri.split("\\/");
      return paths[paths.length - 2];
    }
    return null;
  }
  
  private String resolveRuntimeScript(String ideManifestJsPattern) throws IOException {
    Resource[] resources = resolver.getResources(ideManifestJsPattern);
    if (resources.length > 0) {
      return resources[0].getFilename();
    }
    return null;
  }
  
  private String chunkJs() throws IOException {
    Resource[] resources = resolver.getResources("classpath*:**/wrench-assets-ide/**/static/js/*.chunk.js");
    for (Resource resource : resources) {
      if (resource.getFilename().contains("main")) {
        continue;
      }
      return resource.getFilename();
    }
    return null;
  }

  @Bean
  @ConditionalOnProperty(name = "server.servlet.context-path", matchIfMissing = false)
  public IdeController ideController(IdeIndexConfigBean indexConfig, IdeConfigBean uiConfigBean) {
    if(LOGGER.isDebugEnabled()) {
      LOGGER.debug("ASSET UI hash: " + indexConfig.getHash());
    }
    return new IdeController(uiConfigBean, indexConfig);
  }

  @Bean
  public IdeServicesController ideServicesController(ObjectMapper objectMapper, AssetServiceRepository assetServiceRepository, IdeIndexConfigBean ideConfig) {
    TransientFlowExecutor transientFlowExecutor = new TransientFlowExecutor(objectMapper);
    objectMapper.registerModule(new GuavaModule());
    
    return new IdeServicesController(
        new GenericAssetIdeServices(objectMapper, transientFlowExecutor, assetServiceRepository, ideConfig.getHash())
      );
  }

  @Bean
  @ConditionalOnProperty(name = "wrench.assets.ide.redirect", havingValue = "true", matchIfMissing = true)
  public IndexController indexController(IdeConfigBean uiConfigBean) {
    return new IndexController(uiConfigBean);
  }
}
