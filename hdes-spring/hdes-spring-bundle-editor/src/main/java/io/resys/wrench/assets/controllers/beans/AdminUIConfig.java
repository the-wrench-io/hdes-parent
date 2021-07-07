package io.resys.wrench.assets.controllers.beans;

import java.util.List;

/*-
 * #%L
 * wrench-assets-bundle
 * %%
 * Copyright (C) 2016 - 2021 Copyright 2020 ReSys OÜ
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

public class AdminUIConfig {

  private String id;
  private String contextPath;
  private String index;
  private String hash;
  private String js;
  private String mainJs;
  private String configJs;
  private List<String> css;
  private String manifest;
  private Csrf csrf;
  private String url;

  public String getContextPath() {
    return contextPath;
  }
  public AdminUIConfig setContextPath(String contextPath) {
    this.contextPath = contextPath;
    return this;
  }
  public String getId() {
    return id;
  }
  public AdminUIConfig setId(String id) {
    this.id = id;
    return this;
  }
  public Csrf getCsrf() {
    return csrf;
  }
  public AdminUIConfig setCsrf(Csrf csrf) {
    this.csrf = csrf;
    return this;
  }
  public String getIndex() {
    return index;
  }
  public AdminUIConfig setIndex(String index) {
    this.index = index;
    return this;
  }
  public String getHash() {
    return hash;
  }
  public AdminUIConfig setHash(String hash) {
    this.hash = hash;
    return this;
  }
  public String getUrl() {
    return url;
  }
  public AdminUIConfig setUrl(String url) {
    this.url = url;
    return this;
  }
  public static class Csrf {
    private String key;
    private String value;
    public String getKey() {
      return key;
    }
    public Csrf setKey(String key) {
      this.key = key;
      return this;
    }
    public String getValue() {
      return value;
    }
    public Csrf setValue(String value) {
      this.value = value;
      return this;
    }
  }

  public String getJs() {
    return js;
  }
  public AdminUIConfig setJs(String js) {
    this.js = js;
    return this;
  }
  public String getMainJs() {
    return mainJs;
  }
  public AdminUIConfig setMainJs(String mainJs) {
    this.mainJs = mainJs;
    return this;
  }
  public String getConfigJs() {
    return configJs;
  }
  public AdminUIConfig setConfigJs(String configJs) {
    this.configJs = configJs;
    return this;
  }
  public List<String> getCss() {
    return css;
  }
  public AdminUIConfig setCss(List<String> css) {
    this.css = css;
    return this;
  }
  public String getManifest() {
    return manifest;
  }
  public AdminUIConfig setManifest(String manifest) {
    this.manifest = manifest;
    return this;
  }
}
