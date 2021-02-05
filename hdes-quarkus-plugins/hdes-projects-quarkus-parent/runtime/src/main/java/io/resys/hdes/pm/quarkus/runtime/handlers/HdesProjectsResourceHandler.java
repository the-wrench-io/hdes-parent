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

import java.util.Collection;

import io.quarkus.security.identity.CurrentIdentityAssociation;
import io.quarkus.vertx.http.runtime.CurrentVertxRequest;
import io.resys.hdes.pm.quarkus.runtime.context.HdesProjectsContext;
import io.resys.hdes.projects.api.ImmutableBatchProject;
import io.resys.hdes.projects.api.ImmutableProject;
import io.resys.hdes.projects.api.PmRepository.BatchProject;
import io.resys.hdes.projects.api.PmRepository.Project;
import io.resys.hdes.projects.api.PmRepository.ProjectResource;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class HdesProjectsResourceHandler extends HdesResourceHandler {

  public HdesProjectsResourceHandler(
      CurrentIdentityAssociation currentIdentityAssociation,
      CurrentVertxRequest currentVertxRequest) {
    super(currentIdentityAssociation, currentVertxRequest);
  }
  
  @Override
  protected void handleResource(RoutingContext event, HttpServerResponse response, HdesProjectsContext ctx) {

    switch (event.request().method()) {
    case GET:
      Collection<ProjectResource> defs = ctx.repo().query().project().find();
      response.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
      response.end(Buffer.buffer(ctx.writer().build(defs)));
      break;

    case DELETE:
      Project toDelete = ctx.reader().build(event.getBody().getBytes(), ImmutableProject.class);
      Project deleted = ctx.repo().delete().project(toDelete.getId(), toDelete.getRev());
      response.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
      response.end(Buffer.buffer(ctx.writer().build(deleted)));
      break;

    case POST:
      BatchProject create = ctx.reader().build(event.getBody().getBytes(), ImmutableBatchProject.class);
      ProjectResource created = ctx.repo().create().project(create);
      response.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
      response.end(Buffer.buffer(ctx.writer().build(created)));
      break;

    case PUT:
      BatchProject update = ctx.reader().build(event.getBody().getBytes(), ImmutableBatchProject.class);
      ProjectResource updated = ctx.repo().update().project(update);
      response.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
      response.end(Buffer.buffer(ctx.writer().build(updated)));
      break;
    default:
      break;
    }
  }
}
