package io.resys.wrench.assets.bundle.spi;

/*-
 * #%L
 * hdes-spring-bundle-editor
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

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.resys.wrench.assets.controllers.exception.AssetExceptionMapping;
import io.resys.wrench.assets.git.AssetGitConfiguration;

@Configuration
@ConditionalOnProperty(name = "wrench.assets.enabled", havingValue = "true", matchIfMissing = true)
@Import({
  InMemoryAssetConfiguration.class, 
  AssetGitConfiguration.class, 
  AssetComponentConfiguration.class})
public class AssetAutoConfiguration {

  @Bean
  public AssetExceptionMapping assetExceptionMapping() {
    return new AssetExceptionMapping();
  }
}