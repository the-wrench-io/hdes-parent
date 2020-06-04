package io.resys.hdes.ui.quarkus.runtime.handlers;
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

import javax.enterprise.inject.spi.CDI;

import io.quarkus.arc.Arc;
import io.resys.hdes.backend.api.HdesBackend;
import io.resys.hdes.ui.quarkus.runtime.HdesHandlerHelper;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class HdesStatusHandler implements Handler<RoutingContext> {
  
  @Override
  public void handle(RoutingContext event) {
    boolean active = HdesHandlerHelper.active();
    try {
      var backend = CDI.current().select(HdesBackend.class).get();
      var status = backend.status();
      
      HttpServerResponse response = event.response();
      response.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
      response.end(Buffer.buffer(backend.writer().build(status)));
      
    } finally {
      if (active) {
        Arc.container().requestContext().terminate();
      }
    }
  }
}