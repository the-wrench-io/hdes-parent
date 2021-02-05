package io.resys.hdes.pm.quarkus.runtime;

/*-
 * #%L
 * hdes-projects-quarkus
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

import java.util.function.Supplier;

import org.eclipse.microprofile.jwt.JsonWebToken;

import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.SecurityIdentityAugmentor;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;


public class HdesProjectSecurityAugmentor implements SecurityIdentityAugmentor {

  public static final String ADMIN_INIT = "hdes-projects-admin";
  private final String adminInitUserName;
  
  public HdesProjectSecurityAugmentor(String adminInitUserName) {
    super();
    this.adminInitUserName = adminInitUserName;
  }
  
  @Override
  public Uni<SecurityIdentity> augment(SecurityIdentity identity, AuthenticationRequestContext context) {
    if(identity.isAnonymous()) {
      return Uni.createFrom().item(identity);
    }
    return Uni.createFrom().item(build(identity));
  }

  private Supplier<SecurityIdentity> build(SecurityIdentity identity) {
    // create a new builder and copy principal, attributes, credentials and roles
    // from the original identity
    QuarkusSecurityIdentity.Builder builder = QuarkusSecurityIdentity.builder(identity);

    if(identity.getPrincipal() instanceof JsonWebToken) {
      JsonWebToken webToken = (JsonWebToken) identity.getPrincipal();
      String userName = webToken.getClaim("user_name");
      if(userName != null && userName.equals(adminInitUserName)) {
        builder.addRole(ADMIN_INIT);
      }
    }

    return builder::build;
  }
}
