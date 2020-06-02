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

import java.util.Collection;
import java.util.List;

import javax.enterprise.inject.spi.CDI;

import io.quarkus.arc.Arc;
import io.resys.hdes.backend.api.HdesBackend;
import io.resys.hdes.backend.api.HdesBackend.Def;
import io.resys.hdes.backend.api.HdesBackend.DefChangeEntry;
import io.resys.hdes.backend.api.HdesBackend.DefCreateEntry;
import io.resys.hdes.backend.api.HdesBackend.DefDeleteEntry;
import io.resys.hdes.ui.quarkus.runtime.HdesHandlerHelper;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class HdesDefsHandler implements Handler<RoutingContext> {
  
  @Override
  public void handle(RoutingContext event) {
    boolean active = HdesHandlerHelper.active();
    try {
      HttpServerResponse response = event.response();
      HdesBackend backend = CDI.current().select(HdesBackend.class).get();
  
      switch (event.request().method()) {
      case GET:
        Collection<Def> defs = backend.query().find();
        response.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
        response.end(Buffer.buffer(backend.writer().build(defs)));    
        break;
        
      case DELETE:
        DefDeleteEntry toDelete = backend.reader().build(event.getBody().getBytes(), DefDeleteEntry.class);
        List<Def> deleted = backend.delete().entry(toDelete).build();
        response.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
        response.end(Buffer.buffer(backend.writer().build(deleted)));
        break;
        
      case POST:
        List<DefCreateEntry> create = backend.reader().list(event.getBody().getBytes(), DefCreateEntry.class);
        List<Def> created = backend.builder().add(create).build();
        response.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
        response.end(Buffer.buffer(backend.writer().build(created)));
        break;
        
      case PUT:
        DefChangeEntry change = backend.reader().build(event.getBody().getBytes(), DefChangeEntry.class);
        Def changed = backend.change().add(change).build();
        response.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
        response.end(Buffer.buffer(backend.writer().build(changed)));
        break;
      default:
        break;
      }
      
    } finally {
      if (active) {
        Arc.container().requestContext().terminate();
      }
    }
  }
}
