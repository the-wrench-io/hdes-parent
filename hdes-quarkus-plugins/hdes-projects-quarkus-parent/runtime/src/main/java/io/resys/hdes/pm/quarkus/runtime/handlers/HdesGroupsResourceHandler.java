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

import io.resys.hdes.pm.quarkus.runtime.context.HdesProjectsContext;
import io.resys.hdes.projects.api.ImmutableBatchGroup;
import io.resys.hdes.projects.api.ImmutableGroup;
import io.resys.hdes.projects.api.PmRepository.BatchGroup;
import io.resys.hdes.projects.api.PmRepository.Group;
import io.resys.hdes.projects.api.PmRepository.GroupResource;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class HdesGroupsResourceHandler extends HdesResourceHandler {

  @Override
  protected void handleResource(RoutingContext event, HttpServerResponse response, HdesProjectsContext ctx) {
    switch (event.request().method()) {
    case GET:
      Collection<GroupResource> defs = ctx.repo().query().groups().find();
      response.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
      response.end(Buffer.buffer(ctx.writer().build(defs)));
      break;

    case DELETE:
      Group toDelete = ctx.reader().build(event.getBody().getBytes(), ImmutableGroup.class);
      Group deleted = ctx.repo().delete().group(toDelete.getId(), toDelete.getRev());
      response.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
      response.end(Buffer.buffer(ctx.writer().build(deleted)));
      break;

    case POST:
      BatchGroup create = ctx.reader().build(event.getBody().getBytes(), ImmutableBatchGroup.class);
      GroupResource created = ctx.repo().create().group(create);
      response.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
      response.end(Buffer.buffer(ctx.writer().build(created)));
      break;

    case PUT:
      BatchGroup update = ctx.reader().build(event.getBody().getBytes(), ImmutableBatchGroup.class);
      GroupResource updated = ctx.repo().update().group(update);
      response.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
      response.end(Buffer.buffer(ctx.writer().build(updated)));
      break;
    default:
      break;
    }
  }
}
