package io.resys.wrench.assets.bundle.spi.store;

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

import java.io.Serializable;

import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceType;

public class AssetLocation implements Serializable {
  private static final long serialVersionUID = -5312216893265396576L;

  private final String value;
  private final boolean git;
  private final String assetFolder;

  public AssetLocation(String value, String assetFolder, boolean git) {
    super();
    this.value = value;
    this.assetFolder = assetFolder;
    this.git = git;
  }
  public String getResourceFullName(ServiceType type, String pointer) {
    return value + getResourceName(type, pointer);
  }
  public String getResourceName(ServiceType type, String pointer) {
    return assetFolder + getPath(type) + "/" + pointer;
  }
  public String getResourceId(ServiceType type, String filename) {
    return filename.substring(0, filename.lastIndexOf("."));
  }

  private String getPath(ServiceType type) {
    switch (type) {
    case FLOW:
      return "flow";
    case FLOW_TASK:
      return "flowtask";
    case DT:
      return "dt";
    default: throw new IllegalArgumentException("Unknown asset type:" + type + "!");
    }
  }
  public String getFlowRegex() {
    return withRegex("**/flow/**/*.json");
  }
  public String getFlowTaskRegex() {
    return withRegex("**/flowtask/**/*.json");
  }
  public String getDtRegex() {
    return withRegex("**/dt/**/*.json");
  }
  public String getMigrationRegex() {
    return withRegex("**/migration/**/*.json");
  }

  protected String withRegex(String exp) {
    return value + assetFolder + exp;
  }
  public boolean isGit() {
    return git;
  }
  public String getValue() {
    return value;
  }
}
