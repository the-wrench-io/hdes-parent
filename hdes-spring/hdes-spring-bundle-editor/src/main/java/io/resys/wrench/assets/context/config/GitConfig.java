package io.resys.wrench.assets.context.config;

/*-
 * #%L
 * wrench-assets-services
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

public class GitConfig {

  private String repositoryUrl;
  private String branchSpecifier;
  private String repositoryPath;
  private String privateKey;
  private String email;
  private String message;
  private String path;

  public String getRepositoryUrl() {
    return repositoryUrl;
  }
  public GitConfig setRepositoryUrl(String repositoryUrl) {
    this.repositoryUrl = repositoryUrl;
    return this;
  }
  public String getBranchSpecifier() {
    return branchSpecifier;
  }
  public GitConfig setBranchSpecifier(String branchSpecifier) {
    this.branchSpecifier = branchSpecifier;
    return this;
  }
  public String getPrivateKey() {
    return privateKey;
  }
  public GitConfig setPrivateKey(String privateKey) {
    this.privateKey = privateKey;
    return this;
  }
  public String getPath() {
    return path;
  }
  public GitConfig setPath(String path) {
    this.path = path;
    return this;
  }
  public String getEmail() {
    return email;
  }
  public GitConfig setEmail(String email) {
    this.email = email;
    return this;
  }
  public String getMessage() {
    return message;
  }
  public GitConfig setMessage(String message) {
    this.message = message;
    return this;
  }
  public String getRepositoryPath() {
    return repositoryPath;
  }
  public GitConfig setRepositoryPath(String repositoryPath) {
    this.repositoryPath = repositoryPath;
    return this;
  }
}
