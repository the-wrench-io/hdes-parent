package io.resys.wrench.assets.controllers;

/*-
 * #%L
 * wrench-component-resource
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

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import io.resys.wrench.assets.context.config.IdeConfigBean;
import io.resys.wrench.assets.datatype.spi.util.FileUtils;

@Controller
public class IndexController {

  private final IdeConfigBean configBean;

  public IndexController(IdeConfigBean configBean) {
    super();
    this.configBean = configBean;
  }

  @RequestMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
  public String index() {
    return "redirect:/" + FileUtils.cleanPath(configBean.getContextPath()) + "/";
  }
}
