package io.resys.wrench.assets.git;

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

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceStore;
import io.resys.wrench.assets.bundle.spi.store.AssetLocation;
import io.resys.wrench.assets.bundle.spi.store.GitAssetStore;
import io.resys.wrench.assets.bundle.spi.store.git.AssetAuthorProvider;
import io.resys.wrench.assets.bundle.spi.store.git.GitRepository;
import io.resys.wrench.assets.bundle.spi.store.git.SshGitRepository;
import io.resys.wrench.assets.context.config.GitConfigBean;

@ConditionalOnProperty(name = "wrench.assets.git.enabled", havingValue = "true")
public class AssetGitConfiguration {
  @Bean
  @ConditionalOnProperty(name = "security.oauth2.enabled", havingValue = "false", matchIfMissing = true)
  @ConditionalOnMissingBean(AssetAuthorProvider.class)
  public AssetAuthorProvider gitAuthorProvider(GitConfigBean gitConfigBean) {
    return () -> new AssetAuthorProvider.Author(gitConfigBean.getEmail().split("@")[0], gitConfigBean.getEmail());
  }
  @Bean
  public GitRepository gitRepository(GitConfigBean gitConfigBean, AssetAuthorProvider gitAuthorProvider) 
      throws InvalidRemoteException, IOException, GitAPIException {
    return new SshGitRepository(gitConfigBean.toConfig(), gitAuthorProvider);
  }
  @Bean
  public AssetLocation gitAsssetLocation(GitRepository gitRepository) {
    return new AssetLocation(gitRepository.getWorkingDir(), "/assets/", true);
  }
  @Bean
  public ServiceStore serviceStore(GitRepository gitRepository, AssetLocation location) {
    return new GitAssetStore(gitRepository, location);
  }
}
