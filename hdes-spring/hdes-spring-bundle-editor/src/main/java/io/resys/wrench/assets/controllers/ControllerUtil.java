package io.resys.wrench.assets.controllers;

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

import org.apache.commons.lang3.StringUtils;

import io.resys.wrench.assets.datatype.spi.util.FileUtils;

public class ControllerUtil {

  public static final String ASSET_CONTEXT_PATH = "${wrench.assets.ide.services:/assets}";
  
  private static String getContextPath(String serverContextPath) {
    String cp = "";
    if (StringUtils.isNotBlank(serverContextPath)) {
      if (!serverContextPath.startsWith("/")) {
        cp = "/";
      }
      cp = cp + serverContextPath;
      if (cp.endsWith("/")) {
        cp = cp.substring(0, cp.length() - 1);
      }
    }
    return cp;
  }

  private static String getUrl(String proto, String host, String serverContextPath) {
    final String contextPath = getContextPath(serverContextPath);
    if (StringUtils.isBlank(proto)) {
      proto = "http";
    }
    if (!proto.endsWith(":")) {
      proto = proto + ":";
    }
    String baseUrl = proto + "//" + host + contextPath;
    return baseUrl;
  }

  public static String getRestUrl(String proto, String host, String apiContextPath, String serverContextPath) {
    return FileUtils.cleanPath(getUrl(proto, host, serverContextPath)) + "/" + FileUtils.cleanPath(apiContextPath) + "/";
  }
}
