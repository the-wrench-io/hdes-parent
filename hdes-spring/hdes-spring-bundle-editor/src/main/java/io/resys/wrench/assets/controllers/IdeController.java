package io.resys.wrench.assets.controllers;

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

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import io.resys.wrench.assets.context.config.IdeConfigBean;
import io.resys.wrench.assets.controllers.beans.AdminUIConfig;
import io.resys.wrench.assets.controllers.beans.AdminUIConfig.Csrf;
import io.resys.wrench.assets.controllers.config.IdeIndexConfigBean;
import io.resys.wrench.assets.datatype.spi.util.FileUtils;

@Controller
public class IdeController {
  private static final Logger LOGGER = LoggerFactory.getLogger(IdeController.class);

  @Value("${server.servlet.context-path}")
  private String contextPath;
  @Value(ControllerUtil.ASSET_CONTEXT_PATH)
  private String assetContextPath;
  
  private final IdeIndexConfigBean indexConfig;
  private final IdeConfigBean uiConfigBean;

  public IdeController(IdeConfigBean uiConfigBean, IdeIndexConfigBean indexConfig) {
    this.uiConfigBean = uiConfigBean;
    this.indexConfig = indexConfig;
  }

  @RequestMapping(value = "${wrench.assets.ide.contextPath:/ide}", produces = MediaType.TEXT_HTML_VALUE)
  public String wrench(
      CsrfToken csrfToken,
      Model model,
      @RequestHeader(value = "Host", required = false) String host,
      @RequestHeader(value = "X-Forwarded-Proto", required = false, defaultValue = "") String proto) {

    String restUrl = ControllerUtil.getRestUrl(
            firstNonNull(uiConfigBean.getProto(), proto),
            firstNonNull(uiConfigBean.getHost(), host),
            assetContextPath,
            contextPath);
    
    AdminUIConfig config = new AdminUIConfig()
        .setId(uiConfigBean.getId())
        .setIndex(uiConfigBean.getIndex())
        .setContextPath("/" + FileUtils.cleanPath(uiConfigBean.getContextPath()))
        
        .setManifest(indexConfig.getManifest())
        .setCss(indexConfig.getCss())
        .setMainJs(indexConfig.getMainJs())
        .setJs(indexConfig.getJs())
        .setConfigJs(indexConfig.getConfigJs())
        
        .setUrl(restUrl)
        .setHash(indexConfig.getHash())
        .setCsrf(csrfToken == null ? null : new Csrf().setKey(csrfToken.getHeaderName()).setValue(csrfToken.getToken()));

    model.addAttribute("config", config);
    return "wrench-ide";
  }
}
