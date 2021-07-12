package io.resys.wrench.assets.bundle.spi;

/*-
 * #%L
 * wrench-assets-bundle
 * %%
 * Copyright (C) 2016 - 2019 Copyright 2016 ReSys OÜ
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

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.resys.wrench.assets.bundle.spi.store.git.AssetAuthorProvider;
import io.resys.wrench.assets.context.config.GitConfigBean;
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

  @Bean
  @ConditionalOnMissingBean(AssetAuthorProvider.class)
  @ConditionalOnProperty(value = "security.oauth2.enabled", havingValue = "false", matchIfMissing = true)
  public AssetAuthorProvider assetAuthorProvider(GitConfigBean gitConfigBean) {
    AssetAuthorProvider.Author result = new AssetAuthorProvider.Author("assetManager", "assetManager@resys.io");
    return () -> result;
  }
}
