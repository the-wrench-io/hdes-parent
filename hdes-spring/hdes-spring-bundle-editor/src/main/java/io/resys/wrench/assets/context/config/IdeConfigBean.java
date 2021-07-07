package io.resys.wrench.assets.context.config;

import org.springframework.beans.factory.annotation.Value;

/*-
 * #%L
 * wrench-component-assets-flow
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

@ConfigurationProperties(prefix="wrench.assets.ide")
public class IdeConfigBean {

  @Value("${id:wrench}")
  private String id;

  @Value("${contextPath:/ide}")
  private String contextPath;

  @Value("${enabled:true}")
  private String enabled;

  @Value("${redirect:true}")
  private String redirect;

  private String proto;

  private String host;
  private String index;

  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getContextPath() {
    return contextPath;
  }
  public void setContextPath(String contextPath) {
    this.contextPath = contextPath;
  }
  public String getEnabled() {
    return enabled;
  }
  public void setEnabled(String enabled) {
    this.enabled = enabled;
  }
  public String getRedirect() {
    return redirect;
  }
  public void setRedirect(String redirect) {
    this.redirect = redirect;
  }
  public String getIndex() {
    return index;
  }
  public void setIndex(String index) {
    this.index = index;
  }

  public String getProto() {
    return proto;
  }

  public void setProto(String proto) {
    this.proto = proto;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }
}
