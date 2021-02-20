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

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = HdesResourceEditorProcessor.FEATURE_BUILD_ITEM)
public class HdesResourceEditorConfig {
  /**
   * projects UI path, anything except '/'
   */
  @ConfigItem(defaultValue = "/hdes-re/ui")
  String frontendPath;

  /**
   * projects services path, anything except '/'
   */
  @ConfigItem(defaultValue = "/hdes-re/rest-api")
  String backendPath;
  
  /**
   * Mongo DB connection URL
   */
  @ConfigItem
  String connectionUrl;
  
  /**
   * Mongo DB NAME
   */
  @ConfigItem(defaultValue = "RE")
  String dbName;  
  
  public String getProjects() {
    return backendPath + "/resources/projects";
  }
}
