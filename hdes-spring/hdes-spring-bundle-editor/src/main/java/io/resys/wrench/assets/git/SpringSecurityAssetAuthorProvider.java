package io.resys.wrench.assets.git;

/*-
 * #%L
 * wrench-assets-bundle
 * %%
 * Copyright (C) 2016 - 2018 Copyright 2016 ReSys OÜ
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

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;

import io.resys.wrench.assets.bundle.spi.store.git.AssetAuthorProvider;
import io.resys.wrench.assets.context.config.GitConfigBean;

public class SpringSecurityAssetAuthorProvider implements AssetAuthorProvider {

  private final GitConfigBean gitConfigBean;

  public SpringSecurityAssetAuthorProvider(GitConfigBean gitConfigBean) {
    this.gitConfigBean = gitConfigBean;
  }

  @Override
  public AssetAuthorProvider.Author get() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();
    String email = gitConfigBean.getEmail();
    String name = null;
    if(authentication != null) {
      Object principal  = authentication.getPrincipal();
      if (principal instanceof OAuth2User) {
        OAuth2User user = (OAuth2User) principal;
        Map<String, Object> decodedDetails = user.getAttributes();
        email = (String) decodedDetails.get("email");
        name = StringUtils.defaultIfBlank((String) decodedDetails.get("given_name"), "") + " " + StringUtils.defaultIfBlank((String) decodedDetails.get("family_name"), "");
      }
    }
    if (StringUtils.isBlank(name) && email.contains("@")) {
      name = email.split("@")[0];
    }
    return new AssetAuthorProvider.Author(name, email);
  }
}
