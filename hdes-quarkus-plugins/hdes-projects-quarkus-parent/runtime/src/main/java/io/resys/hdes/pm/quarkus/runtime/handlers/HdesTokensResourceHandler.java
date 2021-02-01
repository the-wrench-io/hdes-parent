package io.resys.hdes.pm.quarkus.runtime.handlers;
/*-
 * #%L
 * hdes-ui-quarkus
 * %%
 * Copyright (C) 2020 Copyright 2020 ReSys OÜ
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

import java.util.Optional;

import io.resys.hdes.pm.quarkus.runtime.context.HdesProjectsContext;
import io.resys.hdes.projects.api.PmRepository.TokenResource;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class HdesTokensResourceHandler extends HdesResourceHandler {

  @Override
  protected void handleResource(RoutingContext event, HttpServerResponse response, HdesProjectsContext ctx) {
    switch (event.request().method()) {
    case GET:
      
      String token = event.request().getParam("id");
      Optional<TokenResource> defs = ctx.repo().query().tokens().findOne(token);
      response.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
      if(defs.isPresent()) {
        response.end(Buffer.buffer(ctx.writer().build(defs.get())));
        return;
      }
      
      catch404(token, ctx, response);
      break;
    default:
      break;
    }
  }
}
