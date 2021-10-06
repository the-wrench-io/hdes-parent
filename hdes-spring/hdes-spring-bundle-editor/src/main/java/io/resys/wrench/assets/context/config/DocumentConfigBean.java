package io.resys.wrench.assets.context.config;

import org.springframework.beans.factory.annotation.Value;

/*-
 * #%L
 * wrench-component-assets-persistence
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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import io.resys.wrench.assets.bundle.spi.store.document.DocumentConfig;


@Component
@ConfigurationProperties(prefix="wrench.assets.document")
public class DocumentConfigBean {

  private String repositoryName;
  
  @Value("${branchSpecifier:main}")
  private String branchSpecifier;

  @Value("${enabled:false}")
  private boolean enabled;

  @Value("${email:asset.manager@resys.io}")
  private String email;

  @Value("${message:application commit}")
  private String message;

  public DocumentConfig toConfig() {
    return new DocumentConfig()
        .setRepoName(getRepositoryName())
        .setHeadName(branchSpecifier);
  }
  
  
  public String getBranchSpecifier() {
    return branchSpecifier;
  }
  public DocumentConfigBean setBranchSpecifier(String branchSpecifier) {
    this.branchSpecifier = branchSpecifier;
    return this;
  }
  public String getEmail() {
    return email;
  }
  public DocumentConfigBean setEmail(String email) {
    this.email = email;
    return this;
  }
  public String getMessage() {
    return message;
  }
  public DocumentConfigBean setMessage(String message) {
    this.message = message;
    return this;
  }
  public boolean isEnabled() {
    return enabled;
  }
  public DocumentConfigBean setEnabled(boolean enabled) {
    this.enabled = enabled;
    return this;
  }
  
  public String getRepositoryName() {
    return repositoryName;
  }
  public DocumentConfigBean setRepositoryName(String repositoryName) {
    this.repositoryName = repositoryName;
    return this;
  }
}
